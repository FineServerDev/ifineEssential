package eu.ifine.ifineess;

import eu.ifine.ifineess.argument.WarpArgumentType;
import eu.ifine.ifineess.commands.RegisterHomeCommand;
import eu.ifine.ifineess.commands.RegisterSpawnCommand;
import eu.ifine.ifineess.commands.RegisterTpaCommand;
import eu.ifine.ifineess.commands.RegisterWarpCommand;
import eu.ifine.ifineess.leveldb.Home;
import eu.ifine.ifineess.leveldb.LevelDbUtil;
import eu.ifine.ifineess.leveldb.Spawn;
import eu.ifine.ifineess.leveldb.Warp;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.command.argument.UuidArgumentType;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.UUID;

public class Ifineess implements ModInitializer {
    public static final Logger LOGGER = LogManager.getLogger();
    public static HashMap<UUID, Home> homeMap = new HashMap<>();
    public static HashMap<String, Warp> warpMap = new HashMap<>();
    public static Spawn spawn;
    public static LevelDbUtil levelDb = new LevelDbUtil();
    public  static MinecraftServer SERVER;
    public static String PREFIX = "§6§l[§e§lFINE§6§l]§r ";

    @Override
    public void onInitialize() {
        levelDb.initLevelDB();
        InitHomeMap();
        InitWarpMap();
        InitSpawn();
        //ArgumentTypeRegistry.registerArgumentType(new Identifier(Identifier.DEFAULT_NAMESPACE, "warp"), WarpArgumentType.class, ConstantArgumentSerializer.of(WarpArgumentType::warp));

        CommandRegistrationCallback.EVENT.register((commandDispatcher, registryAccess, environment) -> {
            RegisterHomeCommand.registerCommands(commandDispatcher);
            RegisterWarpCommand.registerCommands(commandDispatcher);
            RegisterTpaCommand.registerCommands(commandDispatcher);
            RegisterSpawnCommand.registerCommands(commandDispatcher);
        });

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            SERVER = server;
        });

        LOGGER.info("Ifineess加载成功！");
    }

    public void InitHomeMap(){
        levelDb.getKeys("home_").forEach(uuid -> {
            Home obj = levelDb.get(uuid,Home.class);
            String key = uuid.substring(5);
            homeMap.put(UUID.fromString(key), obj);
        });
    }

    public void InitWarpMap(){
        levelDb.getKeys("warp_").forEach(uuid -> {
            Warp obj = levelDb.get(uuid,Warp.class);
            String key = uuid.substring(5);
            warpMap.put(key, obj);
        });
    }

    public void InitSpawn(){
        spawn = levelDb.get("spawn",Spawn.class);
    }

}