package org.imanity.knockback.modules;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.util.Vector;
import spg.lgdev.knockback.impl.AbstractKnockback;
import spg.lgdev.util.ValueOrder;

public class AdvancedPlusKnockback extends AbstractKnockback {

    @ValueOrder(2)
    public float HORIZONTAL = 0.4f;
    @ValueOrder(3)
    public float VERTICAL = 0.36f;

    @ValueOrder(4)
    public float WTAP_HORI = 1.35f;
    @ValueOrder(5)
    public float WTAP_VERT = 1.022f;

    @ValueOrder(6)
    public boolean SHOULD_INHERIT_HORIZONTAL = true;
    @ValueOrder(7)
    public boolean SHOULD_INHERIT_VERTICAL = false;

    @ValueOrder(8)
    public float FRICTION_HORI = 2.0f;
    @ValueOrder(9)
    public float FRICTION_VERT = 2.0f;

    @ValueOrder(10)
    public float GROUND_HORI = 1.023f;
    @ValueOrder(11)
    public float GROUND_VERT = 1.023f;

    @ValueOrder(12)
    public float SPRINT_SLOWDOWN_HORI = 0.6f;
    @ValueOrder(13)
    public boolean SPRINT_STOP = true;

    @ValueOrder(14)
    public float POT_FALL_SPEED = 0.05f;
    @ValueOrder(15)
    public float POT_THROW_MULTIPLIER = 0.5f;
    @ValueOrder(16)
    public float POT_OFFSET = -20.0f;

    @ValueOrder(17)
    public float ENCHANTED_HORI = 1.0f;

    @ValueOrder(18)
    public float ENCHANTED_VERT = 0.15f;

    @ValueOrder(19)
    public float ROD_HORI = 0.8f;
    @ValueOrder(20)
    public float ROD_VERT = 0.95f;

    @ValueOrder(21)
    public float BOW_HORI = 0.8f;
    @ValueOrder(22)
    public float BOW_VERT = 0.95f;

    @ValueOrder(23)
    public boolean SHOULD_LIMIT_Y_DIFFERENT = true;
    @ValueOrder(24)
    public float Y_DIFFERENT_LIMIT = 1.2f;

    @ValueOrder(25)
    public int HIT_DELAY = 20;

    @ValueOrder(26)
    public boolean COMBO_MODE = false;

    @ValueOrder(27)
    public float COMBO_HEIGHT = 3.0f;

    @ValueOrder(28)
    public float COMBO_VERTICAL = -0.035f;

    @ValueOrder(29)
    public float COMBO_DAMAGE_TICKS = 3;

    @ValueOrder(30)
    public boolean YAW_BASED_CALCULATION = true;

    @ValueOrder(31)
    public boolean FLOATY_VERTICAL = false;

    @Override
    public String moduleName() {
        return "advanced_plus";
    }

