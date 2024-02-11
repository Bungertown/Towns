package town.bunger.towns.plugin.command.town;

import org.incendo.cloud.Command;
import org.incendo.cloud.context.CommandContext;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.jspecify.annotations.Nullable;
import town.bunger.towns.api.command.TownParser;
import town.bunger.towns.api.resident.Resident;
import town.bunger.towns.api.town.Town;
import town.bunger.towns.plugin.command.TownCommandBean;

import java.util.concurrent.CompletableFuture;

import static net.kyori.adventure.text.Component.text;

public final class CommandTownJoin extends TownCommandBean<CommandSender> {

    @Override
    protected Command.Builder<CommandSender> configure(Command.Builder<CommandSender> builder) {
        return builder
            .literal("join")
            .required("town", TownParser.of());
    }

    @Override
    public CompletableFuture<@Nullable Void> executeFuture(CommandContext<CommandSender> context) {
        final Resident resident = context.inject(Resident.class).orElse(null);
        if (resident == null) {
            context.sender().sendMessage(text("Your resident data is not loaded.", NamedTextColor.RED));
            return CompletableFuture.completedFuture(null);
        }
        if (resident.hasTown()) {
            context.sender().sendMessage(text("You are already a member of a town.", NamedTextColor.RED));
            return CompletableFuture.completedFuture(null);
        }

        final Town town = context.get("town");

        if (!town.isOpen()) {
            context.sender().sendMessage(text("This town is not open to new members.", NamedTextColor.RED));
            return CompletableFuture.completedFuture(null);
        }

        return resident.joinTown(town)
            .thenAccept(joined -> {
                if (joined) {
                    context.sender().sendMessage(text("You joined the town " + town.name() + ".", NamedTextColor.GREEN));
                } else {
                    context.sender().sendMessage(text("You could not join the town " + town.name() + ".", NamedTextColor.RED));
                }
            });
    }
}
