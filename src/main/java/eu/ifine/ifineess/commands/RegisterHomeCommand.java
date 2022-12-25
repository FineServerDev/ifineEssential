package eu.ifine.ifineess.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;

import java.util.function.Predicate;

public class RegisterHomeCommand {

    public static void registerCommands(CommandDispatcher dispatcher) {
        Predicate<ServerCommandSource> isPlayer = source -> {
            try {
                return source.getPlayerOrThrow() != null;
            } catch(CommandSyntaxException e) {
                return false;
            }
        };

    }
}
