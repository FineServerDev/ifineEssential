package eu.ifine.ifineess.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.ifine.ifineess.Ifineess;
import eu.ifine.ifineess.argument.WarpArgumentType;
import eu.ifine.ifineess.leveldb.Warp;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.function.Predicate;

public class RegisterWarpCommand {
    public static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        Predicate<ServerCommandSource> isPlayer = source -> {
            try {
                return source.getPlayerOrThrow() != null;
            } catch(CommandSyntaxException e) {
                return false;
            }
        };
        dispatcher.register(
                CommandManager.literal("warp")
                        .then(CommandManager.literal("set")
                                .then(CommandManager.argument("name", StringArgumentType.greedyString())
                                        .executes(context -> setWarp(context.getSource(), StringArgumentType.getString(context, "name")))
                                )
                        )
                        .then(CommandManager.literal("del")
                                .then(CommandManager.argument("name", StringArgumentType.greedyString())
                                        .executes(context -> delWarp(context.getSource(), StringArgumentType.getString(context, "name")))
                                )
                        )
                        .then(CommandManager.literal("list")
                                .executes(context -> listWarp(context.getSource()))
                        )
                        .then(CommandManager.literal("tp")
                                .then(CommandManager.argument("name", StringArgumentType.greedyString())
                                        .executes(context -> tpWarp(context.getSource(), StringArgumentType.getString(context, "name")))
                                )
                        )
                        .then(CommandManager.literal("gui")
                                .executes(context -> guiWarp(context.getSource()))
                        )
        );
    }
    private static boolean hasPermission(ServerCommandSource source) {
        return source.hasPermissionLevel(2);
    }

    private static int setWarp(ServerCommandSource source, String name) {
         if (!hasPermission(source)){
                source.sendFeedback(Text.of(Ifineess.PREFIX + "§c你没有权限这么做！"), false);
                return 0;
         }
        ServerPlayerEntity player =  source.getPlayer();
        if (!Ifineess.warpMap.containsKey(name)) {
            new Warp(name, player.getPos(), player.getWorld().getRegistryKey());
            source.sendFeedback(Text.of(Ifineess.PREFIX + "§a成功设置传送点！"), false);
        } else {
            source.sendFeedback(Text.of(Ifineess.PREFIX + "§c传送点已存在！"), false);
        }
        return 1;
    }


    private static int delWarp(ServerCommandSource source, String name) {
        if (!hasPermission(source)){
            source.sendFeedback(Text.of(Ifineess.PREFIX + "§c你没有权限这么做！"), false);
            return 0;
        }
        if (Ifineess.warpMap.containsKey(name)) {
            Ifineess.warpMap.get(name).removeWarp();
            source.sendFeedback(Text.of(Ifineess.PREFIX + "§a成功删除传送点！"), false);
        } else {
            source.sendFeedback(Text.of(Ifineess.PREFIX + "§c传送点不存在！"), false);
        }
        return 0;
    }

    private static int listWarp(ServerCommandSource source) {
        ServerPlayerEntity player =  source.getPlayer();
        StringBuilder homeList = new StringBuilder();
        int Temp = 0;
        for (String WarpName : Ifineess.warpMap.keySet()) {
            Temp ++;
            homeList.append(WarpName);
            if(Temp != Ifineess.warpMap.keySet().size()){
                homeList.append("§7,§r ");
            }
        }
        source.sendFeedback(Text.of(Ifineess.PREFIX +"§a世界传送点有：§r" + homeList.toString()), false);
        return 1;
    }

    private static int tpWarp(ServerCommandSource source, String name) {
        ServerPlayerEntity player =  source.getPlayer();
        if (Ifineess.warpMap.containsKey(name)) {
            Warp warp = Ifineess.warpMap.get(name);
            player.teleport( Ifineess.SERVER.getWorld(warp.getDimension()), warp.getPosition().x, warp.getPosition().y, warp.getPosition().z, player.getYaw(), player.getPitch());
            source.sendFeedback(Text.of(Ifineess.PREFIX + "§a成功传送到传送点！"), false);
        } else {
            source.sendFeedback(Text.of(Ifineess.PREFIX + "§c传送点不存在！"), false);
        }
        return 0;
    }

    private static int guiWarp(ServerCommandSource source) {
        ServerPlayerEntity player =  source.getPlayer();
        return 0;
    }
}
