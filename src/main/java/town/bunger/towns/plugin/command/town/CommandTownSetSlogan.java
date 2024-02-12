package town.bunger.towns.plugin.command.town;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.Command;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.parser.standard.StringParser;
import org.jspecify.annotations.Nullable;
import town.bunger.towns.api.town.Town;
import town.bunger.towns.plugin.command.TownCommandBean;

import java.util.concurrent.CompletableFuture;

import static net.kyori.adventure.text.Component.text;

public final class CommandTownSetSlogan extends TownCommandBean<CommandSender> {

    @Override
    protected Command.Builder<CommandSender> configure(Command.Builder<CommandSender> builder) {
        return builder
            .literal("set")
            .literal("slogan")
            .optional("slogan", StringParser.greedyStringParser());
    }

    @Override
    public CompletableFuture<@Nullable Void> executeFuture(
        CommandContext<CommandSender> context
    ) {
        final String slogan = context.<String>optional("slogan").orElse(null);

        final Town town = context.inject(Town.class).orElse(null);
        if (town == null) {
            context.sender().sendMessage(text("You are not in a town.", NamedTextColor.RED));
            return CompletableFuture.completedFuture(null);
        }

        return town.setSlogan(slogan)
            .whenComplete(($, ex) -> {
                if (ex != null) {
                    context.sender().sendMessage(text("Failed to set town slogan", NamedTextColor.RED));
                } else {
                    if (slogan != null) {
                        context.sender().sendMessage(text("Set town slogan to '" + slogan + "'", NamedTextColor.GREEN));
                    } else {
                        context.sender().sendMessage(text("Removed town slogan.", NamedTextColor.GREEN));
                    }
                }
            });
    }
}
