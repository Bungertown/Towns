package town.bunger.towns.plugin.command.town;

import org.bukkit.command.CommandSender;
import org.incendo.cloud.Command;
import org.incendo.cloud.context.CommandContext;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import town.bunger.towns.api.command.ResidentParser;
import town.bunger.towns.api.resident.Resident;
import town.bunger.towns.api.town.Town;
import town.bunger.towns.plugin.command.TownCommandBean;

import java.util.concurrent.CompletableFuture;

import static town.bunger.towns.plugin.i18n.Messages.*;

public final class CommandTownKick extends TownCommandBean<CommandSender> {

    @Override
    protected Command.Builder<CommandSender> configure(Command.Builder<CommandSender> builder) {
        return builder
            .literal("kick")
            .required("resident", ResidentParser.sameTown());
    }

    @Override
    public CompletableFuture<@Nullable Void> executeFuture(CommandContext<CommandSender> context) {
        final Logger logger = context.inject(Logger.class).orElseThrow();

        final Resident source = context.inject(Resident.class).orElse(null);
        final String sourceName = source != null ? source.name() : "$CONSOLE";

        final Town town = context.inject(Town.class).orElse(null);
        if (town == null) {
            context.sender().sendMessage(ERROR_TOWN_MEMBERSHIP_REQUIRED);
            return CompletableFuture.completedFuture(null);
        }

        final Resident target = context.get("resident");

        return town.kick(target)
            .thenAccept(kicked -> {
                if (kicked) {
                    town.sendMessage(NOTIFY_TOWN_KICKED_OTHER(town.name(), target.name(), sourceName));
                    context.sender().sendMessage(NOTIFY_TOWN_KICKED_SOURCE(town.name(), target.name()));
                    target.sendMessage(NOTIFY_TOWN_KICKED_TARGET(town.name()));
                    logger.info(sourceName + " kicked " + target.name() + " from the town of " + town.name());
                } else {
                    context.sender().sendMessage(ERROR_TOWN_KICK_FAILED(town.name(), target.name()));
                }
            })
            .exceptionally(ex -> {
                context.sender().sendMessage(ERROR_TOWN_KICK_FAILED(town.name(), target.name()));
                logger.error("Failed to kick " + target.name() + " from the town of " + town.name(), ex);
                return null;
            });
    }
}
