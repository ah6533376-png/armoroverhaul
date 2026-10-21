package dev.emeraldgear.item;

import java.util.function.Supplier;

import net.minecraft.item.Items;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;

/**
 * Stats shared by every emerald tool.
 *
 * For reference (vanilla 1.20.1):
 *   iron      -> level 2, durability  250, speed 6.0, damage 2.0, enchantability 14
 *   emerald   -> level 3, durability 1100, speed 7.5, damage 2.5, enchantability 22   (this mod)
 *   diamond   -> level 3, durability 1561, speed 8.0, damage 3.0, enchantability 10
 *   netherite -> level 4, durability 2031, speed 9.0, damage 4.0, enchantability 15
 *
 * So emerald mines everything diamond does, a bit slower and with less durability,
 * but it enchants way better. Change the numbers below to rebalance it.
 */
public enum ModToolMaterials implements ToolMaterial {
    EMERALD(
            3,      // mining level (3 = diamond tier, so it can mine obsidian and ancient debris)
            1100,   // durability
            7.5f,   // mining speed
            2.5f,   // attack damage bonus added on top of each tool's own base damage
            22,     // enchantability
            () -> Ingredient.ofItems(Items.EMERALD) // what repairs it in an anvil
    );

    private final int miningLevel;
    private final int durability;
    private final float miningSpeed;
    private final float attackDamage;
    private final int enchantability;
    private final Supplier<Ingredient> repairIngredient;

    ModToolMaterials(int miningLevel, int durability, float miningSpeed, float attackDamage,
                     int enchantability, Supplier<Ingredient> repairIngredient) {
        this.miningLevel = miningLevel;
        this.durability = durability;
        this.miningSpeed = miningSpeed;
        this.attackDamage = attackDamage;
        this.enchantability = enchantability;
        this.repairIngredient = repairIngredient;
    }

    @Override
    public int getDurability() {
        return this.durability;
    }

    @Override
    public float getMiningSpeedMultiplier() {
        return this.miningSpeed;
    }

    @Override
    public float getAttackDamage() {
        return this.attackDamage;
    }

    @Override
    public int getMiningLevel() {
        return this.miningLevel;
    }

    @Override
    public int getEnchantability() {
        return this.enchantability;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return this.repairIngredient.get();
    }
}
