package com.translator.minecraft.chat.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.translator.minecraft.TranslateChat;
import com.translator.minecraft.chat.ChatMode;

/**
 * Comando para alternar para o chat local ou enviar uma mensagem local
 */
public class LocalChatCommand implements CommandExecutor {

    private final TranslateChat plugin;

    public LocalChatCommand(TranslateChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cEste comando só pode ser executado por jogadores.");
            return true;
        }

        Player player = (Player) sender;

        // Se não houver argumentos, apenas alterna o modo de chat para local
        if (args.length == 0) {
            boolean isLocked = plugin.getChatManager().togglePlayerChatLock(player);
            plugin.getChatManager().setPlayerChatMode(player, ChatMode.LOCAL);
            
            if (isLocked) {
                player.sendMessage("§aModo de chat alterado para §aLOCAL§a e §6TRAVADO§a.");
                player.sendMessage("§aTodas as suas mensagens serão enviadas no chat local até você usar /l novamente.");
            } else {
                player.sendMessage("§aModo de chat alterado para §aLOCAL§a e §cDESTRAVADO§a.");
                player.sendMessage("§aVocê precisará usar /l antes de cada mensagem local.");
            }
            return true;
        }

        // Se houver argumentos, envia a mensagem no chat local
        StringBuilder message = new StringBuilder();
        for (String arg : args) {
            message.append(arg).append(" ");
        }

        // Simula o envio de uma mensagem de chat
        player.chat(message.toString().trim());
        
        return true;
    }
}
