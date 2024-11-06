package net.javaplugg.minecraft.fairylights.listener;

import net.javaplugg.minecraft.fairylights.FairyLightsPlugin;
import net.javaplugg.minecraft.fairylights.spawner.FairyLightsSpawner;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FairyLightsListener implements Listener {

    private final Map<String, List<Material>> MATERIALS = new HashMap<>();
    private final Map<String, Location> playerFirstLocationMap = new HashMap<>();
    private final Map<String, List<Material>> playerLightsMap = new HashMap<>();

    public FairyLightsListener() {
        MATERIALS.put("multicolored_lights", List.of(
                Material.RED_STAINED_GLASS,
                Material.YELLOW_STAINED_GLASS,
                Material.LIME_STAINED_GLASS,
                Material.BLUE_STAINED_GLASS
        ));
        MATERIALS.put("white_lights", List.of(Material.WHITE_STAINED_GLASS));
        MATERIALS.put("light_gray_lights", List.of(Material.LIGHT_GRAY_STAINED_GLASS));
        MATERIALS.put("gray_lights", List.of(Material.GRAY_STAINED_GLASS));
        MATERIALS.put("black_lights", List.of(Material.BLACK_STAINED_GLASS));
        MATERIALS.put("brown_lights", List.of(Material.BROWN_STAINED_GLASS));
        MATERIALS.put("red_lights", List.of(Material.RED_STAINED_GLASS));
        MATERIALS.put("orange_lights", List.of(Material.ORANGE_STAINED_GLASS));
        MATERIALS.put("yellow_lights", List.of(Material.YELLOW_STAINED_GLASS));
        MATERIALS.put("lime_lights", List.of(Material.LIME_STAINED_GLASS));
        MATERIALS.put("green_lights", List.of(Material.GREEN_STAINED_GLASS));
        MATERIALS.put("cyan_lights", List.of(Material.CYAN_STAINED_GLASS));
        MATERIALS.put("light_blue_lights", List.of(Material.LIGHT_BLUE_STAINED_GLASS));
        MATERIALS.put("blue_lights", List.of(Material.BLUE_STAINED_GLASS));
        MATERIALS.put("purple_lights", List.of(Material.PURPLE_STAINED_GLASS));
        MATERIALS.put("magenta_lights", List.of(Material.MAGENTA_STAINED_GLASS));
        MATERIALS.put("pink_lights", List.of(Material.PINK_STAINED_GLASS));

        Bukkit.getScheduler().runTaskTimer(FairyLightsPlugin.getPlugin(FairyLightsPlugin.class), () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                String playerName = player.getName();
                if (playerFirstLocationMap.getOrDefault(playerName, null) == null) {
                    continue;
                }
                Location loc1 = playerFirstLocationMap.get(playerName);
                Location loc2 = player.getLocation();
                FairyLightsSpawner.spawn(loc1, loc2, playerName, true, playerLightsMap.get(playerName));
            }
        }, 0, 1);
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        String playerName = player.getName();
        playerFirstLocationMap.put(playerName, null);
        playerLightsMap.put(playerName, null);
    }

    @EventHandler
    @SuppressWarnings({"DataFlowIssue", "unused"})
    public void onPlaceFairyLights(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        }
        if (!event.getHand().equals(EquipmentSlot.HAND)) {
            return;
        }
        ItemStack itemStack = event.getItem();
        if (itemStack == null || itemStack.getType().isAir()) {
            return;
        }
        ItemMeta itemMeta = itemStack.getItemMeta();
        assert itemMeta != null;
        Plugin plugin = FairyLightsPlugin.getPlugin(FairyLightsPlugin.class);
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(plugin, "fairy_lights");
        if (!container.has(key, PersistentDataType.STRING)) {
            return;
        }
        event.setCancelled(true);

        String id = container.get(key, PersistentDataType.STRING);
        Player player = event.getPlayer();
        String playerName = player.getName();
        Location location = event.getClickedBlock().getLocation();

        if (playerFirstLocationMap.getOrDefault(playerName, null) == null) {
            playerFirstLocationMap.put(playerName, location);
            playerLightsMap.put(playerName, MATERIALS.get(id));
            return;
        }

        ItemStack newItem = new ItemStack(Material.AIR);
        if (itemStack.getAmount() > 1) {
            itemStack.setAmount(itemStack.getAmount() - 1);
            newItem = itemStack;
        }
        player.getInventory().setItemInMainHand(newItem);

        Location loc1 = playerFirstLocationMap.get(playerName);
        if (loc1.distance(location) > 100.0) {
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, SoundCategory.MASTER, 100, 1);
            return;
        }
        FairyLightsSpawner.spawn(loc1, location, playerName, false, MATERIALS.get(id));
        playerFirstLocationMap.remove(playerName);
        playerLightsMap.remove(playerName);
    }

    @EventHandler
    @SuppressWarnings({"DataFlowIssue", "unused"})
    public void onDestroyFairyLights(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (!action.equals(Action.LEFT_CLICK_BLOCK) && !action.equals(Action.LEFT_CLICK_AIR)) {
            return;
        }
        if (!event.getHand().equals(EquipmentSlot.HAND)) {
            return;
        }
        Player player = event.getPlayer();
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        if (!isAxe(itemStack)) {
            return;
        }
        String uuid = getUuid(player.getNearbyEntities(2, 2, 2));
        if (uuid == null) {
            return;
        }
        FairyLightsSpawner.remove(player.getName(), uuid);
    }

    private String getUuid(List<Entity> entities) {
        for (Entity entity : entities) {
            PersistentDataContainer container = entity.getPersistentDataContainer();
            NamespacedKey uuidKey = new NamespacedKey(FairyLightsPlugin.getPlugin(FairyLightsPlugin.class), "uuid");
            if (container.has(uuidKey, PersistentDataType.STRING)) {
                return container.get(uuidKey, PersistentDataType.STRING);
            }
        }
        return null;
    }

    private boolean isAxe(ItemStack itemStack) {
        return Set.of(
                Material.WOODEN_AXE,
                Material.STONE_AXE,
                Material.GOLDEN_AXE,
                Material.IRON_AXE,
                Material.DIAMOND_AXE,
                Material.NETHERITE_AXE
        ).contains(itemStack.getType());
    }
}
