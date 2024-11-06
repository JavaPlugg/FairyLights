package net.javaplugg.minecraft.fairylights;

import net.javaplugg.minecraft.fairylights.listener.FairyLightsListener;
import net.javaplugg.minecraft.fairylights.recipe.Recipes;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class FairyLightsPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        Recipes.add();
        Bukkit.getPluginManager().registerEvents(new FairyLightsListener(), this);
    }
}