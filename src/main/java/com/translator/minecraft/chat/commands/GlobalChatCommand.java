package com.translator.minecraft.chat.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.translator.minecraft.TranslateChat;
import com.translator.minecraft.chat.ChatMode;

/**
 * Comando para alternar para o chat global ou enviar uma mensagem global
 */
public class GlobalChatCommand implements CommandExecutor {

    private final TranslateChat plugin;

    public GlobalChatCommand(TranslateChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cEste comando só pode ser executado por jogadores.");
            return true;
        }

        Player player = (Player) sender;

        // Se não houver argumentos, apenas alterna o modo de chat para global
        if (args.length == 0) {
            boolean isLocked = plugin.getChatManager().togglePlayerChatLock(player);
            plugin.getChatManager().setPlayerChatMode(player, ChatMode.GLOBAL);
            
            if (isLocked) {
                player.sendMessage("§aModo de chat alterado para §6GLOBAL§a e §6TRAVADO§a.");
                player.sendMessage("§aTodas as suas mensagens serão enviadas no chat global até você usar /g novamente.");
            } else {
                player.sendMessage("§aModo de chat alterado para §6GLOBAL§a e §cDESTRAVADO§a.");
                player.sendMessage("§aVocê precisará usar /g antes de cada mensagem global.");
            }
            return true;
        }

        // Se houver argumentos, envia a mensagem no chat global
        StringBuilder message = new StringBuilder();
        for (String arg : args) {
            message.append(arg).append(" ");
        }

        // Simula o envio de uma mensagem de chat
        player.chat(message.toString().trim());
        
        return true;
    }
}
