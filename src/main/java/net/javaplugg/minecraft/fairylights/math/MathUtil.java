package net.javaplugg.minecraft.fairylights.math;

import org.bukkit.Location;

public class MathUtil {

    public static Location multiply(Location loc, double d) {
        return new Location(loc.getWorld(), loc.getX() * d, loc.getY() * d, loc.getZ() * d);
    }

    public static Location divide(Location loc, double d) {
        return new Location(loc.getWorld(), loc.getX() / d, loc.getY() / d, loc.getZ() / d);
    }

    public static Location center(Location loc1, Location loc2) {
        double centerX = (loc1.getX() + loc2.getX()) / 2;
        double centerY = (loc1.getY() + loc2.getY()) / 2;
        double centerZ = (loc1.getZ() + loc2.getZ()) / 2;
        return new Location(loc1.getWorld(), centerX, centerY, centerZ);
    }

    public static Location rotate(Location base, Location toRotate, double angle) {
        double diffX = toRotate.getX() - base.getX();
        double diffZ = toRotate.getZ() - base.getZ();
        double theta = Math.atan2(diffZ, diffX);
        double radius = Math.sqrt(diffX * diffX + diffZ * diffZ);
        theta += angle;
        double x = radius * Math.cos(theta);
        double z = radius * Math.sin(theta);
        return new Location(base.getWorld(), base.getX() + x, base.getY(), base.getZ() + z);
    }
}
