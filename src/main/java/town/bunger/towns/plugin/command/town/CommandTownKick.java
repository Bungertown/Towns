package town.bunger.towns.plugin.command.town;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.Command;
import org.incendo.cloud.context.CommandContext;
import org.jspecify.annotations.Nullable;
import town.bunger.towns.api.command.ResidentParser;
import town.bunger.towns.api.resident.Resident;
import town.bunger.towns.api.town.Town;
import town.bunger.towns.plugin.command.TownCommandBean;

import java.util.concurrent.CompletableFuture;

import static net.kyori.adventure.text.Component.text;

public final class CommandTownKick extends TownCommandBean<CommandSender> {

    @Override
    protected Command.Builder<CommandSender> configure(Command.Builder<CommandSender> builder) {
        return builder
            .literal("kick")
            .required("resident", ResidentParser.sameTown());
    }

    @Override
    public CompletableFuture<@Nullable Void> executeFuture(CommandContext<CommandSender> context) {
        final Town town = context.inject(Town.class).orElse(null);
        if (town == null) {
            context.sender().sendMessage(text("You are not a member of a town.", NamedTextColor.RED));
            return CompletableFuture.completedFuture(null);
        }

        final Resident resident = context.get("resident");

        return town.kick(resident)
            .thenAccept(kicked -> {
                if (kicked) {
                    resident.sendMessage(text("You were kicked from your town.", NamedTextColor.RED));
                    context.sender().sendMessage(text("You kicked " + resident.name() + " from your town.", NamedTextColor.GREEN));
                } else {
                    context.sender().sendMessage(text("You could not kick " + resident.name() + " from your town.", NamedTextColor.RED));
                }
            });
    }
}
