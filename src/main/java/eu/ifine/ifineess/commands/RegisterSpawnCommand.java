package eu.ifine.ifineess.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.ifine.ifineess.Ifineess;
import eu.ifine.ifineess.leveldb.Spawn;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.function.Predicate;

public class RegisterSpawnCommand {
    public static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        Predicate<ServerCommandSource> isPlayer = source -> {
            try {
                return source.getPlayerOrThrow() != null;
            } catch (CommandSyntaxException e) {
                return false;
            }
        };
        dispatcher.register(
                CommandManager.literal("spawn")
                        .executes(context -> spawn(context.getSource()))
        );
        //setspawn
        dispatcher.register(
                CommandManager.literal("setspawn")
                        .executes(context -> setSpawn(context.getSource()))
        );
    }

    private static boolean hasPermission(ServerCommandSource source) {
        return source.hasPermissionLevel(2);
    }

    private static int spawn(ServerCommandSource source) {
        if (Ifineess.spawn == null) {
            source.sendFeedback(Text.of(Ifineess.PREFIX + "§c服务器尚未设置出生点！"), false);
            return 0;
        }
        ServerPlayerEntity player = source.getPlayer();
        player.teleport(Ifineess.SERVER.getWorld(Ifineess.spawn.getDimension()) ,Ifineess.spawn.getX(), Ifineess.spawn.getY(), Ifineess.spawn.getZ(), player.getYaw(),player.getPitch());
        return 0;
    }

    private static int setSpawn(ServerCommandSource source) {
        if (!hasPermission(source)) {
            source.sendFeedback(Text.of(Ifineess.PREFIX + "§c你没有权限这么做！"), false);
            return 0;
        }
        ServerPlayerEntity player =  source.getPlayer();

        Spawn spawn = new Spawn(player.getPos().x, player.getPos().y, player.getPos().z, player.getEntityWorld().getRegistryKey().getValue().toString());
        return 0;
    }
}

