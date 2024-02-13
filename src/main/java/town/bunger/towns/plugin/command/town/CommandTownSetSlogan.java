package town.bunger.towns.plugin.command.town;

import org.bukkit.command.CommandSender;
import org.incendo.cloud.Command;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.parser.standard.StringParser;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import town.bunger.towns.api.town.Town;
import town.bunger.towns.plugin.command.TownCommandBean;

import java.util.concurrent.CompletableFuture;

import static town.bunger.towns.plugin.i18n.Messages.*;

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
        final Logger logger = context.inject(Logger.class).orElseThrow();
        final String slogan = context.<String>optional("slogan").orElse(null);

        final Town town = context.inject(Town.class).orElse(null);
        if (town == null) {
            context.sender().sendMessage(ERROR_TOWN_MEMBERSHIP_REQUIRED);
            return CompletableFuture.completedFuture(null);
        }

        return town.setSlogan(slogan)
            .whenComplete(($, ex) -> {
                if (ex == null) {
                    if (slogan != null) {
                        context.sender().sendMessage(NOTIFY_TOWN_SLOGAN_CHANGED_SELF(town.name(), slogan));
                        logger.info(context.sender().getName() + " set the slogan of " + town.name() + " to " + slogan);
                    } else {
                        context.sender().sendMessage(NOTIFY_TOWN_SLOGAN_CLEARED_SELF(town.name()));
                        logger.info(context.sender().getName() + " cleared the slogan of " + town.name());
                    }
                } else {
                    context.sender().sendMessage(ERROR_TOWN_SET_SLOGAN_FAILED(slogan));
                    logger.error("Failed to set the slogan of " + town.name() + " to " + slogan, ex);
                }
            });
    }
}
