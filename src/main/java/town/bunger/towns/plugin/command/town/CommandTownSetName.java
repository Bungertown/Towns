package town.bunger.towns.plugin.command.town;

import org.bukkit.command.CommandSender;
import org.incendo.cloud.Command;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.parser.standard.StringParser;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import town.bunger.towns.api.BungerTowns;
import town.bunger.towns.api.town.Town;
import town.bunger.towns.plugin.command.TownCommandBean;
import town.bunger.towns.plugin.util.Notifications;

import java.util.concurrent.CompletableFuture;

import static town.bunger.towns.plugin.i18n.Messages.*;

public final class CommandTownSetName extends TownCommandBean<CommandSender> {

    @Override
    protected Command.Builder<CommandSender> configure(Command.Builder<CommandSender> builder) {
        return builder
            .literal("set")
            .literal("name")
            .required("name", StringParser.stringParser());
    }

    @Override
    public CompletableFuture<@Nullable Void> executeFuture(
        CommandContext<CommandSender> context
    ) {
        final Logger logger = context.inject(Logger.class).orElseThrow();
        final String newName = context.get("name");

        final Town town = context.inject(Town.class).orElse(null);
        if (town == null) {
            context.sender().sendMessage(ERROR_TOWN_MEMBERSHIP_REQUIRED);
            return CompletableFuture.completedFuture(null);
        }
        final String oldName = town.name();

        final BungerTowns api = context.inject(BungerTowns.class).orElseThrow();
        if (api.towns().contains(newName)) {
            context.sender().sendMessage(ERROR_TOWN_NAME_TAKEN);
            return CompletableFuture.completedFuture(null);
        }

        return town.setName(newName)
            .whenComplete(($, ex) -> {
                if (ex == null) {
                    Notifications.TOWN_RENAMED.sendMessage(NOTIFY_TOWN_RENAMED_OTHER(oldName, newName, context.sender().getName()));
                    context.sender().sendMessage(NOTIFY_TOWN_RENAMED_SELF(oldName, newName));
                    logger.info(context.sender().getName() + " renamed the town of " + oldName + " to " + newName);
                } else {
                    context.sender().sendMessage(ERROR_TOWN_SET_NAME_FAILED(newName));
                    logger.error("Failed to rename the town of " + oldName + " to " + newName, ex);
                }
            });
    }
}
