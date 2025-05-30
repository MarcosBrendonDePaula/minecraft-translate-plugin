package com.translator.minecraft.chat.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.translator.minecraft.TranslateChat;

/**
 * Comando para responder rapidamente à última mensagem privada recebida
 */
public class ReplyCommand implements CommandExecutor {

    private final TranslateChat plugin;

    public ReplyCommand(TranslateChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cEste comando só pode ser executado por jogadores.");
            return true;
        }

        Player player = (Player) sender;

        // Verificar se há argumentos
        if (args.length == 0) {
            player.sendMessage("§cUso correto: /" + label + " <mensagem>");
            return true;
        }

        // Obter o último jogador com quem conversou
        java.util.UUID lastTargetUUID = plugin.getChatManager().getLastPrivateMessageTarget(player);
        
        if (lastTargetUUID == null) {
            player.sendMessage("§cVocê ainda não enviou ou recebeu mensagens privadas.");
            return true;
        }

        Player target = Bukkit.getPlayer(lastTargetUUID);
        
        if (target == null || !target.isOnline()) {
            player.sendMessage("§cO jogador com quem você estava conversando não está mais online.");
            return true;
        }

        // Construir a mensagem
        StringBuilder message = new StringBuilder();
        for (String arg : args) {
            message.append(arg).append(" ");
        }
        String messageText = message.toString().trim();

        // Enviar mensagem privada
        sendPrivateMessage(player, target, messageText);

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
