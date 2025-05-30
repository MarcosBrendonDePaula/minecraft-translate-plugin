package com.translator.minecraft;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LanguagesCommand implements CommandExecutor {

    private final TranslateChat plugin;

    public LanguagesCommand(TranslateChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage("§6=== Idiomas Disponíveis ===");
        
        Map<String, String> languages = plugin.getAvailableLanguages();
        for (Map.Entry<String, String> entry : languages.entrySet()) {
            sender.sendMessage("§e" + entry.getKey() + " §7- §f" + entry.getValue());
        }
        
        if (sender instanceof Player) {
            Player player = (Player) sender;
            String currentLanguage = plugin.getPlayerLanguage(player);
            sender.sendMessage("§6Seu idioma atual: §e" + currentLanguage);
        }
        
        sender.sendMessage("§7Use /language <idioma> para definir seu idioma.");
        
        return true;
    }
}
