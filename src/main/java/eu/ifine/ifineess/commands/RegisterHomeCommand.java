package eu.ifine.ifineess.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.ifine.ifineess.Ifineess;
import eu.ifine.ifineess.leveldb.Home;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import java.util.function.Predicate;

public class RegisterHomeCommand {

    public static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        Predicate<ServerCommandSource> isPlayer = source -> {
            try {
                return source.getPlayerOrThrow() != null;
            } catch(CommandSyntaxException e) {
                return false;
            }
        };
        dispatcher.register(
                CommandManager.literal("home")
                        .requires(isPlayer)
                        .then(CommandManager.literal("set")
                                .then(CommandManager.argument("name",StringArgumentType.greedyString())
                                        .executes(context -> setHome(context.getSource(), StringArgumentType.getString(context, "name")))
                                )
                        )
                        .then(CommandManager.literal("del")
                                .then(CommandManager.argument("name", StringArgumentType.greedyString())
                                        .executes(context -> delHome(context.getSource(), StringArgumentType.getString(context, "name")))
                                )
                        )
                        .then(CommandManager.literal("list")
                                .executes(context -> listHome(context.getSource()))
                        )
                        .then(CommandManager.literal("tp")
                                .then(CommandManager.argument("name", StringArgumentType.greedyString())
                                        .executes(context -> tpHome(context.getSource(), StringArgumentType.getString(context, "name")))
                                )
                        )
        );
    }

    private static int setHome(ServerCommandSource source, String name) {
        ServerPlayerEntity player =  source.getPlayer();

        if (!Ifineess.homeMap.containsKey(player.getUuid())) {
            Ifineess.homeMap.put(player.getUuid(), new Home(player.getUuid()));
        }

        Home home = Ifineess.homeMap.get(player.getUuid());
        if (home.hasHome(name)){
            source.sendFeedback(Text.of(Ifineess.PREFIX + "§c你已经有这个家了！"), false);
            return 0;
        }
        home.addHome(name, player.getPos(), player.getWorld().getRegistryKey());
        source.sendFeedback(Text.of(Ifineess.PREFIX + "§a成功设置家("+name+")！"), false);
        return 1;
    }

    private static int delHome(ServerCommandSource source, String name) {
        ServerPlayerEntity player =  source.getPlayer();
        Home home = Ifineess.homeMap.get(player.getUuid());
        if(home == null || !home.hasHome(name)){
            source.sendFeedback(Text.of(Ifineess.PREFIX +"§c你还没有一个家！"), false);
            return 0;
        }
        home.removeHome(name);
        source.sendFeedback(Text.of(Ifineess.PREFIX +"§a成功删除家("+ name +")！"), false);
        return 1;
    }

    private static int listHome(ServerCommandSource source) {
        ServerPlayerEntity player =  source.getPlayer();
        Home home = Ifineess.homeMap.get(player.getUuid());
        if(home == null){
            source.sendFeedback(Text.of(Ifineess.PREFIX +"§c你还没有一个家！"), false);
            return 0;
        }
        StringBuilder homeList = new StringBuilder();
        int Temp = 0;
        for (String homeName : home.getHomes()) {
            Temp ++;
            homeList.append(homeName);
            if(Temp != home.getHomes().size()){
                homeList.append("§7,§r ");
            }
        }
        source.sendFeedback(Text.of(Ifineess.PREFIX +"§a你的家有：§r" + homeList.toString()), false);
        return 1;
    }

    private static int tpHome(ServerCommandSource source, String name) {
        ServerPlayerEntity player =  source.getPlayer();
        Home home = Ifineess.homeMap.get(player.getUuid());
        if(home == null || !home.hasHome(name)){
            source.sendFeedback(Text.of(Ifineess.PREFIX +"§c你还没有一个家！"), false);
            return 0;
        }
        Pair<Vec3d, RegistryKey<World>> h =  home.getHome(name);
        player.teleport(player.getServer().getWorld(h.getRight()), h.getLeft().x, h.getLeft().y, h.getLeft().z, player.getYaw(), player.getPitch());
        source.sendFeedback(Text.of(Ifineess.PREFIX +"§a成功传送到家("+ name +")！"), false);
        return 1;
    }
}
