package com.translator.minecraft;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class LanguageSelectionGUI implements Listener {

    private final TranslateChat plugin;
    private final String inventoryTitle = "§6Selecione seu idioma";

    public LanguageSelectionGUI(TranslateChat plugin) {
        this.plugin = plugin;
    }

    /**
     * Abre a GUI de seleção de idioma para um jogador
     * @param player Jogador para abrir a GUI
     */
    public void openGUI(Player player) {
        // Obter a lista de idiomas disponíveis
        Map<String, String> availableLanguages = plugin.getAvailableLanguages();
        
        // Calcular o tamanho do inventário (múltiplo de 9)
        int size = (int) Math.ceil(availableLanguages.size() / 9.0) * 9;
        size = Math.max(9, Math.min(54, size)); // Mínimo 9, máximo 54
        
        // Criar o inventário
        Inventory inventory = Bukkit.createInventory(null, size, inventoryTitle);
        
        // Adicionar os itens de idioma
        int slot = 0;
        for (Map.Entry<String, String> entry : availableLanguages.entrySet()) {
            String languageCode = entry.getKey();
            String languageName = entry.getValue();
            
            // Criar o item
            ItemStack item = createLanguageItem(languageCode, languageName, player);
            
            // Adicionar ao inventário
            inventory.setItem(slot++, item);
        }
        
        // Abrir o inventário para o jogador
        player.openInventory(inventory);
    }
    
    /**
     * Cria um item para representar um idioma na GUI
     * @param languageCode Código do idioma
     * @param languageName Nome do idioma
     * @param player Jogador para verificar o idioma atual
     * @return ItemStack representando o idioma
     */
    private ItemStack createLanguageItem(String languageCode, String languageName, Player player) {
        // Determinar o material do item
        Material material = Material.PAPER;
        
        // Verificar se é o idioma atual do jogador
        boolean isCurrentLanguage = plugin.getPlayerLanguage(player).equalsIgnoreCase(languageCode);
        
        if (isCurrentLanguage) {
            material = Material.ENCHANTED_BOOK;
        }
        
        // Criar o item
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        
        // Definir o nome e a descrição
        meta.setDisplayName("§e" + languageName);
        
        List<String> lore = new ArrayList<>();
        lore.add("§7Código: §f" + languageCode);
        
        if (isCurrentLanguage) {
            lore.add("§aIdioma atual");
        } else {
            lore.add("§7Clique para selecionar");
        }
        
        meta.setLore(lore);
        item.setItemMeta(meta);
        
        return item;
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // Verificar se o inventário é a GUI de seleção de idioma
        if (!event.getView().getTitle().equals(inventoryTitle)) {
            return;
        }
        
        // Cancelar o evento para evitar que o jogador pegue o item
        event.setCancelled(true);
        
        // Verificar se o clique foi em um slot válido
        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) {
            return;
        }
        
        // Obter o jogador
        Player player = (Player) event.getWhoClicked();
        
        // Obter o nome do item
        String itemName = event.getCurrentItem().getItemMeta().getDisplayName();
        
        // Encontrar o código do idioma correspondente
        String selectedLanguage = null;
        
        for (Map.Entry<String, String> entry : plugin.getAvailableLanguages().entrySet()) {
            if (("§e" + entry.getValue()).equals(itemName)) {
                selectedLanguage = entry.getKey();
                break;
            }
        }
        
        // Verificar se o idioma foi encontrado
        if (selectedLanguage == null) {
            return;
        }
        
        // Verificar se o idioma está disponível
        if (!plugin.isLanguageAvailable(selectedLanguage)) {
            player.sendMessage("§cIdioma não disponível: " + selectedLanguage);
            return;
        }
        
        // Definir o idioma do jogador
        plugin.setPlayerLanguage(player, selectedLanguage);
        
        // Informar o jogador
        player.sendMessage("§aIdioma definido para: §e" + selectedLanguage);
        
        // Fechar o inventário
        player.closeInventory();
    }
}
