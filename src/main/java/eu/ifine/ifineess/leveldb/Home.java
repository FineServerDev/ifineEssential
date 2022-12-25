package eu.ifine.ifineess.leveldb;

import eu.ifine.ifineess.Ifineess;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import java.util.HashMap;
import java.util.UUID;

public class Home {
    private UUID uuid;
    private HashMap<String,Pair<Vec3d, RegistryKey<World>>> homeMap;

    public void addHome(String name, Vec3d pos, RegistryKey<World> world) {
        homeMap.put(name, new Pair<>(pos, world));
        save();
    }

    public void removeHome(String name) {
        homeMap.remove(name);
        save();
    }

    public Pair<Vec3d, RegistryKey<World>> getHome(String name) {
        return homeMap.get(name);
    }

    public HashMap<String, Pair<Vec3d, RegistryKey<World>>> getHomeMap() {
        return homeMap;
    }

    public void setHomeMap(HashMap<String, Pair<Vec3d, RegistryKey<World>>> homeMap) {
        this.homeMap = homeMap;
    }

    public void save(){
        Ifineess.homeMap.put(uuid, this);
        Ifineess.levelDb.put("home_" + uuid.toString(), this);
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }
}
