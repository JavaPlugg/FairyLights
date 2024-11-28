package net.javaplugg.minecraft.fairylights.recipe;

import net.javaplugg.minecraft.fairylights.FairyLightsPlugin;
import net.javaplugg.minecraft.fairylights.item.Items;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import java.util.HashMap;
import java.util.Map;

public class Recipes {

    private static final Map<String, Material> DYE_MAP = new HashMap<>();

    static {
        DYE_MAP.put("multicolored_lights", Material.COMPARATOR);
        DYE_MAP.put("white_lights", Material.WHITE_DYE);
        DYE_MAP.put("light_gray_lights", Material.LIGHT_GRAY_DYE);
        DYE_MAP.put("gray_lights", Material.GRAY_DYE);
        DYE_MAP.put("black_lights", Material.BLACK_DYE);
        DYE_MAP.put("brown_lights", Material.BROWN_DYE);
        DYE_MAP.put("red_lights", Material.RED_DYE);
        DYE_MAP.put("orange_lights", Material.ORANGE_DYE);
        DYE_MAP.put("yellow_lights", Material.YELLOW_DYE);
        DYE_MAP.put("lime_lights", Material.LIME_DYE);
        DYE_MAP.put("green_lights", Material.GREEN_DYE);
        DYE_MAP.put("cyan_lights", Material.CYAN_DYE);
        DYE_MAP.put("light_blue_lights", Material.LIGHT_BLUE_DYE);
        DYE_MAP.put("blue_lights", Material.BLUE_DYE);
        DYE_MAP.put("purple_lights", Material.PURPLE_DYE);
        DYE_MAP.put("magenta_lights", Material.MAGENTA_DYE);
        DYE_MAP.put("pink_lights", Material.PINK_DYE);
    }

    public static void add() {
        Items.ITEM_MAP.forEach((id, itemStack) -> {
            ShapedRecipe recipe = createRecipe(id, itemStack);
            recipe.shape(
                    "   ",
                    "SDS",
                    " L "
            );
            recipe.setIngredient('S', Material.STRING);
            recipe.setIngredient('L', Material.REDSTONE_LAMP);
            recipe.setIngredient('D', DYE_MAP.get(id));
            Bukkit.addRecipe(recipe);
        });
    }

    private static ShapedRecipe createRecipe(String key, ItemStack result) {
        NamespacedKey namespacedKey = new NamespacedKey(FairyLightsPlugin.getPlugin(FairyLightsPlugin.class), key);
        return new ShapedRecipe(namespacedKey, result);
    }
}
