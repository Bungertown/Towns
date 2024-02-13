package town.bunger.towns.plugin.command.town;

import org.bukkit.command.CommandSender;
import org.incendo.cloud.Command;
import org.incendo.cloud.context.CommandContext;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import town.bunger.towns.api.command.TownParser;
import town.bunger.towns.api.resident.Resident;
import town.bunger.towns.api.town.Town;
import town.bunger.towns.plugin.command.TownCommandBean;

import java.util.concurrent.CompletableFuture;

import static town.bunger.towns.plugin.i18n.Messages.*;

public final class CommandTownJoin extends TownCommandBean<CommandSender> {

    @Override
    protected Command.Builder<CommandSender> configure(Command.Builder<CommandSender> builder) {
        return builder
            .literal("join")
            .required("town", TownParser.of());
    }

    @Override
    public CompletableFuture<@Nullable Void> executeFuture(CommandContext<CommandSender> context) {
        final Logger logger = context.inject(Logger.class).orElseThrow();

        final Resident resident = context.inject(Resident.class).orElse(null);
        if (resident == null) {
            context.sender().sendMessage(ERROR_RESIDENT_NOT_LOADED);
            return CompletableFuture.completedFuture(null);
        }
        if (resident.hasTown()) {
            context.sender().sendMessage(ERROR_TOWN_MUST_LEAVE_FIRST);
            return CompletableFuture.completedFuture(null);
        }

        final Town town = context.get("town");

        if (!town.isOpen()) {
            context.sender().sendMessage(ERROR_TOWN_NOT_OPEN);
            return CompletableFuture.completedFuture(null);
        }

        return resident.joinTown(town)
            .thenAccept(joined -> {
                if (joined) {
                    town.sendMessage(NOTIFY_TOWN_JOINED_OTHER(town.name(), resident.name()));
                    context.sender().sendMessage(NOTIFY_TOWN_JOINED_SELF(town.name()));
                    logger.info(resident.name() + " joined the town of " + town.name());
                } else {
                    context.sender().sendMessage(ERROR_TOWN_JOIN_FAILED(town.name()));
                }
            })
            .exceptionally(ex -> {
                context.sender().sendMessage(ERROR_TOWN_JOIN_FAILED(town.name()));
                logger.error("Failed to join the town of " + town.name(), ex);
                return null;
            });
    }
}
