package town.bunger.towns.plugin.command.town;

import org.incendo.cloud.Command;
import org.incendo.cloud.context.CommandContext;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.jspecify.annotations.Nullable;
import town.bunger.towns.api.resident.Resident;
import town.bunger.towns.api.town.Town;
import town.bunger.towns.plugin.command.TownCommandBean;

import java.util.concurrent.CompletableFuture;

import static net.kyori.adventure.text.Component.text;

public final class CommandTownDisband extends TownCommandBean<CommandSender> {

    @Override
    protected Command.Builder<CommandSender> configure(Command.Builder<CommandSender> builder) {
        return builder
            .literal("disband");
    }

    @Override
    public CompletableFuture<@Nullable Void> executeFuture(CommandContext<CommandSender> context) {
        final Town town = context.inject(Town.class).orElse(null);
        if (town == null) {
            context.sender().sendMessage(text("You are not a member of a town.", NamedTextColor.RED));
            return CompletableFuture.completedFuture(null);
        }

        final Resident resident = context.inject(Resident.class).orElse(null);
        if (resident == null) {
            context.sender().sendMessage(text("Your resident data is not loaded.", NamedTextColor.RED));
            return CompletableFuture.completedFuture(null);
        }

        if (!town.ownerId().equals(resident.id())) {
            context.sender().sendMessage(text("You are not the owner of this town.", NamedTextColor.RED));
            return CompletableFuture.completedFuture(null);
        }

        return town.delete()
            .thenAccept(deleted -> {
                if (deleted) {
                    context.sender().sendMessage(text("Deleted town " + town.name(), NamedTextColor.GREEN));
                } else {
                    context.sender().sendMessage(text("Failed to delete town " + town.name(), NamedTextColor.RED));
                }
            });
    }
}
