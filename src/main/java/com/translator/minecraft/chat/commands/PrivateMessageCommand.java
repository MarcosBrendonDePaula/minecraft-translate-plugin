package com.translator.minecraft.chat.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.translator.minecraft.TranslateChat;
import com.translator.minecraft.chat.ChatMode;

/**
 * Comando para enviar mensagens privadas entre jogadores
 */
public class PrivateMessageCommand implements CommandExecutor {

    private final TranslateChat plugin;

    public PrivateMessageCommand(TranslateChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cEste comando só pode ser executado por jogadores.");
            return true;
        }

        Player player = (Player) sender;

        // Verificar se há argumentos suficientes
        if (args.length < 2) {
            player.sendMessage("§cUso correto: /" + label + " <jogador> <mensagem>");
            return true;
        }

        // Obter o jogador alvo
        String targetName = args[0];
        Player target = Bukkit.getPlayer(targetName);

        if (target == null || !target.isOnline()) {
            player.sendMessage("§cJogador '" + targetName + "' não encontrado ou offline.");
            return true;
        }

        // Não permitir enviar mensagem para si mesmo
        if (target.equals(player)) {
            player.sendMessage("§cVocê não pode enviar mensagens privadas para si mesmo.");
            return true;
        }

        // Construir a mensagem
        StringBuilder message = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            message.append(args[i]).append(" ");
        }
        String messageText = message.toString().trim();

        // Enviar mensagem privada
        sendPrivateMessage(player, target, messageText);
        
        // Armazenar o último alvo para resposta rápida
        plugin.getChatManager().setLastPrivateMessageTarget(player, target);
        plugin.getChatManager().setLastPrivateMessageTarget(target, player);

        return true;
    }
    
    /**
     * Envia uma mensagem privada entre dois jogadores
     * @param sender Jogador que envia a mensagem
     * @param receiver Jogador que recebe a mensagem
     * @param message Conteúdo da mensagem
     */
    private void sendPrivateMessage(Player sender, Player receiver, String message) {
        // Formato para o remetente
        sender.sendMessage("§d[Você → " + receiver.getName() + "] §f" + message);
        
        // Formato para o destinatário
        receiver.sendMessage("§d[" + sender.getName() + " → Você] §f" + message);
        
        // Log da mensagem privada no console (opcional)
        plugin.getLogger().info("[PM] " + sender.getName() + " -> " + receiver.getName() + ": " + message);
    }
}
