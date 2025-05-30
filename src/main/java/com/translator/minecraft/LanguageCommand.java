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
            sender.sendMessage(plugin.getConfig().getString("messages.prefix") + 
                "§cEste comando só pode ser executado por jogadores.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length < 1) {
            String currentLanguage = plugin.getPlayerLanguage(player);
            String message = plugin.getConfig().getString("messages.prefix") + 
                plugin.getConfig().getString("messages.current-language").replace("%language%", currentLanguage);
            player.sendMessage(message);
            player.sendMessage(plugin.getConfig().getString("messages.prefix") + 
                "§eUse /language <idioma> para alterar.");
            
            // Abrir interface gráfica se estiver habilitada
            if (plugin.getConfig().getBoolean("language-selection.enabled", true)) {
                plugin.getLanguageSelectionGUI().openLanguageSelection(player);
            }
            
            return true;
        }

        String language = args[0].toLowerCase();

        if (!plugin.isLanguageAvailable(language)) {
            player.sendMessage(plugin.getConfig().getString("messages.prefix") + 
                plugin.getConfig().getString("messages.language-not-available"));
            return true;
        }

        plugin.setPlayerLanguage(player, language);
        String message = plugin.getConfig().getString("messages.prefix") + 
            plugin.getConfig().getString("messages.language-changed").replace("%language%", language);
        player.sendMessage(message);
        return true;
    }
}
