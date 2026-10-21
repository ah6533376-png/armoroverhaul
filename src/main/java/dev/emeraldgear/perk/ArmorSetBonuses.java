package dev.emeraldgear.perk;

import java.util.UUID;

import dev.emeraldgear.item.ModItems;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ArmorMaterials;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Full set bonuses for the vanilla armor tiers and the turtle shell set.
 * (The emerald set keeps its own Hero of the Village bonus in {@link EmeraldSetBonus}.)
 *
 * <pre>
 *   leather    +10% movement speed          (light and fast)
 *   chainmail  +5% attack speed             (quick hands)
 *   iron       +20% knockback resistance    (hard to push around)
 *   gold       Luck                         (finally a reason to wear it)
 *   diamond    Haste                        (faster mining and swinging)
 *   netherite  Fire Resistance              (walk through lava like it is nothing)
 *   turtle     Water Breathing, plus Dolphin's Grace while in water
 * </pre>
 *
 * How it works: every second the server checks each player. If all four armor slots hold the
 * same tier, the bonus is applied. Effects are short and refreshed every check, so they fade
 * a few seconds after a piece is taken off. Attribute bonuses are added and removed right away.
 */
public final class ArmorSetBonuses {
    /** Flip this to false to turn every set bonus in this class off. */
    public static final boolean ENABLED = true;

    /** How often (in ticks) we check players. 20 ticks = 1 second. */
    private static final int CHECK_INTERVAL_TICKS = 20;

    /** How long each effect refresh lasts. Longer than the check interval so it never flickers. */
    private static final int EFFECT_DURATION_TICKS = 60;

    // Fixed ids so a modifier we added can be found and removed later.
    private static final UUID LEATHER_SPEED_ID = UUID.fromString("7c1f2a10-5b0e-4d7a-9a3c-0e1d2c3b4a51");
    private static final UUID CHAIN_ATTACK_SPEED_ID = UUID.fromString("7c1f2a10-5b0e-4d7a-9a3c-0e1d2c3b4a52");
    private static final UUID IRON_KNOCKBACK_ID = UUID.fromString("7c1f2a10-5b0e-4d7a-9a3c-0e1d2c3b4a53");

    private enum ArmorSet {
        NONE, LEATHER, CHAIN, IRON, GOLD, DIAMOND, NETHERITE, TURTLE
    }

    private ArmorSetBonuses() {
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
                applyBonuses(player, detectSet(player));
            }
        });
    }

    private static void applyBonuses(ServerPlayerEntity player, ArmorSet set) {
        // Attribute bonuses: added while the set is worn, removed as soon as it is not.
        setModifier(player, EntityAttributes.GENERIC_MOVEMENT_SPEED, LEATHER_SPEED_ID,
                "Leather set speed", 0.10, EntityAttributeModifier.Operation.MULTIPLY_BASE,
                set == ArmorSet.LEATHER);
        setModifier(player, EntityAttributes.GENERIC_ATTACK_SPEED, CHAIN_ATTACK_SPEED_ID,
                "Chainmail set attack speed", 0.05, EntityAttributeModifier.Operation.MULTIPLY_BASE,
                set == ArmorSet.CHAIN);
        setModifier(player, EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, IRON_KNOCKBACK_ID,
                "Iron set knockback resistance", 0.2, EntityAttributeModifier.Operation.ADDITION,
                set == ArmorSet.IRON);

        // Effect bonuses: short effects refreshed every check.
        if (set == ArmorSet.GOLD) {
            giveEffect(player, StatusEffects.LUCK);
        } else if (set == ArmorSet.DIAMOND) {
            giveEffect(player, StatusEffects.HASTE);
        } else if (set == ArmorSet.NETHERITE) {
            giveEffect(player, StatusEffects.FIRE_RESISTANCE);
        } else if (set == ArmorSet.TURTLE) {
            giveEffect(player, StatusEffects.WATER_BREATHING);
            if (player.isTouchingWater()) {
                giveEffect(player, StatusEffects.DOLPHINS_GRACE);
            }
        }
    }

    /** Works out which full set the player is wearing, if any. */
    private static ArmorSet detectSet(ServerPlayerEntity player) {
        ItemStack head = player.getEquippedStack(EquipmentSlot.HEAD);
        ItemStack chest = player.getEquippedStack(EquipmentSlot.CHEST);
        ItemStack legs = player.getEquippedStack(EquipmentSlot.LEGS);
        ItemStack feet = player.getEquippedStack(EquipmentSlot.FEET);

        // The turtle set mixes the vanilla helmet with this mod's three pieces.
        if (head.isOf(Items.TURTLE_HELMET)
                && chest.isOf(ModItems.TURTLE_CHESTPLATE)
                && legs.isOf(ModItems.TURTLE_LEGGINGS)
                && feet.isOf(ModItems.TURTLE_BOOTS)) {
            return ArmorSet.TURTLE;
        }

        ArmorMaterial material = materialOf(head);
        if (material == null
                || material != materialOf(chest)
                || material != materialOf(legs)
                || material != materialOf(feet)) {
            return ArmorSet.NONE;
        }

        if (material == ArmorMaterials.LEATHER) {
            return ArmorSet.LEATHER;
        }
        if (material == ArmorMaterials.CHAIN) {
            return ArmorSet.CHAIN;
        }
        if (material == ArmorMaterials.IRON) {
            return ArmorSet.IRON;
        }
        if (material == ArmorMaterials.GOLD) {
            return ArmorSet.GOLD;
        }
        if (material == ArmorMaterials.DIAMOND) {
            return ArmorSet.DIAMOND;
        }
        if (material == ArmorMaterials.NETHERITE) {
            return ArmorSet.NETHERITE;
        }
        return ArmorSet.NONE;
    }

    private static ArmorMaterial materialOf(ItemStack stack) {
        if (stack.getItem() instanceof ArmorItem armor) {
            return armor.getMaterial();
        }
        return null;
    }

    private static void giveEffect(ServerPlayerEntity player, StatusEffect effect) {
        StatusEffectInstance current = player.getStatusEffect(effect);

        // If the player has a real, stronger or longer version (a potion, say), leave it alone.
        if (current != null && (current.getAmplifier() > 0 || current.getDuration() > EFFECT_DURATION_TICKS)) {
            return;
        }

        player.addStatusEffect(new StatusEffectInstance(
                effect,
                EFFECT_DURATION_TICKS,
                0,      // amplifier 0 = level 1
                true,   // ambient (softer beacon-style effect frame)
                false,  // no swirling particles
                true    // show the icon so you can see it working
        ));
    }

    private static void setModifier(ServerPlayerEntity player, EntityAttribute attribute, UUID id, String name,
                                    double amount, EntityAttributeModifier.Operation operation, boolean active) {
        EntityAttributeInstance instance = player.getAttributeInstance(attribute);
        if (instance == null) {
            return;
        }

        EntityAttributeModifier existing = instance.getModifier(id);
        if (active && existing == null) {
            instance.addTemporaryModifier(new EntityAttributeModifier(id, name, amount, operation));
        } else if (!active && existing != null) {
            instance.removeModifier(id);
        }
    }
}
