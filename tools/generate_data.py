"""
Generates the repetitive JSON for Emerald Gear: item models, crafting recipes
and the advancements that unlock the recipes in the recipe book.

Run from the project root:   python3 tools/generate_data.py
"""
import json
import os

MOD = "emeraldgear"
RES = os.path.join("src", "main", "resources")

TOOLS = ["emerald_sword", "emerald_pickaxe", "emerald_axe", "emerald_shovel", "emerald_hoe"]
ARMOR = ["emerald_helmet", "emerald_chestplate", "emerald_leggings", "emerald_boots"]

# pattern rows use E = emerald, S = stick
RECIPES = {
    "emerald_sword": ("equipment", ["E", "E", "S"]),
    "emerald_pickaxe": ("equipment", ["EEE", " S ", " S "]),
    "emerald_axe": ("equipment", ["EE", "ES", " S"]),
    "emerald_shovel": ("equipment", ["E", "S", "S"]),
    "emerald_hoe": ("equipment", ["EE", " S", " S"]),
    "emerald_helmet": ("equipment", ["EEE", "E E"]),
    "emerald_chestplate": ("equipment", ["E E", "EEE", "EEE"]),
    "emerald_leggings": ("equipment", ["EEE", "E E", "E E"]),
    "emerald_boots": ("equipment", ["E E", "E E"]),
}

ADV_FOLDER = {
    "emerald_sword": "combat", "emerald_axe": "tools", "emerald_pickaxe": "tools",
    "emerald_shovel": "tools", "emerald_hoe": "tools",
    "emerald_helmet": "combat", "emerald_chestplate": "combat",
    "emerald_leggings": "combat", "emerald_boots": "combat",
}


def write_json(path, data):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w", newline="\n") as f:
        json.dump(data, f, indent=2)
        f.write("\n")


def main():
    # ---- item models ----
    for name in TOOLS:
        write_json(
            os.path.join(RES, "assets", MOD, "models", "item", name + ".json"),
            {"parent": "minecraft:item/handheld", "textures": {"layer0": MOD + ":item/" + name}},
        )
    for name in ARMOR:
        write_json(
            os.path.join(RES, "assets", MOD, "models", "item", name + ".json"),
            {"parent": "minecraft:item/generated", "textures": {"layer0": MOD + ":item/" + name}},
        )

    # ---- recipes ----
    for name, (category, pattern) in RECIPES.items():
        key = {"E": {"item": "minecraft:emerald"}}
        if any("S" in row for row in pattern):
            key["S"] = {"item": "minecraft:stick"}
        write_json(
            os.path.join(RES, "data", MOD, "recipes", name + ".json"),
            {
                "type": "minecraft:crafting_shaped",
                "category": category,
                "key": key,
                "pattern": pattern,
                "result": {"item": MOD + ":" + name},
            },
        )

        # ---- recipe unlock advancement (so it shows in the recipe book after grabbing an emerald) ----
        recipe_id = MOD + ":" + name
        write_json(
            os.path.join(RES, "data", MOD, "advancements", "recipes", ADV_FOLDER[name], name + ".json"),
            {
                "parent": "minecraft:recipes/root",
                "criteria": {
                    "has_emerald": {
                        "conditions": {"items": [{"items": ["minecraft:emerald"]}]},
                        "trigger": "minecraft:inventory_changed",
                    },
                    "has_the_recipe": {
                        "conditions": {"recipe": recipe_id},
                        "trigger": "minecraft:recipe_unlocked",
                    },
                },
                "requirements": [["has_the_recipe", "has_emerald"]],
                "rewards": {"recipes": [recipe_id]},
            },
        )

    print("generated models, recipes and advancements")


if __name__ == "__main__":
    main()
