package com.translator.minecraft;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.translator.minecraft.chat.ChatMode;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ChatListener implements Listener {

    private final TranslateChat plugin;

    public ChatListener(TranslateChat plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player sender = event.getPlayer();
        String message = event.getMessage();
        
        // Cancelar o evento para gerenciar manualmente a distribuição da mensagem
        event.setCancelled(true);
        
        // Determinar o modo de chat a ser usado
        ChatMode chatMode = determineChatMode(sender, message);
        
        // Obter o idioma do remetente
        String senderLanguage = plugin.getPlayerLanguage(sender);
        
        // Se for chat global, usar o método centralizado para garantir envio para todos
        if (chatMode == ChatMode.GLOBAL) {
            plugin.processGlobalMessage(sender, message, senderLanguage);
            return;
        }
        
        // Para modos não-globais, continuar com a lógica original
        // Conjunto de jogadores que já receberam a mensagem
        Set<Player> informedPlayers = new HashSet<>();
        
        // Para cada jogador online
        for (Player recipient : Bukkit.getOnlinePlayers()) {
            // Verificar se o jogador está no alcance do modo de chat
            if (!chatMode.isInRange(sender, recipient)) {
                continue;
            }
            
            // Obter o idioma do destinatário
            String recipientLanguage = plugin.getPlayerLanguage(recipient);
            
            // Traduzir a mensagem do idioma do remetente para o idioma do destinatário
            CompletableFuture<String> translationFuture = plugin.getTranslationAPI()
                    .translate(message, senderLanguage, recipientLanguage);
            
            // Processar a tradução quando estiver pronta
            translationFuture.thenAccept(translatedMessage -> {
                // Construir o formato da mensagem
                String prefix = chatMode.getPrefix().replace("&", "§");
                String formattedMessage;
                
                // Se os idiomas forem diferentes, mostrar o idioma original
                if (!senderLanguage.equalsIgnoreCase(recipientLanguage)) {
                    formattedMessage = prefix + " §7[" + senderLanguage.toUpperCase() + "] §f" + 
                            sender.getDisplayName() + "§7: §f" + translatedMessage;
                } else {
                    formattedMessage = prefix + " §f" + sender.getDisplayName() + "§7: §f" + translatedMessage;
                }
                
                // Enviar a mensagem traduzida para o destinatário
                recipient.sendMessage(formattedMessage);
                
                // Adicionar o jogador à lista de informados
                informedPlayers.add(recipient);
            });
        }
        
        // Log da mensagem no console
        plugin.getLogger().info("[CHAT] [" + chatMode.getName().toUpperCase() + "] " + 
                sender.getName() + ": " + message);
    }
    
    /**
     * Determina o modo de chat a ser usado com base no jogador e na mensagem
     * @param player Jogador que enviou a mensagem
     * @param message Mensagem enviada
     * @return Modo de chat a ser usado
     */
    private ChatMode determineChatMode(Player player, String message) {
        // Verificar se o jogador tem um modo de chat travado
        if (plugin.getChatManager().isPlayerChatLocked(player)) {
            // Usar o modo de chat atual do jogador
            return plugin.getChatManager().getPlayerChatMode(player);
        }
        
        // Se a mensagem começar com /g, usar chat global
        if (message.startsWith("/g ")) {
            return ChatMode.GLOBAL;
        }
        
        // Se a mensagem começar com /l, usar chat local
        if (message.startsWith("/l ")) {
            return ChatMode.LOCAL;
        }
        
        // Caso contrário, usar o modo padrão (GLOBAL)
        return ChatMode.GLOBAL;
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Limpar dados do jogador quando ele sair
        plugin.getChatManager().clearPlayerData(event.getPlayer());
    }
}
