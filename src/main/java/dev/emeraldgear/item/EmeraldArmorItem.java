package dev.emeraldgear.item;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

/**
 * A normal armor item that also tells the player about the full set bonus
 * (the actual bonus logic lives in {@link dev.emeraldgear.perk.EmeraldSetBonus}).
 */
public class EmeraldArmorItem extends ArmorItem {
    public EmeraldArmorItem(ArmorMaterial material, ArmorItem.Type type, Settings settings) {
        super(material, type, settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        tooltip.add(Text.translatable("tooltip.emeraldgear.set_bonus_title").formatted(Formatting.GRAY));
        tooltip.add(Text.translatable("tooltip.emeraldgear.set_bonus").formatted(Formatting.GREEN));
    }
}
