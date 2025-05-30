package com.translator.minecraft;

import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class TranslationAPI {

    private final TranslateChat plugin;
    private final String apiUrl = "https://translate.marcosbrendon.com/translate";
    private final JSONParser jsonParser = new JSONParser();
    
    // Cache para armazenar traduções recentes
    private final Map<String, CacheEntry> translationCache;
    
    // Tempo padrão de expiração do cache em minutos
    private final long cacheExpirationTime;

    public TranslationAPI(TranslateChat plugin) {
        this.plugin = plugin;
        this.translationCache = new ConcurrentHashMap<>();
        
        // Obter tempo de expiração do cache da configuração ou usar valor padrão
        this.cacheExpirationTime = plugin.getConfig().getLong("translation-cache.expiration-minutes", 30);
        
        // Agendar limpeza periódica do cache
        scheduleCacheCleanup();
    }
    
    /**
     * Agenda limpeza periódica do cache para remover entradas expiradas
     */
    private void scheduleCacheCleanup() {
        long cleanupInterval = plugin.getConfig().getLong("translation-cache.cleanup-interval-minutes", 10);
        
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            long now = System.currentTimeMillis();
            translationCache.entrySet().removeIf(entry -> 
                (now - entry.getValue().getTimestamp()) > TimeUnit.MINUTES.toMillis(cacheExpirationTime));
            
            plugin.getLogger().info("Limpeza do cache de traduções concluída. Entradas restantes: " + translationCache.size());
        }, 20 * 60, 20 * 60 * cleanupInterval); // Converter minutos para ticks (20 ticks = 1 segundo)
    }

    /**
     * Traduz um texto de um idioma para outro de forma assíncrona
     * @param text Texto a ser traduzido
     * @param sourceLanguage Idioma de origem
     * @param targetLanguage Idioma de destino
     * @return CompletableFuture com o texto traduzido
     */
    public CompletableFuture<String> translate(String text, String sourceLanguage, String targetLanguage) {
        // Se os idiomas forem iguais, não precisa traduzir
        if (sourceLanguage.equalsIgnoreCase(targetLanguage)) {
            return CompletableFuture.completedFuture(text);
        }
        
        // Criar chave para o cache
        String cacheKey = createCacheKey(text, sourceLanguage, targetLanguage);
        
        // Verificar se a tradução está no cache
        CacheEntry cachedTranslation = translationCache.get(cacheKey);
        if (cachedTranslation != null) {
            // Atualizar timestamp para manter a entrada "fresca"
            cachedTranslation.updateTimestamp();
            plugin.getLogger().info("Tradução encontrada no cache: " + cacheKey);
            return CompletableFuture.completedFuture(cachedTranslation.getTranslation());
        }

        return CompletableFuture.supplyAsync(() -> {
            try {
                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");
                connection.setDoOutput(true);

                // Criar o JSON para enviar
                JSONObject requestBody = new JSONObject();
                requestBody.put("q", text);
                requestBody.put("source", sourceLanguage);
                requestBody.put("target", targetLanguage);

                // Enviar a requisição
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = requestBody.toJSONString().getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                // Obter a resposta
                if (connection.getResponseCode() == 200) {
                    // Ler a resposta
                    java.util.Scanner scanner = new java.util.Scanner(connection.getInputStream(), StandardCharsets.UTF_8.name());
                    String responseBody = scanner.useDelimiter("\\A").next();
                    scanner.close();

                    // Parsear a resposta JSON
                    JSONObject responseJson = (JSONObject) jsonParser.parse(responseBody);
                    String translatedText = (String) responseJson.get("translatedText");
                    
                    // Armazenar no cache
                    translationCache.put(cacheKey, new CacheEntry(translatedText));
                    plugin.getLogger().info("Nova tradução adicionada ao cache: " + cacheKey);
                    
                    return translatedText;
                } else {
                    plugin.getLogger().warning("Erro ao traduzir texto. Código de resposta: " + connection.getResponseCode());
                    return text; // Retorna o texto original em caso de erro
                }
            } catch (IOException | ParseException e) {
                plugin.getLogger().warning("Erro ao traduzir texto: " + e.getMessage());
                return text; // Retorna o texto original em caso de erro
            }
        });
    }

    /**
     * Cria uma chave única para o cache baseada no texto e idiomas
     * @param text Texto a ser traduzido
     * @param sourceLanguage Idioma de origem
     * @param targetLanguage Idioma de destino
     * @return Chave para o cache
     */
    private String createCacheKey(String text, String sourceLanguage, String targetLanguage) {
        return sourceLanguage + ":" + targetLanguage + ":" + text.hashCode();
    }
    
    /**
     * Limpa o cache de traduções
     */
    public void clearCache() {
        translationCache.clear();
        plugin.getLogger().info("Cache de traduções limpo");
    }
    
    /**
     * Obtém o tamanho atual do cache
     * @return Número de entradas no cache
     */
    public int getCacheSize() {
        return translationCache.size();
    }
    
    /**
     * Classe interna para armazenar entradas do cache com timestamp
     */
    private static class CacheEntry {
        private final String translation;
        private long timestamp;
        
        public CacheEntry(String translation) {
            this.translation = translation;
            this.timestamp = System.currentTimeMillis();
        }
        
        public String getTranslation() {
            return translation;
        }
        
        public long getTimestamp() {
            return timestamp;
        }
        
        public void updateTimestamp() {
            this.timestamp = System.currentTimeMillis();
        }
    }
}
