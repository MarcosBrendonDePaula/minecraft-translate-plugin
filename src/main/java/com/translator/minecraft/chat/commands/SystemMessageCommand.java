package com.translator.minecraft.chat.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.translator.minecraft.TranslateChat;
import com.translator.minecraft.chat.ChatMode;

/**
 * Comando para enviar avisos do sistema para todos os jogadores
 */
public class SystemMessageCommand implements CommandExecutor {

    private final TranslateChat plugin;

    public SystemMessageCommand(TranslateChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Verificar permissão
        if (!sender.hasPermission("translatechat.system")) {
            sender.sendMessage("§cVocê não tem permissão para usar este comando.");
            return true;
        }

        // Verificar se há argumentos
        if (args.length == 0) {
            sender.sendMessage("§cUso correto: /" + label + " <mensagem>");
            return true;
        }

        // Construir a mensagem
        StringBuilder message = new StringBuilder();
        for (String arg : args) {
            message.append(arg).append(" ");
        }
        String messageText = message.toString().trim();

        // Enviar mensagem do sistema para todos os jogadores
        broadcastSystemMessage(messageText);
        
        // Confirmar envio para o remetente
        sender.sendMessage("§aAviso do sistema enviado com sucesso!");

        return true;
    }
    
    /**
     * Envia uma mensagem do sistema para todos os jogadores
     * @param message Conteúdo da mensagem
     */
    private void broadcastSystemMessage(String message) {
        String formattedMessage = ChatMode.SYSTEM.getPrefix().replace("&", "§") + " §f" + message;
        
        // Enviar para todos os jogadores online
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(formattedMessage);
        }
        
        // Log da mensagem do sistema no console
        plugin.getLogger().info("[SISTEMA] " + message);
    }
}
