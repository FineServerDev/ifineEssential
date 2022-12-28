package eu.ifine.ifineess.form;

import eu.ifine.ifineess.Ifineess;

import net.minecraft.server.network.ServerPlayerEntity;
import org.geysermc.cumulus.form.CustomForm;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.floodgate.api.player.FloodgatePlayer;

import java.util.List;

public class WarpForm {
    public static void sendWarpFormMain(FloodgatePlayer player){
        player.sendForm(SimpleForm.builder()
                .title("§d传送点系统")
                .content("§b请选择你要操作的类型")
                .button("§l§6传送到一个传送点")
                .button("§l§a创建一个传送点")
                .button("§l§c删除一个传送点")
                .button("§l§e查看传送点列表")
                .validResultHandler(response -> {
                    if (response.clickedButtonId() == 0) {
                        sendWarpFormGoHome(player);
                    } else if (response.clickedButtonId() == 1) {
                        sendWarpFormAddHome(player);
                    } else if (response.clickedButtonId() == 2) {
                        sendWarpFormDelHome(player);
                    } else if (response.clickedButtonId() == 3) {
                        sendWarpFormListHome(player);
                    }
                }).build());
    }

    private static void sendWarpFormGoHome(FloodgatePlayer player){
        SimpleForm.Builder sim =  SimpleForm.builder();
        sim.title("§d传送到一个传送点");
        List<String> homelist = Ifineess.warpMap.keySet().stream().toList();
        homelist.forEach(sim::button);
        player.sendForm(sim.validResultHandler(response -> {
            homelist.forEach(s -> {
                if (response.clickedButtonId() == homelist.indexOf(s)) {
                    ServerPlayerEntity pl =  Ifineess.SERVER.getPlayerManager().getPlayer(player.getJavaUniqueId());
                    if(pl != null){
                        Ifineess.SERVER.getCommandManager().executeWithPrefix(pl.getCommandSource(), "/warp tp " + s);
                    }
                }
            });
        }).build());
    }

    private static void sendWarpFormAddHome(FloodgatePlayer player){
        ServerPlayerEntity pl =  Ifineess.SERVER.getPlayerManager().getPlayer(player.getJavaUniqueId());
        if(pl != null) {
            if (!pl.getCommandSource().hasPermissionLevel(2)) {
                CustomForm.Builder sim = CustomForm.builder();
                sim.title("§4你没有权限创建传送点！");
                sim.label("§c你没有权限创建传送点！");
                player.sendForm(sim.build());
                return;
            }
            CustomForm.Builder cust = CustomForm.builder();
            cust.title("§d创建一个传送点");
            cust.input("§l§6请输入传送点的名字", "传送点的名字");

            player.sendForm(cust.validResultHandler(response -> {
                String name = response.asInput(0);
                if (pl != null) {
                    Ifineess.SERVER.getCommandManager().executeWithPrefix(pl.getCommandSource(), "/warp set " + name);
                }
            }).build());
        }
    }

    private static void sendWarpFormDelHome(FloodgatePlayer player){
        ServerPlayerEntity pl =  Ifineess.SERVER.getPlayerManager().getPlayer(player.getJavaUniqueId());
        if (pl != null) {
            if (!pl.getCommandSource().hasPermissionLevel(2)) {
                CustomForm.Builder sim = CustomForm.builder();
                sim.title("§4你没有权限删除传送点！");
                sim.label("§c你没有权限删除传送点！");
                player.sendForm(sim.build());
                return;
            }
            SimpleForm.Builder sim = SimpleForm.builder();
            sim.title("§d删除一个传送点");
            List<String> warplist = Ifineess.warpMap.keySet().stream().toList();
            warplist.forEach(sim::button);
            player.sendForm(sim.validResultHandler(response -> {
                warplist.forEach(s -> {
                    if (response.clickedButtonId() == warplist.indexOf(s)) {
                        Ifineess.SERVER.getCommandManager().executeWithPrefix(pl.getCommandSource(), "/warp remove " + s);
                    }
                });
            }).build());
        }
    }
    private  static void sendWarpFormListHome(FloodgatePlayer player){
        CustomForm.Builder sim =  CustomForm.builder();
        sim.title("§d查看传送点");
        Ifineess.warpMap.forEach((s, warp) -> {
            sim.label("§a名字: §b" + s + " §a| 位置: §5" + warp.getPosition().x + " " + warp.getPosition().y + " " + warp.getPosition().z + " §a| 世界：§6" + warp.getDimension().getValue().toString());
        });
        player.sendForm(sim.build());
    }

}