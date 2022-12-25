package eu.ifine.ifineess.leveldb;

import eu.ifine.ifineess.Ifineess;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Home {
    private UUID uuid;
    private ArrayList<HomeData> homeList;

    public Home(UUID uuid) {
        this.uuid = uuid;
        this.homeList = new ArrayList<>();
    }

    public void addHome(String name, Vec3d pos, RegistryKey<World> world) {
        if (homeList == null) {
            homeList = new ArrayList<>();
        }
        homeList.add(new HomeData(name, pos, world));
        save();
    }


    public void removeHome(String name) {
        for (HomeData homeData : homeList) {
            if (homeData.getName().equals(name)) {
                homeList.remove(homeData);
                save();
                return;
            }
        }
    }

    public boolean hasHome(String name) {
        for (HomeData homeData : homeList) {
            if (homeData.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public Pair<Vec3d, RegistryKey<World>> getHome(String name) {
        for (HomeData homeData : homeList) {
            if (homeData.getName().equals(name)) {
                return new Pair<>(homeData.getPos(), homeData.getDimension());
            }
        }
        return null;
    }

    public List<String> getHomes(){
        List<String> result = new ArrayList<>();
        for (HomeData homeData : homeList) {
            result.add(homeData.getName());
        }
        return result;
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
