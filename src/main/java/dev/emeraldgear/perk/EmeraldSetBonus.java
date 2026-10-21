package dev.emeraldgear.perk;

import dev.emeraldgear.item.ModItems;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Wear the full emerald set and villagers treat you like a raid hero:
 * you get the Hero of the Village effect, which means cheaper trades
 * (about 30 percent off at level 1, same as the vanilla effect).
 *
 * How it works: once per second the server checks every player. If all four
 * armor slots hold emerald armor, a short Hero of the Village effect is refreshed.
 * Because the effect is short, it disappears within a few seconds of taking a piece off.
 */
public final class EmeraldSetBonus {
    /** Flip this to false if you ever want the armor to be purely cosmetic stats. */
    public static final boolean ENABLED = true;

    /** How often (in ticks) we check players. 20 ticks = 1 second. */
    private static final int CHECK_INTERVAL_TICKS = 20;

    /** How long each refresh lasts. Slightly longer than the check interval so it never flickers. */
    private static final int EFFECT_DURATION_TICKS = 60;

    private EmeraldSetBonus() {
    }

    public static void register() {
        if (!ENABLED) {
            return;
        }

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (server.getTicks() % CHECK_INTERVAL_TICKS != 0) {
                return;
            }

            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                if (isWearingFullSet(player)) {
                    applyHeroEffect(player);
                }
            }
        });
    }

    private static boolean isWearingFullSet(ServerPlayerEntity player) {
        return player.getEquippedStack(EquipmentSlot.HEAD).isOf(ModItems.EMERALD_HELMET)
                && player.getEquippedStack(EquipmentSlot.CHEST).isOf(ModItems.EMERALD_CHESTPLATE)
                && player.getEquippedStack(EquipmentSlot.LEGS).isOf(ModItems.EMERALD_LEGGINGS)
                && player.getEquippedStack(EquipmentSlot.FEET).isOf(ModItems.EMERALD_BOOTS);
    }

    private static void applyHeroEffect(ServerPlayerEntity player) {
        StatusEffectInstance current = player.getStatusEffect(StatusEffects.HERO_OF_THE_VILLAGE);

        // If the player earned a real Hero of the Village from a raid (stronger or longer), leave it alone.
        if (current != null && (current.getAmplifier() > 0 || current.getDuration() > EFFECT_DURATION_TICKS)) {
            return;
        }

        player.addStatusEffect(new StatusEffectInstance(
                StatusEffects.HERO_OF_THE_VILLAGE,
                EFFECT_DURATION_TICKS,
                0,      // amplifier 0 = level 1
                true,   // ambient (uses the softer beacon-style effect frame)
                false,  // no swirling particles
                true    // show the icon in the corner so you can see it working
        ));
    }
}
