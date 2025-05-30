package com.translator.minecraft;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TranslateChat extends JavaPlugin implements Listener {

    private Map<UUID, String> playerLanguages;
    private File languagesFile;
    private FileConfiguration languagesConfig;
    private TranslationAPI translationAPI;
    private LanguageSelectionGUI languageSelectionGUI;

    @Override
    public void onEnable() {
        // Salvar configuração padrão se não existir
        saveDefaultConfig();
        
        // Inicializar mapa de idiomas dos jogadores
        playerLanguages = new HashMap<>();
        
        // Inicializar API de tradução
        translationAPI = new TranslationAPI(this);
        
        // Carregar idiomas dos jogadores
        loadPlayerLanguages();
        
        // Inicializar interface gráfica
        languageSelectionGUI = new LanguageSelectionGUI(this);
        
        // Registrar eventos
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new ChatListener(this), this);
        
        // Registrar comandos
        getCommand("language").setExecutor(new LanguageCommand(this));
        getCommand("languages").setExecutor(new LanguagesCommand(this));
        
        getLogger().info("TranslateChat ativado com sucesso!");
    }

    @Override
    public void onDisable() {
        // Salvar idiomas dos jogadores
        savePlayerLanguages();
        
        getLogger().info("TranslateChat desativado com sucesso!");
    }
    
    /**
     * Carrega os idiomas dos jogadores do arquivo de configuração
     */
    private void loadPlayerLanguages() {
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
        
        // Carregar idiomas dos jogadores
        if (languagesConfig.contains("players")) {
            for (String uuidString : languagesConfig.getConfigurationSection("players").getKeys(false)) {
                UUID uuid = UUID.fromString(uuidString);
                String language = languagesConfig.getString("players." + uuidString);
                playerLanguages.put(uuid, language);
            }
        }
    }
    
    /**
     * Salva os idiomas dos jogadores no arquivo de configuração
     */
    public void savePlayerLanguages() {
        if (languagesConfig == null || languagesFile == null) {
            return;
        }
        
        // Salvar idiomas dos jogadores
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
     * @param language Código do idioma
     */
    public void setPlayerLanguage(Player player, String language) {
        playerLanguages.put(player.getUniqueId(), language);
        savePlayerLanguages();
    }
    
    /**
     * Obtém o idioma de um jogador
     * @param player Jogador
     * @return Código do idioma ou idioma padrão se não definido
     */
    public String getPlayerLanguage(Player player) {
        return playerLanguages.getOrDefault(player.getUniqueId(), getConfig().getString("default-language", "pt-br"));
    }
    
    /**
     * Verifica se um idioma está disponível
     * @param language Código do idioma
     * @return true se o idioma estiver disponível
     */
    public boolean isLanguageAvailable(String language) {
        return getConfig().getStringList("available-languages").contains(language);
    }
    
    /**
     * Obtém a lista de idiomas disponíveis
     * @return Lista de idiomas disponíveis
     */
    public java.util.List<String> getAvailableLanguages() {
        return getConfig().getStringList("available-languages");
    }
    
    /**
     * Obtém a API de tradução
     * @return API de tradução
     */
    public TranslationAPI getTranslationAPI() {
        return translationAPI;
    }
    
    /**
     * Obtém a interface gráfica de seleção de idioma
     * @return Interface gráfica de seleção de idioma
     */
    public LanguageSelectionGUI getLanguageSelectionGUI() {
        return languageSelectionGUI;
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        // Se o jogador não tiver um idioma definido, mostrar menu de seleção
        if (!playerLanguages.containsKey(player.getUniqueId())) {
            // Agendar para mostrar o menu após 2 segundos (para garantir que o jogador já carregou completamente)
            getServer().getScheduler().runTaskLater(this, () -> {
                // Abrir interface de seleção de idioma
                if (getConfig().getBoolean("language-selection.enabled", true)) {
                    languageSelectionGUI.openLanguageSelection(player);
                } else {
                    player.sendMessage(getConfig().getString("messages.prefix") + 
                        "§eBem-vindo! Use /language <idioma> para selecionar seu idioma.");
                }
            }, 40L); // 40 ticks = 2 segundos
        }
    }
}
