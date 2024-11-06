package net.javaplugg.minecraft.fairylights.blocklight;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Light;

public class BlockLight {

    public static void lightUp(Block block) {
        if (!block.getType().isAir() && !block.getType().equals(Material.LIGHT)) {
            return;
        }
        Light light = (Light) Bukkit.createBlockData(Material.LIGHT);
        light.setLevel(15);
        block.setBlockData(light, true);
    }

    public static void lightDown(Block block) {
        if (!block.getType().isAir() && !block.getType().equals(Material.LIGHT)) {
            return;
        }
        Light light = (Light) Bukkit.createBlockData(Material.LIGHT);
        light.setLevel(0);
        block.setBlockData(light, true);
    }
}
