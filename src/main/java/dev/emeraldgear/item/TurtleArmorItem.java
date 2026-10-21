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
 * Turtle shell chestplate, leggings and boots. Normal armor that also tells the player
 * about the full set bonus (the logic lives in {@link dev.emeraldgear.perk.ArmorSetBonuses}).
 */
public class TurtleArmorItem extends ArmorItem {
    public TurtleArmorItem(ArmorMaterial material, ArmorItem.Type type, Settings settings) {
        super(material, type, settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        tooltip.add(Text.translatable("tooltip.emeraldgear.turtle_set_title").formatted(Formatting.GRAY));
        tooltip.add(Text.translatable("tooltip.emeraldgear.turtle_set_bonus").formatted(Formatting.GREEN));
    }
}
