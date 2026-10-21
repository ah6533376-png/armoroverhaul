package dev.emeraldgear.item;

import dev.emeraldgear.EmeraldGear;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/** The dedicated "Armor Overhaul" creative tab, plus the items slotted into the vanilla tabs. */
public final class ModItemGroups {
    private ModItemGroups() {
    }

    public static final ItemGroup EMERALD_GEAR = Registry.register(
            Registries.ITEM_GROUP,
            new Identifier(EmeraldGear.MOD_ID, "emerald_gear"),
            FabricItemGroup.builder()
                    .displayName(Text.translatable("itemGroup.emeraldgear"))
                    .icon(() -> new ItemStack(ModItems.EMERALD_SWORD))
                    .entries((context, entries) -> {
                        entries.add(ModItems.EMERALD_SWORD);
                        entries.add(ModItems.EMERALD_PICKAXE);
                        entries.add(ModItems.EMERALD_AXE);
                        entries.add(ModItems.EMERALD_SHOVEL);
                        entries.add(ModItems.EMERALD_HOE);
                        entries.add(ModItems.EMERALD_HELMET);
                        entries.add(ModItems.EMERALD_CHESTPLATE);
                        entries.add(ModItems.EMERALD_LEGGINGS);
                        entries.add(ModItems.EMERALD_BOOTS);
                        entries.add(ModItems.TURTLE_CHESTPLATE);
                        entries.add(ModItems.TURTLE_LEGGINGS);
                        entries.add(ModItems.TURTLE_BOOTS);
                    })
                    .build());

    public static void register() {
        // Also show the gear in the normal vanilla tabs, like a vanilla item would be.
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(entries -> {
            entries.add(ModItems.EMERALD_SWORD);
            entries.add(ModItems.EMERALD_AXE);
            entries.add(ModItems.EMERALD_HELMET);
            entries.add(ModItems.EMERALD_CHESTPLATE);
            entries.add(ModItems.EMERALD_LEGGINGS);
            entries.add(ModItems.EMERALD_BOOTS);
            entries.add(ModItems.TURTLE_CHESTPLATE);
            entries.add(ModItems.TURTLE_LEGGINGS);
            entries.add(ModItems.TURTLE_BOOTS);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> {
            entries.add(ModItems.EMERALD_SHOVEL);
            entries.add(ModItems.EMERALD_PICKAXE);
            entries.add(ModItems.EMERALD_AXE);
            entries.add(ModItems.EMERALD_HOE);
        });
    }
}
