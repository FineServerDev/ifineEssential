package eu.ifine.ifineess.leveldb;

import eu.ifine.ifineess.Ifineess;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

public class Spawn {
    private double x;
    private double y;
    private double z;
    private String dimension;

    public Spawn(double x, double y, double z, String dimension) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.dimension = dimension;
        Ifineess.levelDb.put("spawn", this);
        Ifineess.spawn = this;
    }
    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }
    public Vec3d getPosition() {
        return new Vec3d(x, y, z);
    }

    public RegistryKey<World> getDimension() {
        for (RegistryKey<World> world : Ifineess.SERVER.getWorldRegistryKeys()) {
            if (world.getValue().toString().equals(dimension)) {
                return world;
            }
        }
        return null;
    }
}
