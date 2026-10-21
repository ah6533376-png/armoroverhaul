package dev.emeraldgear;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.emeraldgear.item.ModItemGroups;
import dev.emeraldgear.item.ModItems;
import dev.emeraldgear.perk.ArmorSetBonuses;
import dev.emeraldgear.perk.EmeraldSetBonus;
import net.fabricmc.api.ModInitializer;

public class EmeraldGear implements ModInitializer {
    public static final String MOD_ID = "emeraldgear";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        // Order matters: items first, then the creative tab that lists them.
        ModItems.register();
        ModItemGroups.register();
        EmeraldSetBonus.register();
        ArmorSetBonuses.register();

        LOGGER.info("Armor Overhaul loaded. Go get some emeralds.");
    }
}
