package com.translator.minecraft.chat;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

/**
 * Gerenciador de modos de chat para os jogadores
 */
public class ChatManager {
    
    // Mapa para armazenar o modo de chat atual de cada jogador
    private final Map<UUID, ChatMode> playerChatModes;
    
    // Mapa para armazenar se o jogador está com o modo travado
    private final Map<UUID, Boolean> playerChatLocked;
    
    // Mapa para armazenar o último jogador com quem cada jogador conversou em privado
    private final Map<UUID, UUID> lastPrivateMessageTarget;

    public ChatManager() {
        this.playerChatModes = new HashMap<>();
        this.playerChatLocked = new HashMap<>();
        this.lastPrivateMessageTarget = new HashMap<>();
    }
    
    /**
     * Obtém o modo de chat atual de um jogador
     * @param player Jogador
     * @return Modo de chat atual (padrão: GLOBAL)
     */
    public ChatMode getPlayerChatMode(Player player) {
        return playerChatModes.getOrDefault(player.getUniqueId(), ChatMode.GLOBAL);
    }
    
    /**
     * Define o modo de chat de um jogador
     * @param player Jogador
     * @param mode Modo de chat
     */
    public void setPlayerChatMode(Player player, ChatMode mode) {
        playerChatModes.put(player.getUniqueId(), mode);
    }
    
    /**
     * Verifica se o modo de chat de um jogador está travado
     * @param player Jogador
     * @return true se o modo estiver travado
     */
    public boolean isPlayerChatLocked(Player player) {
        return playerChatLocked.getOrDefault(player.getUniqueId(), false);
    }
    
    /**
     * Define se o modo de chat de um jogador está travado
     * @param player Jogador
     * @param locked true para travar, false para destravar
     */
    public void setPlayerChatLocked(Player player, boolean locked) {
        playerChatLocked.put(player.getUniqueId(), locked);
    }
    
    /**
     * Alterna o travamento do modo de chat de um jogador
     * @param player Jogador
     * @return true se o modo estiver travado após a alternância
     */
    public boolean togglePlayerChatLock(Player player) {
        boolean currentState = isPlayerChatLocked(player);
        setPlayerChatLocked(player, !currentState);
        return !currentState;
    }
    
    /**
     * Define o último jogador com quem um jogador conversou em privado
     * @param player Jogador
     * @param target Alvo da mensagem privada
     */
    public void setLastPrivateMessageTarget(Player player, Player target) {
        lastPrivateMessageTarget.put(player.getUniqueId(), target.getUniqueId());
    }
    
    /**
     * Obtém o UUID do último jogador com quem um jogador conversou em privado
     * @param player Jogador
     * @return UUID do último alvo ou null se não houver
     */
    public UUID getLastPrivateMessageTarget(Player player) {
        return lastPrivateMessageTarget.get(player.getUniqueId());
    }
    
    /**
     * Limpa os dados de um jogador quando ele sai do servidor
     * @param player Jogador
     */
    public void clearPlayerData(Player player) {
        UUID playerId = player.getUniqueId();
        playerChatModes.remove(playerId);
        playerChatLocked.remove(playerId);
        lastPrivateMessageTarget.remove(playerId);
    }
}
