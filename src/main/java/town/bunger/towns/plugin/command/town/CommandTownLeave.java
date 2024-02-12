package town.bunger.towns.plugin.command.town;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.Command;
import org.incendo.cloud.context.CommandContext;
import org.jspecify.annotations.Nullable;
import town.bunger.towns.api.resident.Resident;
import town.bunger.towns.api.town.Town;
import town.bunger.towns.plugin.command.TownCommandBean;

import java.util.concurrent.CompletableFuture;

import static net.kyori.adventure.text.Component.text;

public final class CommandTownLeave extends TownCommandBean<CommandSender> {

    @Override
    protected Command.Builder<CommandSender> configure(Command.Builder<CommandSender> builder) {
        return builder
            .literal("leave");
    }

    @Override
    public CompletableFuture<@Nullable Void> executeFuture(CommandContext<CommandSender> context) {
        final Resident resident = context.inject(Resident.class).orElse(null);
        if (resident == null) {
            context.sender().sendMessage(text("Your resident data is not loaded.", NamedTextColor.RED));
            return CompletableFuture.completedFuture(null);
        }
        if (!resident.hasTown()) {
            context.sender().sendMessage(text("You are not a member of a town.", NamedTextColor.RED));
            return CompletableFuture.completedFuture(null);
        }

        final Town town = resident.town();
        if (town == null) {
            context.sender().sendMessage(text("Your town data is not loaded.", NamedTextColor.RED));
            return CompletableFuture.completedFuture(null);
        }

        if (town.ownerId().equals(resident.id())) {
            context.sender().sendMessage(text("You cannot leave your town as the owner. Use /town disband instead.", NamedTextColor.RED));
            return CompletableFuture.completedFuture(null);
        }

        return resident.leaveTown()
            .thenAccept(left -> {
                if (left) {
                    context.sender().sendMessage(text("You left your town.", NamedTextColor.GREEN));
                } else {
                    context.sender().sendMessage(text("You could not leave your town.", NamedTextColor.RED));
                }
            });
    }
}
