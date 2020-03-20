package org.imanity.knockback.player;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.imanity.knockback.ImanityKBPlus;

public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        ImanityKBPlus.getInstance().getPlayerManager().disconnect(player);
    }

}
