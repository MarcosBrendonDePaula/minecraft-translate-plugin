package com.translator.minecraft;

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
        StringBuilder message = new StringBuilder("§6[TranslateChat] §eIdiomas disponíveis:\n");
        
        for (String language : plugin.getAvailableLanguages()) {
            message.append("§f- ").append(language).append("\n");
        }
        
        sender.sendMessage(message.toString());
        
        if (sender instanceof Player) {
            Player player = (Player) sender;
            String currentLanguage = plugin.getPlayerLanguage(player);
            sender.sendMessage("§6[TranslateChat] §eSeu idioma atual é: §f" + currentLanguage);
        }
        
        return true;
    }
}
