package town.bunger.towns.plugin.command.town;

import org.bukkit.command.CommandSender;
import org.incendo.cloud.Command;
import org.incendo.cloud.context.CommandContext;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import town.bunger.towns.api.resident.Resident;
import town.bunger.towns.api.town.Town;
import town.bunger.towns.plugin.command.TownCommandBean;
import town.bunger.towns.plugin.util.Notifications;

import java.util.concurrent.CompletableFuture;

import static town.bunger.towns.plugin.i18n.Messages.*;

public final class CommandTownDelete extends TownCommandBean<CommandSender> {

    @Override
    protected Command.Builder<CommandSender> configure(Command.Builder<CommandSender> builder) {
        return builder
            .literal("delete", "disband");
    }

    @Override
    public CompletableFuture<@Nullable Void> executeFuture(CommandContext<CommandSender> context) {
        final Logger logger = context.inject(Logger.class).orElseThrow();

        final Resident resident = context.inject(Resident.class).orElse(null);
        if (resident == null) {
            context.sender().sendMessage(ERROR_RESIDENT_NOT_LOADED);
            return CompletableFuture.completedFuture(null);
        }

        final Town town = context.inject(Town.class).orElse(null);
        if (town == null) {
            context.sender().sendMessage(ERROR_TOWN_MEMBERSHIP_REQUIRED);
            return CompletableFuture.completedFuture(null);
        }
        final String townName = town.name();

        if (!town.ownerId().equals(resident.id())) {
            context.sender().sendMessage(ERROR_TOWN_OWNERSHIP_REQUIRED);
            return CompletableFuture.completedFuture(null);
        }

        return town.delete()
            .thenAccept(deleted -> {
                if (deleted) {
                    Notifications.TOWN_DELETED.sendMessage(NOTIFY_TOWN_DELETED_OTHER(townName, resident.name()));
                    context.sender().sendMessage(NOTIFY_TOWN_DELETED_SELF(townName));
                    logger.info(resident.name() + " deleted the town of " + townName);
                } else {
                    context.sender().sendMessage(ERROR_TOWN_DELETE_FAILED(townName));
                }
            })
            .exceptionally(ex -> {
                context.sender().sendMessage(ERROR_TOWN_DELETE_FAILED(townName));
                logger.error("Failed to delete the town of " + townName, ex);
                return null;
            });
    }
}
