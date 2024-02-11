package town.bunger.towns.plugin.command.town;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.util.TriState;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.Command;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.parser.standard.BooleanParser;
import org.jspecify.annotations.Nullable;
import town.bunger.towns.api.town.Town;
import town.bunger.towns.plugin.command.TownCommandBean;

import java.util.concurrent.CompletableFuture;

import static net.kyori.adventure.text.Component.text;

public final class CommandTownSetOpen extends TownCommandBean<CommandSender> {

    @Override
    protected Command.Builder<? extends CommandSender> configure(Command.Builder<CommandSender> builder) {
        return builder
            .literal("set")
            .literal("open")
            .optional("open", BooleanParser.booleanParser());
    }

    @Override
    public CompletableFuture<@Nullable Void> executeFuture(CommandContext<CommandSender> context) {
        final TriState openArg = TriState.byBoolean(context.<Boolean>optional("open").orElse(null));
        final Town town = context.inject(Town.class).orElse(null);
        if (town == null) {
            context.sender().sendMessage("You are not in a town.");
            return CompletableFuture.completedFuture(null);
        }

        final boolean open = openArg.toBooleanOrElse(!town.isOpen());
        return town.setOpen(open)
            .thenAccept($ -> {
                context.sender().sendMessage(text("Set open status to " + open, NamedTextColor.GREEN));
            })
            .exceptionally($ -> {
                context.sender().sendMessage(text("Failed to set open status", NamedTextColor.RED));
                return null;
            });
    }
}
