package eu.ifine.ifineess;

import eu.ifine.ifineess.leveldb.Home;
import eu.ifine.ifineess.leveldb.LevelDbUtil;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.UUID;

public class Ifineess implements ModInitializer {
    public static final Logger LOGGER = LogManager.getLogger();
    public static HashMap<UUID, Home> homeMap = new HashMap<>();
    public static LevelDbUtil levelDb = new LevelDbUtil();

    @Override
    public void onInitialize() {
        levelDb.initLevelDB();
        InitHomeMap();
        LOGGER.info("Ifineess加载成功！");
    }

    public void InitHomeMap(){
        levelDb.getKeys("home_").forEach(uuid -> {
            Home obj = (Home)levelDb.get(uuid);
            homeMap.put(obj.getUuid(), obj);
        });
    }

}