package org.imanity.knockback;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import org.imanity.knockback.modules.RegularPlusKnockback;
import org.imanity.knockback.player.PlayerListener;
import org.imanity.knockback.player.PlayerManager;
import spg.lgdev.iSpigot;
import spg.lgdev.knockback.impl.RegularKnockback;

@Getter
public final class ImanityKBPlus extends JavaPlugin {

    @Getter
    private static ImanityKBPlus instance;

    private PlayerManager playerManager;

    @Override
    public void onEnable() {

        instance = this;

        this.playerManager = new PlayerManager();
        this.getServer().getPluginManager().registerEvents(new PlayerListener(), this);

        iSpigot.INSTANCE.getKnockbackHandler().registerModule("regular_plus", RegularPlusKnockback.class);

        /*
        Regular+ is a fork of regular
        - Added floaty vertical
        - Added kb reduction
         */
    }

    @Override
    public void onDisable() {

        iSpigot.INSTANCE.getKnockbackHandler().unregisterModule("regular_plus");

    }
}
