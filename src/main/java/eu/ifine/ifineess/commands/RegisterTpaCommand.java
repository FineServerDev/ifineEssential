package eu.ifine.ifineess.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.ifine.ifineess.Ifineess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Predicate;

public class RegisterTpaCommand {

    private static HashMap<ServerPlayerEntity, ServerPlayerEntity> Queue = new HashMap<>();

    public static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        Predicate<ServerCommandSource> isPlayer = source -> {
            try {
                return source.getPlayerOrThrow() != null;
            } catch(CommandSyntaxException e) {
                return false;
            }
        };
        dispatcher.register(
                CommandManager.literal("tpa")
                        .requires(isPlayer)
                        .then(CommandManager.literal("to")
                                .then(CommandManager.argument("name",StringArgumentType.greedyString())
                                        .executes(context -> TpaTo(context.getSource(), StringArgumentType.getString(context, "name")))
                                )
                        )
                        .then(CommandManager.literal("from")
                                .then(CommandManager.argument("name", StringArgumentType.greedyString())
                                        .executes(context -> TpaFrom(context.getSource(), StringArgumentType.getString(context, "name")))
                                )
                        )
                        .then(CommandManager.literal("accept")
                                .executes(context -> TpaAccept(context.getSource()))
                        )
                        .then(CommandManager.literal("reject")
                                .executes(context -> TpaReject(context.getSource()))
                        )
        );
    }

    private static int TpaTo(ServerCommandSource source, String name) {
        ServerPlayerEntity player =  source.getPlayer();
        ServerPlayerEntity target =  Ifineess.SERVER.getPlayerManager().getPlayer(name);

        if(target == null) {
            source.sendFeedback(Text.of(Ifineess.PREFIX + "§c未找到该玩家"), false);
            return 0;
        }

        if (!Queue.containsKey(player) && !Queue.containsValue(player)) {
            Queue.put(player, target);
            target.sendMessage(Text.of(Ifineess.PREFIX + "§a"+ player.getName() +"§e请求传送到你的位置!\n§atpa accept接受\n§6tpa reject拒绝"));
            source.sendFeedback(Text.of(Ifineess.PREFIX + "§a成功发送传送请求"), false);
        } else source.sendFeedback(Text.of(Ifineess.PREFIX + "§6你有一个正在处理中的请求哦"), false);

        Timer timer = new Timer();
        timer.schedule(new timeOut(player, Queue),30000L);
        return 1;
    }

    private static int TpaFrom(ServerCommandSource source, String name) {
        ServerPlayerEntity player =  source.getPlayer();
        ServerPlayerEntity target =  Ifineess.SERVER.getPlayerManager().getPlayer(name);

        if(target == null) {
            source.sendFeedback(Text.of(Ifineess.PREFIX + "§c未找到该玩家"), false);
            return 0;
        }

        if (!Queue.containsKey(player) && !Queue.containsValue(player)) {
            Queue.put(target, player);
            target.sendMessage(Text.of(Ifineess.PREFIX + "§a"+ player.getName() +"§e请你传送到他的位置!\n§atpa accept接受\n§6tpa reject拒绝"));
            source.sendFeedback(Text.of(Ifineess.PREFIX + "§a成功发送传送请求"), false);
        } else source.sendFeedback(Text.of(Ifineess.PREFIX + "§6你有一个正在处理中的请求哦"), false);

        Timer timer = new Timer();
        timer.schedule(new timeOut(player, Queue),30000L);
        return 1;
    }

    private static int TpaAccept(ServerCommandSource source) {
        ServerPlayerEntity player =  source.getPlayer();
        ServerPlayerEntity k = null, v = null;
        Vec3d pos;

        for (Map.Entry<ServerPlayerEntity, ServerPlayerEntity> entry : Queue.entrySet()) {
            ServerPlayerEntity key = entry.getKey();
            ServerPlayerEntity value = entry.getValue();
            if (key == player || value == player) {
                k = key;
                v = value;
            }
        }

        if(k != null && v != null){
            pos = v.getPos();
            k.teleport(v.getWorld(), pos.getX(), pos.getY(), pos.getZ(), v.getYaw(), v.getPitch());
            Queue.remove(k);

        } else {
            source.sendFeedback(Text.of(Ifineess.PREFIX + "§6没有待处理的请求"), false);
        }

        return 1;
    }

    private static int TpaReject(ServerCommandSource source) {
        ServerPlayerEntity player =  source.getPlayer();
        ServerPlayerEntity pl = null, k = null;

        for (Map.Entry<ServerPlayerEntity, ServerPlayerEntity> entry : Queue.entrySet()) {
            ServerPlayerEntity key = entry.getKey();
            ServerPlayerEntity value = entry.getValue();
            if (key == player) {
                pl = value;
                k = key;
            }
            if (value == player) {
                pl = key;
                k = key;
            }
        }

        if(pl != null){
            pl.sendMessage(Text.of(Ifineess.PREFIX + "§6对方拒绝了你的请求"));
            Queue.remove(k);
            source.sendFeedback(Text.of(Ifineess.PREFIX + "§a拒绝成功"), false);
        } else {
            source.sendFeedback(Text.of(Ifineess.PREFIX + "§6没有待处理的请求"), false);
        }

        return 1;
    }
}


class timeOut extends TimerTask {

    private ServerPlayerEntity player, k = null, v = null;
    private HashMap<ServerPlayerEntity, ServerPlayerEntity> Queue;

    public timeOut(ServerPlayerEntity player, HashMap<ServerPlayerEntity, ServerPlayerEntity> Queue) {
        this.player = player;
        this.Queue = Queue;
    }

    @Override
    public void run() {
        Queue.forEach((key, value)->{
            if(key == player || value == player) {
                k = key;
                v = value;
            }
        });
        if(k != null && v != null){
            k.sendMessage(Text.of(Ifineess.PREFIX +"§6请求超时！"));
            v.sendMessage(Text.of(Ifineess.PREFIX +"§6请求超时！"));
            Queue.remove(k);
        }
    }
}