package town.bunger.towns.plugin.command.town;

import net.kyori.adventure.text.format.NamedTextColor;
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

import java.util.concurrent.CompletableFuture;

import static net.kyori.adventure.text.Component.text;

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
            context.sender().sendMessage(text("Your resident data is not loaded.", NamedTextColor.RED));
            return CompletableFuture.completedFuture(null);
        }
        if (resident.hasTown()) {
            context.sender().sendMessage(text("You must leave your current town before creating a new one.", NamedTextColor.RED));
            return CompletableFuture.completedFuture(null);
        }

        final Town.Builder builder = api.towns().builder()
            .name(name)
            .owner(resident);
        return api.towns().create(builder)
            .thenAccept(town -> {
                context.sender().sendMessage(text("Created town " + name, NamedTextColor.GREEN));
            })
            .exceptionally(ex -> {
                context.sender().sendMessage(text("Failed to create town " + name, NamedTextColor.RED));
                logger.error("Failed to create town " + name, ex);
                return null;
            });
    }
}
