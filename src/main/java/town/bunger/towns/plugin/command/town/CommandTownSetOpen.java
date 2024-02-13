package town.bunger.towns.plugin.command.town;

import net.kyori.adventure.util.TriState;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.Command;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.parser.standard.BooleanParser;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import town.bunger.towns.api.town.Town;
import town.bunger.towns.plugin.command.TownCommandBean;
import town.bunger.towns.plugin.util.Notifications;

import java.util.concurrent.CompletableFuture;

import static town.bunger.towns.plugin.i18n.Messages.*;

public final class CommandTownSetOpen extends TownCommandBean<CommandSender> {

    @Override
    protected Command.Builder<? extends CommandSender> configure(Command.Builder<CommandSender> builder) {
        return builder
            .literal("set")
            .literal("open")
            .optional("open", BooleanParser.booleanParser());
    }

    @Override
    public CompletableFuture<@Nullable Void> executeFuture(CommandContext<CommandSender> context) {
        final Logger logger = context.inject(Logger.class).orElseThrow();

        final TriState openArg = TriState.byBoolean(context.<Boolean>optional("open").orElse(null));
        final Town town = context.inject(Town.class).orElse(null);
        if (town == null) {
            context.sender().sendMessage(ERROR_TOWN_MEMBERSHIP_REQUIRED);
            return CompletableFuture.completedFuture(null);
        }

        final boolean open = openArg.toBooleanOrElse(!town.isOpen());
        return town.setOpen(open)
            .whenComplete(($, ex) -> {
                if (ex == null) {
                    Notifications.TOWN_OPENED.sendMessage(NOTIFY_TOWN_OPENED_OTHER(open, town.name(), context.sender().getName()));
                    context.sender().sendMessage(NOTIFY_TOWN_OPENED_SELF(open, town.name()));
                    logger.info(context.sender().getName() + " set the open status of " + town.name() + " to " + open);
                } else {
                    context.sender().sendMessage(ERROR_TOWN_SET_OPEN_FAILED(open));
                    logger.error("Failed to set the open status of " + town.name() + " to " + open, ex);
                }
            });
    }
}
