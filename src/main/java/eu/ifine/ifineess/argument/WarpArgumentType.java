package eu.ifine.ifineess.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import eu.ifine.ifineess.Ifineess;
import eu.ifine.ifineess.leveldb.Warp;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.ColorArgumentType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class WarpArgumentType implements ArgumentType<Warp> {
    private static final Collection<String> EXAMPLES = Arrays.asList("a", "b");
    public static final DynamicCommandExceptionType INVALID_WARP_EXCEPTION = new DynamicCommandExceptionType((warp) -> {
        return Text.translatable("argument.warp.invalid", new Object[]{warp});
    });
    public WarpArgumentType() {
    }

    public static WarpArgumentType warp() {
        return new WarpArgumentType();
    }

    public static <S> Warp getWarp( CommandContext<S> context,String name) {
        return context.getArgument(name, Warp.class);
    }

    @Override
    public Warp parse(StringReader stringReader) throws CommandSyntaxException {
        String warp = stringReader.readUnquotedString();
        for (Warp warp1 : Ifineess.warpMap.values()) {
            if (warp1.getName().contains(warp)) {
                return warp1;
            }
        }
        throw INVALID_WARP_EXCEPTION.create(warp);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        Ifineess.warpMap.keySet().forEach(builder::suggest);
        return CommandSource.suggestMatching(Ifineess.warpMap.keySet(), builder);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
