package eu.ifine.ifineess.leveldb;

import eu.ifine.ifineess.Ifineess;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

public class HomeData{
    private String name;
    private double x;
    private double y;
    private double z;
    private String dimension;

    public HomeData(String name, Vec3d pos, RegistryKey<World> dimension) {
        this.name = name;
        this.x = pos.x;
        this.y = pos.y;
        this.z = pos.z;
        this.dimension = dimension.getValue().toString();
    }

    public Vec3d getPos() {
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

    public String getName() {
        return name;
    }
}
