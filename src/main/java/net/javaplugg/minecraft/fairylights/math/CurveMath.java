package net.javaplugg.minecraft.fairylights.math;

import org.bukkit.Location;

public class CurveMath {

    public static Location[] curve(Location loc1, Location loc2) {
        int n = (int) Math.floor(loc1.distance(loc2) / 2);

        Location[] lamps = new Location[n];
        try {
            lamps[0] = loc1;
        } catch (ArrayIndexOutOfBoundsException e) {
            return lamps;
        }

        Location diff = MathUtil.divide(loc2.clone().subtract(loc1), n - 1);
        for (int i = 1; i < n; i++) {
            double x = i * 1.0 / (n);
            double y = (Math.pow((x - 0.5) * 2, 2) - 1) * loc1.distance(loc2) * 0.15;






                                                                 // *Добавим
            lamps[i] = loc1.clone().add(MathUtil.multiply(diff, i)).add(0, y, 0);








        }
        return lamps;
    }
}
