package org.imanity.knockback.modules;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.imanity.knockback.ImanityKBPlus;
import spg.lgdev.knockback.impl.AbstractKnockback;
import spg.lgdev.util.ValueOrder;

public class RegularPlusKnockback extends AbstractKnockback {
    @ValueOrder(0)
    public float HORIZONTAL = 0.35f;
    @ValueOrder(1)
    public float VERTICAL = 0.35f;
    @ValueOrder(2)
    public float SPRINT_HORIZONTAL = 0.425f;
    @ValueOrder(3)
    public float SPRINT_VERTICAL = 0.085f;
    @ValueOrder(4)
    public float VERTICAL_LIMIT = 0.4000000059604645f;
    @ValueOrder(5)
    public float FRICTION = 2.0f;
    @ValueOrder(6)
    public int HIT_DELAY = 20;
    @ValueOrder(7)
    public float SLOWDOWN = 0.6f;
    @ValueOrder(8)
    public boolean STOP_SPRINT = true;
    @ValueOrder(9)
    public boolean FLOATY_VERTICAL = true;
    @ValueOrder(10)
    public boolean KB_REDUCTION = true;
    @ValueOrder(11)
    public float POT_FALL_SPEED = 0.05f;
    @ValueOrder(12)
    public float POT_THROW_MULTIPLIER = 0.5f;
    @ValueOrder(13)
    public float POT_OFFSET = -20.0f;

    @Override
    public String moduleName() {
        return "regular_plus";
    }

    @Override
    public void attack(EntityPlayer victim, EntityPlayer attacker, int i, double[] victimMot) {
        if (i > 0) {
            victim.g(
                    (-MathHelper.sin(attacker.yaw * 3.1415927F / 180.0F) * (float) i * SPRINT_HORIZONTAL), SPRINT_VERTICAL,
                    (MathHelper.cos(attacker.yaw * 3.1415927F / 180.0F) * (float) i * SPRINT_HORIZONTAL)
            );

            attacker.motX *= SLOWDOWN;
            attacker.motZ *= SLOWDOWN;
            if (STOP_SPRINT) {
                attacker.setSprinting(false);
            }
        }

        if (victim.velocityChanged) {
            EntityPlayer attackedPlayer = victim;
            PlayerVelocityEvent event = new PlayerVelocityEvent(attackedPlayer.getBukkitEntity(), attackedPlayer.getBukkitEntity().getVelocity());

            attacker.world.getServer().getPluginManager().callEvent(event);

            if (!event.isCancelled()) {
                attackedPlayer.getBukkitEntity().setVelocityDirect(event.getVelocity());
                attackedPlayer.playerConnection.sendPacket(new PacketPlayOutEntityVelocity(attackedPlayer));
            }

            attackedPlayer.velocityChanged = false;
            attackedPlayer.motX = victimMot[0];
            attackedPlayer.motY = victimMot[1];
            attackedPlayer.motZ = victimMot[2];
        }
    }

    @Override
    public void attackRegular(Entity victim, Entity attacker, float damage, double d0, double d1) {
        victim.ai = true;
        double magnitude = MathHelper.sqrt(d0 * d0 + d1 * d1);

        victim.motX /= FRICTION;
        victim.motY /= FRICTION;
        victim.motZ /= FRICTION;

        victim.motX -= d0 / magnitude * HORIZONTAL;
        victim.motY += FLOATY_VERTICAL ? ((victim.motY > VERTICAL) ? VERTICAL : (VERTICAL - victim.motY)) : VERTICAL;
        victim.motZ -= d1 / magnitude * HORIZONTAL;

        if (victim.motY > VERTICAL_LIMIT) {
            victim.motY = VERTICAL_LIMIT;
        }
    }

    @Override
    public void roding(EntityPlayer victim, Entity attacker, EntityFishingHook rod) {

        if (KB_REDUCTION) {

            ImanityKBPlus.getInstance().getPlayerManager().addKnockback(victim);

        }

    }

    @Override
    public void bow(EntityPlayer victim, Entity attacker, EntityArrow arrow) {
        if (!arrow.world.isClientSide) {
            victim.o(victim.bv() + 1);
        }

        if (arrow.knockbackStrength > 0) {
            float f3 = (float) MathHelper.sqrt(arrow.motX * arrow.motX + arrow.motZ * arrow.motZ);
            if (f3 > 0.0F) {
                victim.g(arrow.motX * (double) arrow.knockbackStrength * 0.6000000238418579D / (double) f3, 0.1D, arrow.motZ * (double) arrow.knockbackStrength * 0.6000000238418579D / (double) f3);
            }
        }
    }

    @Override
    public int hitDelay() {
        return HIT_DELAY;
    }

    @Override
    public float potFallSpeed() {
        return POT_FALL_SPEED;
    }

    @Override
    public float potThrowMultiplier() {
        return POT_THROW_MULTIPLIER;
    }

    @Override
    public float potOffSet() {
        return POT_OFFSET;
    }
}
