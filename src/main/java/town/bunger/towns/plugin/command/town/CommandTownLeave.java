package town.bunger.towns.plugin.command.town;

import org.bukkit.command.CommandSender;
import org.incendo.cloud.Command;
import org.incendo.cloud.context.CommandContext;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import town.bunger.towns.api.resident.Resident;
import town.bunger.towns.api.town.Town;
import town.bunger.towns.plugin.command.TownCommandBean;

import java.util.concurrent.CompletableFuture;

import static town.bunger.towns.plugin.i18n.Messages.*;

public final class CommandTownLeave extends TownCommandBean<CommandSender> {

    @Override
    protected Command.Builder<CommandSender> configure(Command.Builder<CommandSender> builder) {
        return builder
            .literal("leave");
    }

    @Override
    public CompletableFuture<@Nullable Void> executeFuture(CommandContext<CommandSender> context) {
        final Logger logger = context.inject(Logger.class).orElseThrow();

        final Resident resident = context.inject(Resident.class).orElse(null);
        if (resident == null) {
            context.sender().sendMessage(ERROR_RESIDENT_NOT_LOADED);
            return CompletableFuture.completedFuture(null);
        }
        if (!resident.hasTown()) {
            context.sender().sendMessage(ERROR_TOWN_MEMBERSHIP_REQUIRED);
            return CompletableFuture.completedFuture(null);
        }

        final Town town = resident.town();
        if (town == null) {
            context.sender().sendMessage(ERROR_TOWN_NOT_LOADED);
            return CompletableFuture.completedFuture(null);
        }

        if (town.ownerId().equals(resident.id())) {
            context.sender().sendMessage(ERROR_TOWN_OWNERSHIP_CANNOT_LEAVE);
            return CompletableFuture.completedFuture(null);
        }

        return resident.leaveTown()
            .thenAccept(left -> {
                if (left) {
                    town.sendMessage(NOTIFY_TOWN_LEFT_OTHER(town.name(), resident.name()));
                    context.sender().sendMessage(NOTIFY_TOWN_LEFT_SELF(town.name()));
                    logger.info(resident.name() + "left the town of " + town.name());
                } else {
                    context.sender().sendMessage(ERROR_TOWN_LEAVE_FAILED(town.name()));
                }
            })
            .exceptionally(ex -> {
                context.sender().sendMessage(ERROR_TOWN_LEAVE_FAILED(town.name()));
                logger.error("Failed to leave the town of " + town.name(), ex);
                return null;
            });
    }
}
