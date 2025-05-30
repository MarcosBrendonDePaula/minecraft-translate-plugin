package com.translator.minecraft;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ChatListener implements Listener {

    private final TranslateChat plugin;

    public ChatListener(TranslateChat plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        // Cancelar o evento original para controlar a exibição das mensagens
        event.setCancelled(true);
        
        Player sender = event.getPlayer();
        String originalMessage = event.getMessage();
        
        // Obter o idioma do remetente (idioma em que a mensagem foi escrita)
        String sourceLanguage = plugin.getPlayerLanguage(sender);
        
        // Para cada jogador online, traduzir a mensagem para seu idioma preferido
        for (Player recipient : plugin.getServer().getOnlinePlayers()) {
            // Obter o idioma do destinatário
            String targetLanguage = plugin.getPlayerLanguage(recipient);
            
            // Se os idiomas forem iguais, não precisa traduzir
            if (sourceLanguage.equalsIgnoreCase(targetLanguage)) {
                // Formato da mensagem: [Jogador] Mensagem original (Idioma: xx)
                String formattedMessage = String.format("§7[%s] §f%s §7(Idioma: %s)", 
                    sender.getDisplayName(), originalMessage, sourceLanguage);
                
                recipient.sendMessage(formattedMessage);
            } else {
                // Traduzir a mensagem do idioma do remetente para o idioma do destinatário
                plugin.getTranslationAPI().translate(originalMessage, sourceLanguage, targetLanguage)
                    .thenAccept(translatedText -> {
                        // Formato da mensagem: [Jogador] Mensagem traduzida (Idioma original: xx)
                        String formattedMessage = String.format("§7[%s] §f%s §7(Idioma original: %s)", 
                            sender.getDisplayName(), translatedText, sourceLanguage);
                        
                        recipient.sendMessage(formattedMessage);
                    })
                    .exceptionally(ex -> {
                        // Em caso de erro na tradução, enviar a mensagem original
                        String formattedMessage = String.format("§7[%s] §f%s §7(Idioma original: %s - falha na tradução)", 
                            sender.getDisplayName(), originalMessage, sourceLanguage);
                        
                        recipient.sendMessage(formattedMessage);
                        plugin.getLogger().warning("Erro ao traduzir mensagem: " + ex.getMessage());
                        return null;
                    });
            }
        }
    }
}
