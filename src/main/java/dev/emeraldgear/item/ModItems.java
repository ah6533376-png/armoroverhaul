package dev.emeraldgear.item;

import dev.emeraldgear.EmeraldGear;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ShovelItem;
import net.minecraft.item.SwordItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/**
 * Every item this mod adds. Attack numbers follow the vanilla pattern:
 * the first number is the tool base damage, the second is attack speed
 * (negative = slower than the 4.0 default). The material adds its own bonus on top.
 */
public final class ModItems {
    private ModItems() {
    }

    // ---- tools -------------------------------------------------------------------------------
    public static final Item EMERALD_SWORD = register("emerald_sword",
            new SwordItem(ModToolMaterials.EMERALD, 3, -2.4f, new FabricItemSettings()));

    public static final Item EMERALD_SHOVEL = register("emerald_shovel",
            new ShovelItem(ModToolMaterials.EMERALD, 1.5f, -3.0f, new FabricItemSettings()));

    public static final Item EMERALD_PICKAXE = register("emerald_pickaxe",
            new EmeraldToolItems.Pickaxe(ModToolMaterials.EMERALD, 1, -2.8f, new FabricItemSettings()));

    public static final Item EMERALD_AXE = register("emerald_axe",
            new EmeraldToolItems.Axe(ModToolMaterials.EMERALD, 5.0f, -3.0f, new FabricItemSettings()));

    public static final Item EMERALD_HOE = register("emerald_hoe",
            new EmeraldToolItems.Hoe(ModToolMaterials.EMERALD, -2, -0.5f, new FabricItemSettings()));

    // ---- armor -------------------------------------------------------------------------------
    public static final Item EMERALD_HELMET = register("emerald_helmet",
            new EmeraldArmorItem(ModArmorMaterials.EMERALD, ArmorItem.Type.HELMET, new FabricItemSettings()));

    public static final Item EMERALD_CHESTPLATE = register("emerald_chestplate",
            new EmeraldArmorItem(ModArmorMaterials.EMERALD, ArmorItem.Type.CHESTPLATE, new FabricItemSettings()));

    public static final Item EMERALD_LEGGINGS = register("emerald_leggings",
            new EmeraldArmorItem(ModArmorMaterials.EMERALD, ArmorItem.Type.LEGGINGS, new FabricItemSettings()));

    public static final Item EMERALD_BOOTS = register("emerald_boots",
            new EmeraldArmorItem(ModArmorMaterials.EMERALD, ArmorItem.Type.BOOTS, new FabricItemSettings()));

    // ---- turtle shell set (the helmet is the vanilla turtle shell) ---------------------------
    public static final Item TURTLE_CHESTPLATE = register("turtle_chestplate",
            new TurtleArmorItem(ModArmorMaterials.TURTLE_SHELL, ArmorItem.Type.CHESTPLATE, new FabricItemSettings()));

    public static final Item TURTLE_LEGGINGS = register("turtle_leggings",
            new TurtleArmorItem(ModArmorMaterials.TURTLE_SHELL, ArmorItem.Type.LEGGINGS, new FabricItemSettings()));

    public static final Item TURTLE_BOOTS = register("turtle_boots",
            new TurtleArmorItem(ModArmorMaterials.TURTLE_SHELL, ArmorItem.Type.BOOTS, new FabricItemSettings()));

    private static Item register(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(EmeraldGear.MOD_ID, name), item);
    }

    /** Called from the mod initializer. Touching this class is what triggers all the registrations above. */
    public static void register() {
        EmeraldGear.LOGGER.info("Registered emerald tools and armor, plus the turtle shell set");
    }
}
