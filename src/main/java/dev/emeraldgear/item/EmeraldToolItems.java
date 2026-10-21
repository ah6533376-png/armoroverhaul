package dev.emeraldgear.item;

import net.minecraft.item.AxeItem;
import net.minecraft.item.HoeItem;
import net.minecraft.item.Item;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ToolMaterial;

/**
 * Vanilla keeps the constructors of the pickaxe, axe and hoe protected.
 * Tiny subclasses with public constructors are the cleanest way to use them,
 * and they behave exactly like the vanilla items (stripping logs, tilling dirt, etc).
 */
public final class EmeraldToolItems {
    private EmeraldToolItems() {
    }

    public static class Pickaxe extends PickaxeItem {
        public Pickaxe(ToolMaterial material, int attackDamage, float attackSpeed, Item.Settings settings) {
            super(material, attackDamage, attackSpeed, settings);
        }
    }

    public static class Axe extends AxeItem {
        public Axe(ToolMaterial material, float attackDamage, float attackSpeed, Item.Settings settings) {
            super(material, attackDamage, attackSpeed, settings);
        }
    }

    public static class Hoe extends HoeItem {
        public Hoe(ToolMaterial material, int attackDamage, float attackSpeed, Item.Settings settings) {
            super(material, attackDamage, attackSpeed, settings);
        }
    }
}
