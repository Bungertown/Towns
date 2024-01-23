package town.bunger.towns.plugin.command.town;

import cloud.commandframework.Command;
import cloud.commandframework.context.CommandContext;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import town.bunger.towns.api.resident.Resident;
import town.bunger.towns.plugin.command.TownCommandBean;
import town.bunger.towns.plugin.util.InfoScreen;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static net.kyori.adventure.text.Component.text;

public final class CommandTown<C extends CommandSender> extends TownCommandBean<C> {

    @Override
    protected Command.Builder<C> configure(Command.Builder<C> builder) {
        return builder;
    }

    @Override
    public CompletableFuture<@Nullable Void> executeFuture(@NonNull CommandContext<C> context) {
        final Logger logger = context.inject(Logger.class).orElseThrow();

        final Optional<Resident> residentOpt = context.inject(Resident.class);
        if (residentOpt.isEmpty()) {
            context.sender().sendMessage(text("Your resident data is not loaded.", NamedTextColor.RED));
            return CompletableFuture.completedFuture(null);
        }
        final Resident resident = residentOpt.get();

        return resident.loadTown()
            .thenAccept(town -> {
                if (town == null) {
                    context.sender().sendMessage(text("You are not in a town.", NamedTextColor.RED));
                    return;
                }
                final List<Component> components = InfoScreen.printTown(town);
                for (final Component component : components) {
                    context.sender().sendMessage(component);
                }
            })
            .exceptionally(ex -> {
                context.sender().sendMessage(text("Failed to load your town's data.", NamedTextColor.RED));
                logger.error("Failed to load town data for player " + context.sender().getName(), ex);
                return null;
            });
    }
}
