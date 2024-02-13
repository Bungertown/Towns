package town.bunger.towns.plugin.command.town;

import org.bukkit.command.CommandSender;
import org.incendo.cloud.Command;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.parser.standard.StringParser;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import town.bunger.towns.api.BungerTowns;
import town.bunger.towns.api.resident.Resident;
import town.bunger.towns.api.town.Town;
import town.bunger.towns.plugin.command.TownCommandBean;
import town.bunger.towns.plugin.util.Notifications;

import java.util.concurrent.CompletableFuture;

import static town.bunger.towns.plugin.i18n.Messages.*;

public final class CommandTownCreate extends TownCommandBean<CommandSender> {

    @Override
    protected Command.Builder<CommandSender> configure(Command.Builder<CommandSender> builder) {
        return builder
            .literal("create", "new")
            .required("name", StringParser.stringParser());
    }

    @Override
    public CompletableFuture<@Nullable Void> executeFuture(
        CommandContext<CommandSender> context
    ) {
        final String name = context.get("name");
        final BungerTowns api = context.inject(BungerTowns.class).orElseThrow();
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
        if (api.towns().contains(name)) {
            context.sender().sendMessage(ERROR_TOWN_NAME_TAKEN);
            return CompletableFuture.completedFuture(null);
        }
        final Town.Builder builder = api.towns().builder()
            .name(name)
            .owner(resident);
        return api.towns().create(builder)
            .thenAccept(town -> {
                Notifications.TOWN_CREATED.sendMessage(NOTIFY_TOWN_CREATED_OTHER(name, resident.name()));
                context.sender().sendMessage(NOTIFY_TOWN_CREATED_SELF(name));
                logger.info(resident.name() + " created the town of " + name);
            })
            .exceptionally(ex -> {
                context.sender().sendMessage(ERROR_TOWN_CREATE_FAILED(name));
                logger.error("Failed to create the town of " + name, ex);
                return null;
            });
    }
}