    @Override
    public void attack(EntityPlayer victim, EntityPlayer attacker, int multiplier, double[] mot) {
        if (victim.velocityChanged) {
            Entity entity = victim;

            double velX = 0, velY = 0, velZ = 0;

            if (YAW_BASED_CALCULATION) {
                if (SHOULD_INHERIT_HORIZONTAL) {
                    double entityVelX = victim.motX / FRICTION_HORI;
                    double entityVelZ = victim.motZ / FRICTION_HORI;

                    velX = entityVelX + Math.sin(Math.toRadians(attacker.yaw)) * -1.0F;
                    velZ = entityVelZ + Math.cos(Math.toRadians(attacker.yaw));
                } else {
                    velX = Math.sin(Math.toRadians(attacker.getHeadRotation())) * -1.0F;
                    velZ = Math.cos(Math.toRadians(attacker.getHeadRotation()));
                }


                velX *= HORIZONTAL;
                velZ *= HORIZONTAL;

                if (SHOULD_INHERIT_VERTICAL) {
                    if (FLOATY_VERTICAL) {
                        velY = (victim.motY > VERTICAL) ? VERTICAL : (VERTICAL - victim.motY);
                    } else {
                        double entityVelY = victim.motY / FRICTION_VERT;

                        velY = entityVelY + VERTICAL;
                    }
                } else {
                    velY = VERTICAL;
                }


                if (victim.onGround) {
                    velX *= GROUND_HORI;
                    velY *= GROUND_VERT;
                    velZ *= GROUND_HORI;
                }

                int enchLvl = EnchantmentManager.getEnchantmentLevel(Enchantment.KNOCKBACK.id, attacker.inventory.getItemInHand());
                if (enchLvl > 0) {
                    velX = velX + velX * (enchLvl * ENCHANTED_HORI);
                    velY = velY + velY * (enchLvl * ENCHANTED_VERT);
                    velZ = velZ + velZ * (enchLvl * ENCHANTED_HORI);
                }

                if (attacker.shouldDealSprintKnockback) {
                    velX *= WTAP_HORI;
                    velY *= WTAP_VERT;
                    velZ *= WTAP_HORI;

                    attacker.shouldDealSprintKnockback = false;
                }

                if (attacker.isSprinting()) {
                    attacker.motX *= SPRINT_SLOWDOWN_HORI;
                    attacker.motZ *= SPRINT_SLOWDOWN_HORI;
                    attacker.shouldDealSprintKnockback = false;

                    if (SPRINT_STOP) {
                        attacker.setSprinting(false);
                    }
                }

                if (COMBO_MODE) {
                    double yOff = entity.locY - attacker.locY;

                    if (yOff > COMBO_HEIGHT) attacker.ticksDown = MinecraftServer.currentTick;
                    if (yOff > COMBO_HEIGHT || MinecraftServer.currentTick - attacker.ticksDown < COMBO_DAMAGE_TICKS) {
                        velY = COMBO_VERTICAL;
                    }
                }
            } else {
                Vector v = new Vector(entity.locX - attacker.locX, 0, entity.locZ - attacker.locZ).normalize();

                velX = v.getX();
                velY = VERTICAL;
                velZ = v.getZ();

                double yOff = entity.locY - attacker.locY;

                if (COMBO_MODE) {
                    if (yOff > 2.5) attacker.ticksDown = MinecraftServer.currentTick;
                    if (yOff > 2.5 || MinecraftServer.currentTick - attacker.ticksDown < 10) {
                        velY = -0.05;
                    }
                }

                velX *= HORIZONTAL;
                velZ *= HORIZONTAL;

                if (victim.onGround) {
                    velX *= GROUND_HORI;
                    velY *= GROUND_VERT;
                    velZ *= GROUND_HORI;
                }

                int enchLvl = EnchantmentManager.getEnchantmentLevel(Enchantment.KNOCKBACK.id, attacker.inventory.getItemInHand());
                if (enchLvl > 0) {
                    velX = velX + velX * (enchLvl * ENCHANTED_HORI);
                    velY = velY + velY * (enchLvl * ENCHANTED_VERT);
                    velZ = velZ + velZ * (enchLvl * ENCHANTED_HORI);
                }

                if (attacker.shouldDealSprintKnockback) {
                    velX *= WTAP_HORI;
                    velY *= WTAP_VERT;
                    velZ *= WTAP_HORI;
                }

                if (attacker.isSprinting()) {
                    attacker.motX *= SPRINT_SLOWDOWN_HORI;
                    attacker.motZ *= SPRINT_SLOWDOWN_HORI;

                    attacker.shouldDealSprintKnockback = false;

                    attacker.setSprinting(false);
                }
            }

            double yOff = entity.locY - attacker.locY;

            if (SHOULD_LIMIT_Y_DIFFERENT) {
                if (yOff > Y_DIFFERENT_LIMIT) {
                    velY = 0;
                }
            }

            PlayerVelocityEvent event = new PlayerVelocityEvent(victim.getBukkitEntity(), new Vector(velX, velY, velZ));
            Bukkit.getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                victim.playerConnection.sendPacket(new PacketPlayOutEntityVelocity(victim.getId(), velX, velY, velZ));
            }
            victim.velocityChanged = false;
            victim.motX = velX;
            victim.motY = velY;
            victim.motZ = velZ;
        }
    }

    @Override
    public void attackRegular(Entity victim, Entity attacker, float damage, double xo, double zo) {
        victim.ai = true;
        float f1 = MathHelper.sqrt(xo * xo + zo * zo);
        float f2 = 0.4F;

        victim.motX /= 2.0f;
        victim.motY /= 2.0f;
        victim.motZ /= 2.0f;

        victim.motX -= (xo / (double) f1 * (double) f2);
        victim.motY += (double) f2;
        victim.motZ -= (zo / (double) f1 * (double) f2);
        if (victim.motY > 0.4000000059604645f) {
            victim.motY = 0.4000000059604645f;
        }
    }

    @Override
    public void roding(EntityPlayer victim, Entity attacker, EntityFishingHook rod) {
        double velX, velY, velZ;

        Vector v = new Vector(rod.motX, rod.motY, rod.motZ).normalize();
        velX = (v.getX() / 1.6) * ROD_HORI;
        velY = 0.36 * ROD_VERT;
        velZ = (v.getZ() / 1.6) * ROD_HORI;

        victim.playerConnection.sendPacket(new PacketPlayOutEntityVelocity(victim.getId(), velX, velY, velZ));
        victim.velocityChanged = false;
        victim.motX = velX;
        victim.motY = velY;
        victim.motZ = velZ;
    }

    @Override
    public void bow(EntityPlayer victim, Entity attacker, EntityArrow arrow) {
        double velX, velY, velZ;

        Vector v = new Vector(arrow.motX, arrow.motY, arrow.motZ).normalize();
        velX = (v.getX() / 1.6) * BOW_HORI;
        velY = 0.36 * BOW_VERT;
        velZ = (v.getZ() / 1.6) * BOW_HORI;

        if (arrow.knockbackStrength > 0) {
            velX *= arrow.knockbackStrength + 1;
            velZ *= arrow.knockbackStrength + 1;
        }

        victim.playerConnection.sendPacket(new PacketPlayOutEntityVelocity(victim.getId(), velX, velY, velZ));
        victim.velocityChanged = false;
        victim.motX = velX;
        victim.motY = velY;
        victim.motZ = velZ;
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
