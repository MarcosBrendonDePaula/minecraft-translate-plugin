package com.translator.minecraft;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LanguageSelectionGUI implements Listener {

    private final TranslateChat plugin;
    private final Map<String, Material> languageIcons;

    public LanguageSelectionGUI(TranslateChat plugin) {
        this.plugin = plugin;
        this.languageIcons = new HashMap<>();
        
        // Configurar ícones para cada idioma
        setupLanguageIcons();
        
        // Registrar eventos
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    /**
     * Configura os ícones para cada idioma
     */
    private void setupLanguageIcons() {
        // Ícones padrão para idiomas comuns
        languageIcons.put("pt-br", Material.YELLOW_WOOL);
        languageIcons.put("en", Material.RED_WOOL);
        languageIcons.put("es", Material.ORANGE_WOOL);
        languageIcons.put("fr", Material.BLUE_WOOL);
        languageIcons.put("de", Material.BLACK_WOOL);
        languageIcons.put("it", Material.GREEN_WOOL);
        languageIcons.put("ja", Material.WHITE_WOOL);
        languageIcons.put("ko", Material.LIGHT_BLUE_WOOL);
        languageIcons.put("ru", Material.RED_WOOL);
        languageIcons.put("zh-cn", Material.PINK_WOOL);
        
        // Adicionar mais idiomas conforme necessário
    }
    
    /**
     * Abre a interface de seleção de idioma para um jogador
     * @param player Jogador para abrir a interface
     */
    public void openLanguageSelection(Player player) {
        // Verificar se a interface está habilitada
        if (!plugin.getConfig().getBoolean("language-selection.enabled", true)) {
            player.sendMessage(plugin.getConfig().getString("messages.prefix") + 
                "§cInterface de seleção de idioma desabilitada. Use /language <idioma>.");
            return;
        }
        
        // Obter configurações da interface
        String title = plugin.getConfig().getString("language-selection.title", "Selecione seu idioma");
        int rows = plugin.getConfig().getInt("language-selection.rows", 3);
        
        // Criar inventário
        Inventory inventory = Bukkit.createInventory(null, rows * 9, title);
        
        // Adicionar itens para cada idioma disponível
        List<String> availableLanguages = plugin.getAvailableLanguages();
        String currentLanguage = plugin.getPlayerLanguage(player);
        
        for (int i = 0; i < availableLanguages.size() && i < inventory.getSize(); i++) {
            String language = availableLanguages.get(i);
            
            // Obter ícone para o idioma (ou usar um padrão)
            Material icon = languageIcons.getOrDefault(language, Material.PAPER);
            
            // Criar item
            ItemStack item = new ItemStack(icon);
            ItemMeta meta = item.getItemMeta();
            
            // Definir nome e descrição
            meta.setDisplayName("§f" + language);
            
            // Adicionar descrição
            if (language.equals(currentLanguage)) {
                meta.setLore(Arrays.asList("§aIdioma atual", "§7Clique para selecionar"));
            } else {
                meta.setLore(Arrays.asList("§7Clique para selecionar"));
            }
            
            item.setItemMeta(meta);
            
            // Adicionar ao inventário
            inventory.setItem(i, item);
        }
        
        // Abrir inventário para o jogador
        player.openInventory(inventory);
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // Verificar se é o inventário de seleção de idioma
        String title = plugin.getConfig().getString("language-selection.title", "Selecione seu idioma");
        if (!event.getView().getTitle().equals(title)) {
            return;
        }
        
        // Cancelar o evento para evitar que o jogador pegue o item
        event.setCancelled(true);
        
        // Verificar se o clique foi em um item válido
        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) {
            return;
        }
        
        // Obter o jogador
        Player player = (Player) event.getWhoClicked();
        
        // Obter o idioma selecionado
        String language = event.getCurrentItem().getItemMeta().getDisplayName().substring(2); // Remover o §f
        
        // Verificar se o idioma é válido
        if (!plugin.isLanguageAvailable(language)) {
            player.sendMessage(plugin.getConfig().getString("messages.prefix") + 
                plugin.getConfig().getString("messages.language-not-available"));
            return;
        }
        
        // Definir o idioma do jogador
        plugin.setPlayerLanguage(player, language);
        
        // Enviar mensagem de confirmação
        String message = plugin.getConfig().getString("messages.prefix") + 
            plugin.getConfig().getString("messages.language-changed").replace("%language%", language);
        player.sendMessage(message);
        
        // Fechar o inventário
        player.closeInventory();
    }
}
