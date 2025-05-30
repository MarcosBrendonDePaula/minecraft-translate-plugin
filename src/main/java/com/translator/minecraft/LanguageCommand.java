package com.translator.minecraft;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LanguageCommand implements CommandExecutor {

    private final TranslateChat plugin;

    public LanguageCommand(TranslateChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cEste comando só pode ser executado por jogadores.");
            return true;
        }

        Player player = (Player) sender;

        // Se não houver argumentos, mostrar o idioma atual e abrir a GUI
        if (args.length == 0) {
            String currentLanguage = plugin.getPlayerLanguage(player);
            player.sendMessage("§6Seu idioma atual: §e" + currentLanguage);
            player.sendMessage("§7Use /language <idioma> para definir seu idioma ou selecione na GUI.");
            
            // Abrir a GUI de seleção de idioma
            plugin.getLanguageSelectionGUI().openGUI(player);
            return true;
        }

        // Se houver argumentos, definir o idioma
        String language = args[0].toLowerCase();

        // Verificar se o idioma está disponível
        if (!plugin.isLanguageAvailable(language)) {
            player.sendMessage("§cIdioma não disponível: " + language);
            player.sendMessage("§7Use /languages para ver os idiomas disponíveis.");
            return true;
        }

        // Definir o idioma do jogador
        plugin.setPlayerLanguage(player, language);

        // Informar o jogador
        player.sendMessage("§aIdioma definido para: §e" + language);

        return true;
    }
}
