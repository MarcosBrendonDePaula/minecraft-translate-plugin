package com.translator.minecraft;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.translator.minecraft.chat.ChatManager;
import com.translator.minecraft.chat.ChatMode;
import com.translator.minecraft.chat.commands.GlobalChatCommand;
import com.translator.minecraft.chat.commands.LocalChatCommand;
import com.translator.minecraft.chat.commands.PrivateMessageCommand;
import com.translator.minecraft.chat.commands.ReplyCommand;
import com.translator.minecraft.chat.commands.SystemMessageCommand;

public class TranslateChat extends JavaPlugin {

    private Map<UUID, String> playerLanguages;
    private File languagesFile;
    private FileConfiguration languagesConfig;
    private TranslationAPI translationAPI;
    private ChatManager chatManager;
    private LanguageSelectionGUI languageSelectionGUI;

    @Override
    public void onEnable() {
        // Salvar configuração padrão
        saveDefaultConfig();
        
        // Inicializar mapa de idiomas dos jogadores
        playerLanguages = new HashMap<>();
        
        // Carregar idiomas dos jogadores
        loadLanguages();
        
        // Inicializar API de tradução
        translationAPI = new TranslationAPI(this);
        
        // Inicializar gerenciador de chat
        chatManager = new ChatManager();
        
        // Inicializar GUI de seleção de idioma
        languageSelectionGUI = new LanguageSelectionGUI(this);
        
        // Registrar eventos
        getServer().getPluginManager().registerEvents(new ChatListener(this), this);
        getServer().getPluginManager().registerEvents(languageSelectionGUI, this);
        
        // Registrar comandos
        getCommand("language").setExecutor(new LanguageCommand(this));
        getCommand("languages").setExecutor(new LanguagesCommand(this));
        
        // Registrar novos comandos de chat
        getCommand("g").setExecutor(new GlobalChatCommand(this));
        getCommand("l").setExecutor(new LocalChatCommand(this));
        getCommand("msg").setExecutor(new PrivateMessageCommand(this));
        getCommand("r").setExecutor(new ReplyCommand(this));
        getCommand("broadcast").setExecutor(new SystemMessageCommand(this));
        
        getLogger().info("Plugin TranslateChat ativado com sucesso!");
    }

    @Override
    public void onDisable() {
        // Salvar idiomas dos jogadores
        saveLanguages();
        
        // Limpar cache de tradução
        translationAPI.clearCache();
        
        getLogger().info("Plugin TranslateChat desativado com sucesso!");
    }
    
    /**
     * Carrega os idiomas dos jogadores do arquivo
     */
    private void loadLanguages() {
        languagesFile = new File(getDataFolder(), "languages.yml");
        
        if (!languagesFile.exists()) {
            try {
                languagesFile.getParentFile().mkdirs();
                languagesFile.createNewFile();
            } catch (IOException e) {
                getLogger().severe("Não foi possível criar o arquivo languages.yml: " + e.getMessage());
            }
        }
        
        languagesConfig = YamlConfiguration.loadConfiguration(languagesFile);
        
        if (languagesConfig.contains("players")) {
            for (String uuidString : languagesConfig.getConfigurationSection("players").getKeys(false)) {
                UUID uuid = UUID.fromString(uuidString);
                String language = languagesConfig.getString("players." + uuidString);
                playerLanguages.put(uuid, language);
            }
        }
    }
    
    /**
     * Salva os idiomas dos jogadores no arquivo
     */
    public void saveLanguages() {
        if (languagesConfig == null || languagesFile == null) {
            return;
        }
        
        for (Map.Entry<UUID, String> entry : playerLanguages.entrySet()) {
            languagesConfig.set("players." + entry.getKey().toString(), entry.getValue());
        }
        
        try {
            languagesConfig.save(languagesFile);
        } catch (IOException e) {
            getLogger().severe("Não foi possível salvar o arquivo languages.yml: " + e.getMessage());
        }
    }
    
    /**
     * Define o idioma de um jogador
     * @param player Jogador
     * @param language Idioma
     */
    public void setPlayerLanguage(Player player, String language) {
        playerLanguages.put(player.getUniqueId(), language);
        saveLanguages();
    }
    
    /**
     * Obtém o idioma de um jogador
     * @param player Jogador
     * @return Idioma do jogador ou idioma padrão se não estiver definido
     */
    public String getPlayerLanguage(Player player) {
        return playerLanguages.getOrDefault(player.getUniqueId(), getConfig().getString("default-language", "en"));
    }
    
    /**
     * Processa e envia uma mensagem global para todos os jogadores online
     * @param sender Jogador que enviou a mensagem
     * @param message Mensagem a ser enviada
     * @param senderLanguage Idioma do remetente
     */
    public void processGlobalMessage(Player sender, String message, String senderLanguage) {
        // Enviar para todos os jogadores online, sem verificar distância ou mundo
        for (Player recipient : Bukkit.getOnlinePlayers()) {
            // Obter o idioma do destinatário
            String recipientLanguage = getPlayerLanguage(recipient);
            
            // Traduzir a mensagem do idioma do remetente para o idioma do destinatário
            CompletableFuture<String> translationFuture = translationAPI
                    .translate(message, senderLanguage, recipientLanguage);
            
            // Processar a tradução quando estiver pronta
            translationFuture.thenAccept(translatedMessage -> {
                // Construir o formato da mensagem
                String prefix = ChatMode.GLOBAL.getPrefix().replace("&", "§");
                String formattedMessage;
                
                // Se os idiomas forem diferentes, mostrar o idioma original
                if (!senderLanguage.equalsIgnoreCase(recipientLanguage)) {
                    formattedMessage = prefix + " §7[" + senderLanguage.toUpperCase() + "] §f" + 
                            sender.getDisplayName() + "§7: §f" + translatedMessage;
                } else {
                    formattedMessage = prefix + " §f" + sender.getDisplayName() + "§7: §f" + translatedMessage;
                }
                
                // Enviar a mensagem traduzida para o destinatário
                recipient.sendMessage(formattedMessage);
            });
        }
        
        // Log da mensagem no console
        getLogger().info("[CHAT] [GLOBAL] " + sender.getName() + ": " + message);
    }
    
    /**
     * Obtém a API de tradução
     * @return API de tradução
     */
    public TranslationAPI getTranslationAPI() {
        return translationAPI;
    }
    
    /**
     * Obtém o gerenciador de chat
     * @return Gerenciador de chat
     */
    public ChatManager getChatManager() {
        return chatManager;
    }
    
    /**
     * Obtém a GUI de seleção de idioma
     * @return GUI de seleção de idioma
     */
    public LanguageSelectionGUI getLanguageSelectionGUI() {
        return languageSelectionGUI;
    }
    
    /**
     * Obtém os idiomas disponíveis
     * @return Mapa de códigos de idioma para nomes de idioma
     */
    public Map<String, String> getAvailableLanguages() {
        Map<String, String> languages = new LinkedHashMap<>();
        
        if (getConfig().contains("available-languages")) {
            for (String code : getConfig().getConfigurationSection("available-languages").getKeys(false)) {
                String name = getConfig().getString("available-languages." + code);
                languages.put(code, name);
            }
        }
        
        return languages;
    }
    
    /**
     * Verifica se um idioma está disponível
     * @param language Código do idioma
     * @return true se o idioma estiver disponível
     */
    public boolean isLanguageAvailable(String language) {
        return getAvailableLanguages().containsKey(language);
    }
}
