package net.javaplugg.minecraft.fairylights.spawner;

import net.javaplugg.minecraft.fairylights.FairyLightsPlugin;
import net.javaplugg.minecraft.fairylights.blocklight.BlockLight;
import net.javaplugg.minecraft.fairylights.math.CurveMath;
import net.javaplugg.minecraft.fairylights.math.MathUtil;
import org.bukkit.*;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.List;
import java.util.UUID;

public class FairyLightsSpawner {

    private static final float THICKNESS = 0.2f;

    public static void spawn(Location loc1, Location loc2, String owner, boolean temporary, List<Material> lampMaterial) {
        if (loc1.distance(loc2) > 100.0) {
            return;
        }
        Location[] curve = CurveMath.curve(loc1, loc2);
        String uuid = UUID.randomUUID().toString();
        for (int i = 0; i < curve.length - 1; i++) {
            summonBlockDisplay(curve[i], curve[i + 1], owner, uuid, temporary);
        }
        for (int i = 1; i < curve.length - 1; i++) {
            summonLantern(curve[i], loc1, owner, uuid, temporary, lampMaterial.get(i % lampMaterial.size()));
        }
    }

    private static void summonBlockDisplay(Location loc1, Location loc2, String owner, String uuid, boolean temporary) {
        Location centerLocation = MathUtil.center(loc1, loc2);

        World world = loc1.getWorld();
        assert world != null;

        double diffX = loc2.getX() - loc1.getX();
        double diffY = loc2.getY() - loc1.getY();
        double diffZ = loc2.getZ() - loc1.getZ();

        BlockDisplay blockDisplay = (BlockDisplay) world.spawnEntity(centerLocation.add(
                -diffX / 2,
                -diffY / 2,
                -diffZ / 2
        ), EntityType.BLOCK_DISPLAY);
        blockDisplay.setBlock(Bukkit.createBlockData(Material.BLACK_WOOL));
        setOwnerAndUUID(blockDisplay, owner, uuid);

        Vector3f translation = new Vector3f(0, 0, 0);
        AxisAngle4f leftRotation = new AxisAngle4f(0, 0, 0, 0);
        Vector3f scale = new Vector3f(THICKNESS, THICKNESS, (float) loc1.distance(loc2));
        AxisAngle4f rightRotation = new AxisAngle4f(0, 0, 0, 0);

        Transformation transformation = new Transformation(translation, leftRotation, scale, rightRotation);
        blockDisplay.setTransformation(transformation);

        Vector direction = loc2.toVector().subtract(loc1.toVector()).normalize();
        float yaw = (float) -Math.toDegrees(Math.atan2(direction.getX(), direction.getZ()));
        float pitch = (float) -Math.toDegrees(Math.asin(direction.getY()));
        blockDisplay.setRotation(yaw, pitch);

        if (!temporary) {
            return;
        }
        Bukkit.getScheduler().runTaskLater(FairyLightsPlugin.getPlugin(FairyLightsPlugin.class), blockDisplay::remove, 2);
    }

    @SuppressWarnings("SuspiciousNameCombination")
    private static void summonLantern(Location loc, Location loc1, String owner, String uuid, boolean temporary, Material material) {
        final float glassWidth = 0.6f;
        final float glowWidth = 0.4f;

        World world = loc.getWorld();
        assert world != null;
        Location glassLoc = new Location(loc.getWorld(),
                loc.getX() - glassWidth / 2 - THICKNESS / 2,
                loc.getY(),
                loc.getZ() - glassWidth / 2 - THICKNESS / 2
        );
        Location glowLoc = new Location(loc.getWorld(),
                loc.getX() - glowWidth / 2 - THICKNESS / 2,
                loc.getY(),
                loc.getZ() - glowWidth / 2 - THICKNESS / 2
        );

        double diffX = loc.getX() - loc1.getX();
        double diffZ = loc.getZ() - loc1.getZ();
        double angle = Math.atan2(diffZ, diffX);

        glassLoc = MathUtil.rotate(loc, glassLoc, angle).subtract(0, glassWidth / 2, 0);
        glowLoc = MathUtil.rotate(loc, glowLoc, angle).subtract(0, glowWidth / 2, 0);

        if (!temporary) {
            BlockLight.lightUp(glowLoc.getBlock());
        }

        BlockDisplay glass = (BlockDisplay) world.spawnEntity(glassLoc, EntityType.BLOCK_DISPLAY);
        BlockDisplay glow = (BlockDisplay) world.spawnEntity(glowLoc, EntityType.BLOCK_DISPLAY);
        glass.setBlock(Bukkit.createBlockData(material));
        glow.setBlock(Bukkit.createBlockData(Material.GLOWSTONE));
        setOwnerAndUUID(glass, owner, uuid);
        setOwnerAndUUID(glow, owner, uuid);

        glass.setRotation((float) Math.toDegrees(angle), 0f);
        glow.setRotation((float) Math.toDegrees(angle), 0f);

        Vector3f noTranslation = new Vector3f(0, 0, 0);
        AxisAngle4f noRotation = new AxisAngle4f(0, 0, 0, 0);
        Vector3f glassScale = new Vector3f(glassWidth, glassWidth, glassWidth);
        Vector3f glowScale = new Vector3f(glowWidth, glowWidth, glowWidth);

        glass.setTransformation(new Transformation(noTranslation, noRotation, glassScale, noRotation));
        glow.setTransformation(new Transformation(noTranslation, noRotation, glowScale, noRotation));

        if (!temporary) {
            return;
        }
        Bukkit.getScheduler().runTaskLater(FairyLightsPlugin.getPlugin(FairyLightsPlugin.class), () -> {
            glass.remove();
            glow.remove();
        }, 2);
    }
    
    private static void setOwnerAndUUID(Entity entity, String owner, String uuid) {
        PersistentDataContainer container = entity.getPersistentDataContainer();
        NamespacedKey ownerKey = new NamespacedKey(FairyLightsPlugin.getPlugin(FairyLightsPlugin.class), "owner");
        NamespacedKey uuidKey = new NamespacedKey(FairyLightsPlugin.getPlugin(FairyLightsPlugin.class), "uuid");
        container.set(ownerKey, PersistentDataType.STRING, owner);
        container.set(uuidKey, PersistentDataType.STRING, uuid);
    }

    @SuppressWarnings("DataFlowIssue")
    public static void remove(String owner, String uuid) {
        Bukkit.getWorlds().get(0).getEntities().stream().filter(entity -> {
            if (!entity.getType().equals(EntityType.BLOCK_DISPLAY)) {
                return false;
            }
            PersistentDataContainer container = entity.getPersistentDataContainer();
            NamespacedKey ownerKey = new NamespacedKey(FairyLightsPlugin.getPlugin(FairyLightsPlugin.class), "owner");
            NamespacedKey uuidKey = new NamespacedKey(FairyLightsPlugin.getPlugin(FairyLightsPlugin.class), "uuid");
            if (!container.has(ownerKey, PersistentDataType.STRING)) {
                return false;
            }
            if (!container.get(ownerKey, PersistentDataType.STRING).equals(owner)) {
                return false;
            }
            return container.get(uuidKey, PersistentDataType.STRING).equals(uuid);
        }).forEach(entity -> {
            entity.remove();
            BlockLight.lightDown(entity.getLocation().getBlock());
        });
    }
}
