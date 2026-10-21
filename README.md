# Armor Overhaul

A Fabric mod for **Minecraft 1.20.1** that adds emerald tools and armor, a set bonus for every
armor tier, craftable chainmail and a full turtle shell set. Everything is drawn to look like vanilla.

Wear all four armor pieces and villagers treat you like a raid hero: you get the
**Hero of the Village** effect, which means cheaper trades.

## What it adds

| Item | Recipe (E = emerald, S = stick) |
|---|---|
| Emerald Sword | `E` / `E` / `S` (vertical) |
| Emerald Pickaxe | `EEE` / ` S ` / ` S ` |
| Emerald Axe | `EE` / `ES` / ` S` (also works mirrored) |
| Emerald Shovel | `E` / `S` / `S` |
| Emerald Hoe | `EE` / ` S` / ` S` (also works mirrored) |
| Emerald Helmet | `EEE` / `E E` |
| Emerald Chestplate | `E E` / `EEE` / `EEE` |
| Emerald Leggings | `EEE` / `E E` / `E E` |
| Emerald Boots | `E E` / `E E` |

Recipes unlock in the recipe book as soon as you pick up an emerald.
Everything is repaired with emeralds in an anvil, and shows up in a new **Armor Overhaul**
creative tab (and in the vanilla Combat and Tools tabs).

## Stats

| | Iron | **Emerald** | Diamond |
|---|---|---|---|
| Tool durability | 250 | **1100** | 1561 |
| Mining speed | 6.0 | **7.5** | 8.0 |
| Mining level | 2 | **3** | 3 |
| Enchantability | 14 / 9 | **22** | 10 |
| Armor defense (total) | 15 | **18** | 20 |
| Armor toughness | 0 | **1.0** | 2.0 |

Emerald is a bit weaker than diamond on raw stats, but it enchants far better and
comes with the full set bonus.

## Set bonuses (wear all four pieces of the same tier)

| Set | Bonus |
|---|---|
| Leather | +10% movement speed |
| Chainmail | +5% attack speed |
| Iron | +20% knockback resistance |
| Gold | Luck |
| Diamond | Haste |
| Netherite | Fire Resistance |
| Emerald | Hero of the Village (cheaper trades) |
| Turtle shell | Water Breathing, plus Dolphin's Grace while in water |

Set `ENABLED` to `false` in `ArmorSetBonuses` (or `EmeraldSetBonus`) to turn a bonus system off.

## Craftable chainmail

Same shapes as the other armor, made from **iron nuggets**: helmet 5, chestplate 8, leggings 7, boots 4.
Change the key in `data/emeraldgear/recipes/chainmail_*.json` if you want it more expensive.

## Turtle shell set

The vanilla turtle shell is the helmet. This mod adds the matching **Turtle Chestplate**, **Leggings**
and **Boots**, crafted from **scutes** in the usual armor shapes (8, 7 and 4 scutes). Total defense with the
helmet is 13. Scutes also repair them.

## Textures

The item icons are traced from vanilla diamond gear and recolored. `tools/generate_textures.py` is the old
generator and now refuses to run without `--force`, so it cannot overwrite them by accident.

## Requirements

- **JDK 17** (JDK 21 also works with the bundled Gradle 8.5). Newer than 21 will not.
- Internet on the first build (Gradle downloads Minecraft, Fabric and the mappings).

## Build the jar

```bash
# Linux / macOS
./gradlew build

# Windows
gradlew.bat build
```

The finished mod is `build/libs/armor-overhaul-1.1.0.jar`.
Ignore the `-sources` jar, that one is only for developers.

## Install

1. Install [Fabric Loader](https://fabricmc.net/use/) for Minecraft 1.20.1.
2. Download **Fabric API** for 1.20.1 and put it in your `mods` folder.
3. Put `emerald-gear-1.0.0.jar` in the same `mods` folder.
4. Launch the Fabric 1.20.1 profile.

## Develop and test

```bash
./gradlew runClient     # launches a dev client with the mod loaded
./gradlew genSources    # generates readable Minecraft sources for your IDE
```

Open the folder in IntelliJ IDEA (File > Open > pick `build.gradle`) and let it import.
In the dev client, switch to creative, open the **Emerald Gear** tab, and try the gear.
To test the set bonus: put on all four pieces and open a villager, you should see
the Hero of the Village icon in the corner and discounted trades.

## Where things live

```
src/main/java/dev/emeraldgear/
  EmeraldGear.java              mod entrypoint
  item/ModToolMaterials.java    tool stats (durability, speed, enchantability)
  item/ModArmorMaterials.java   armor stats (protection, toughness, texture name)
  item/ModItems.java            registers every item
  item/ModItemGroups.java       creative tab
  item/EmeraldArmorItem.java    armor item with the tooltip
  item/EmeraldToolItems.java    pickaxe / axe / hoe subclasses
  perk/EmeraldSetBonus.java     the Hero of the Village full set bonus

src/main/resources/
  fabric.mod.json               mod metadata (put your name in "authors")
  assets/emeraldgear/           lang file, item models, item textures, icon
  assets/minecraft/textures/models/armor/   the worn armor textures (layer_1 and layer_2)
  data/emeraldgear/             recipes and recipe-unlock advancements

tools/
  generate_textures.py          redraws every texture (needs Pillow)
  generate_data.py              regenerates models, recipes and advancements
```

## Make it yours

- **Rebalance**: edit the numbers in `ModToolMaterials` and `ModArmorMaterials`.
- **No set bonus**: set `ENABLED = false` in `EmeraldSetBonus`.
- **Change a recipe**: edit the JSON in `data/emeraldgear/recipes/`, or change the pattern
  table in `tools/generate_data.py` and rerun it.
- **Retexture**: edit the PNGs directly in an image editor. Armor layer files are 64x32 and
  use the vanilla armor UV layout. Do not rerun `generate_textures.py` afterwards or it will
  overwrite your edits.
- **Your name**: change `authors` in `fabric.mod.json`.

## Troubleshooting

- **Armor looks like a purple and black checkerboard**: the armor texture was not found.
  The texture name comes from `getName()` in `ModArmorMaterials`, and the files must be named
  `<that name>_layer_1.png` and `<that name>_layer_2.png` in
  `assets/minecraft/textures/models/armor/`.
- **Gradle says unsupported Java version**: run Gradle with JDK 17.
- **Items show as missing textures**: check the item model JSON and that the PNG name matches
  the item id.
