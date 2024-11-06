package net.javaplugg.minecraft.fairylights.item;

import net.javaplugg.minecraft.fairylights.FairyLightsPlugin;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;

public class Items {

    public static Map<String, ItemStack> ITEM_MAP = new HashMap<>();

    static {
        ITEM_MAP.put("multicolored_lights", createItem(Material.STRING, "§r§lРазноцветная гирлянда", "multicolored_lights"));

        Map<String, String> colors = new HashMap<>();
        colors.put("white", "Белая");
        colors.put("light_gray", "Светло-серая");
        colors.put("gray", "Серая");
        colors.put("black", "Чёрная");
        colors.put("brown", "Коричневая");
        colors.put("red", "Красная");
        colors.put("orange", "Оранжевая");
        colors.put("yellow", "Жёлтая");
        colors.put("lime", "Лаймовая");
        colors.put("green", "Зелёная");
        colors.put("cyan", "Циановая");
        colors.put("light_blue", "Голубая");
        colors.put("blue", "Синяя");
        colors.put("purple", "Фиолетовая");
        colors.put("magenta", "Пурпурная");
        colors.put("pink", "Розовая");

        colors.forEach((color, name) -> {
            String id = color + "_lights";
            ITEM_MAP.put(id, createItem(Material.STRING, "§r§l" + name + " гирлянда", id));
        });
    }

    @SuppressWarnings("SameParameterValue")
    private static ItemStack createItem(Material material, String name, String id) {
        ItemStack itemStack = new ItemStack(material);
        itemStack.addUnsafeEnchantment(Enchantment.MENDING, 1);
        ItemMeta itemMeta = itemStack.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName(name);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);

        NamespacedKey namespacedKey = new NamespacedKey(FairyLightsPlugin.getPlugin(FairyLightsPlugin.class), "fairy_lights");
        PersistentDataType<String, String> persistentDataType = PersistentDataType.STRING;
        itemMeta.getPersistentDataContainer().set(namespacedKey, persistentDataType, id);

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
