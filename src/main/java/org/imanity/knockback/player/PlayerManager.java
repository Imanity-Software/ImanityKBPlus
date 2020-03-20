package org.imanity.knockback.player;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.entity.Player;

public class PlayerManager {

    private Int2IntMap LAST_KB_TICK = new Int2IntOpenHashMap();

    public void addKnockback(EntityPlayer player) {
        LAST_KB_TICK.put(player.getId(), MinecraftServer.currentTick);
    }

    public boolean isKnockbackReduction(EntityPlayer player) {
        return LAST_KB_TICK.containsValue(player.getId())
                && MinecraftServer.currentTick - LAST_KB_TICK.get(player.getId()) <= 20;
    }

    public void disconnect(Player player) {
        LAST_KB_TICK.remove(player.getEntityId());
    }

}
