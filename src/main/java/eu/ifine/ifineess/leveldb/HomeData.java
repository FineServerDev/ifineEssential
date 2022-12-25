package eu.ifine.ifineess.leveldb;

import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

public class HomeData{
    private String name;
    private Vec3d pos;
    private RegistryKey<World> dimension;

    public HomeData(String name, Vec3d pos, RegistryKey<World> dimension) {
        this.name = name;
        this.pos = pos;
        this.dimension = dimension;
    }

    public Vec3d getPos() {
        return pos;
    }

    public RegistryKey<World> getDimension() {
        return dimension;
    }

    public String getName() {
        return name;
    }
}
