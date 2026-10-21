package dev.emeraldgear.item;

import java.util.function.Supplier;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

/**
 * Stats shared by every piece of emerald armor.
 *
 * For reference (vanilla 1.20.1, total defense points):
 *   iron      -> 15, toughness 0
 *   emerald   -> 18, toughness 1.0   (this mod)
 *   diamond   -> 20, toughness 2.0
 *   netherite -> 20, toughness 3.0, knockback resistance
 */
public enum ModArmorMaterials implements ArmorMaterial {
    EMERALD(
            // IMPORTANT: this is also the texture name. The game looks for
            //   assets/minecraft/textures/models/armor/emeraldgear_emerald_layer_1.png  (helmet, chestplate, boots)
            //   assets/minecraft/textures/models/armor/emeraldgear_emerald_layer_2.png  (leggings)
            // We use a unique prefix so we never clash with another mod or vanilla.
            "emeraldgear_emerald",
            28,                                  // durability multiplier (iron 15, diamond 33)
            new int[] {2, 6, 7, 3},              // protection: boots, leggings, chestplate, helmet
            22,                                  // enchantability
            SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND,
            1.0f,                                // toughness
            0.0f,                                // knockback resistance
            () -> Ingredient.ofItems(Items.EMERALD)
    ),

    // Chestplate, leggings and boots to go with the vanilla turtle shell helmet.
    // Texture files: emeraldgear_turtle_layer_1.png (chestplate, boots) and _layer_2.png (leggings).
    // Total defense is 13 (helmet 2 + chestplate 5 + leggings 4 + boots 2), between chainmail and iron.
    TURTLE_SHELL(
            "emeraldgear_turtle",
            25,                                  // durability multiplier (same as the vanilla turtle shell)
            new int[] {2, 4, 5, 2},              // protection: boots, leggings, chestplate, helmet
            9,                                   // enchantability (same as the vanilla turtle shell)
            SoundEvents.ITEM_ARMOR_EQUIP_TURTLE,
            0.0f,                                // toughness
            0.0f,                                // knockback resistance
            () -> Ingredient.ofItems(Items.SCUTE)
    );

    /** Vanilla base durability per slot: boots, leggings, chestplate, helmet. */
    private static final int[] BASE_DURABILITY = {13, 15, 16, 11};

    private final String name;
    private final int durabilityMultiplier;
    private final int[] protection;
    private final int enchantability;
    private final SoundEvent equipSound;
    private final float toughness;
    private final float knockbackResistance;
    private final Supplier<Ingredient> repairIngredient;

    ModArmorMaterials(String name, int durabilityMultiplier, int[] protection, int enchantability,
                      SoundEvent equipSound, float toughness, float knockbackResistance,
                      Supplier<Ingredient> repairIngredient) {
        this.name = name;
        this.durabilityMultiplier = durabilityMultiplier;
        this.protection = protection;
        this.enchantability = enchantability;
        this.equipSound = equipSound;
        this.toughness = toughness;
        this.knockbackResistance = knockbackResistance;
        this.repairIngredient = repairIngredient;
    }

    /** Maps an armor slot to its index in the arrays above (boots = 0 ... helmet = 3). */
    private static int indexOf(ArmorItem.Type type) {
        if (type == ArmorItem.Type.BOOTS) {
            return 0;
        }
        if (type == ArmorItem.Type.LEGGINGS) {
            return 1;
        }
        if (type == ArmorItem.Type.CHESTPLATE) {
            return 2;
        }
        return 3; // helmet
    }

    @Override
    public int getDurability(ArmorItem.Type type) {
        return BASE_DURABILITY[indexOf(type)] * this.durabilityMultiplier;
    }

    @Override
    public int getProtection(ArmorItem.Type type) {
        return this.protection[indexOf(type)];
    }

    @Override
    public int getEnchantability() {
        return this.enchantability;
    }

    @Override
    public SoundEvent getEquipSound() {
        return this.equipSound;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return this.repairIngredient.get();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public float getToughness() {
        return this.toughness;
    }

    @Override
    public float getKnockbackResistance() {
        return this.knockbackResistance;
    }
}
