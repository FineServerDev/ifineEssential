package eu.ifine.ifineess;

import com.alibaba.fastjson2.JSONObject;
import com.mojang.authlib.minecraft.client.ObjectMapper;
import eu.ifine.ifineess.commands.RegisterHomeCommand;
import eu.ifine.ifineess.leveldb.Home;
import eu.ifine.ifineess.leveldb.LevelDbUtil;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Ifineess implements ModInitializer {
    public static final Logger LOGGER = LogManager.getLogger();
    public static HashMap<UUID, Home> homeMap = new HashMap<>();
    public static LevelDbUtil levelDb = new LevelDbUtil();

    public static String PREFIX = "§6§l[§e§lFINE§6§l]§r ";

    @Override
    public void onInitialize() {
        levelDb.initLevelDB();
        //InitHomeMap();

        CommandRegistrationCallback.EVENT.register((commandDispatcher, registryAccess, environment) -> {
            RegisterHomeCommand.registerCommands(commandDispatcher);
        });

        LOGGER.info("Ifineess加载成功！");
    }

    public void InitHomeMap(){
        levelDb.getKeys("home_").forEach(uuid -> {
            Object obj = levelDb.get(uuid);
            Home home = JSONObject.parseObject(JSONObject.toJSONString(obj), Home.class);
            homeMap.put(home.getUuid(), home);
        });
    }

}