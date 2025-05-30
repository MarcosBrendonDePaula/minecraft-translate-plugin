package com.translator.minecraft.chat;

import org.bukkit.entity.Player;

/**
 * Enum para representar os diferentes modos de chat disponíveis
 */
public enum ChatMode {
    GLOBAL("global", "&6[G]&r", 0),
    LOCAL("local", "&a[L]&r", 100),
    SYSTEM("system", "&c[SISTEMA]&r", -1);

    private final String name;
    private final String prefix;
    private final int range; // -1 para alcance ilimitado (global), valor em blocos para local

    ChatMode(String name, String prefix, int range) {
        this.name = name;
        this.prefix = prefix;
        this.range = range;
    }

    public String getName() {
        return name;
    }

    public String getPrefix() {
        return prefix;
    }

    public int getRange() {
        return range;
    }

    /**
     * Verifica se um jogador está dentro do alcance para receber uma mensagem
     * @param sender Jogador que enviou a mensagem
     * @param receiver Jogador que pode receber a mensagem
     * @return true se o jogador está dentro do alcance
     */
    public boolean isInRange(Player sender, Player receiver) {
        // Se o alcance for -1, é global (todos recebem)
        if (range == -1) {
            return true;
        }
        
        // Se estiverem em mundos diferentes, não está no alcance
        if (!sender.getWorld().equals(receiver.getWorld())) {
            return false;
        }
        
        // Verifica a distância entre os jogadores
        return sender.getLocation().distance(receiver.getLocation()) <= range;
    }
}
