package town.bunger.towns.plugin.command.town;

import cloud.commandframework.Command;
import cloud.commandframework.arguments.standard.StringParser;
import cloud.commandframework.context.CommandContext;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.jspecify.annotations.Nullable;
import town.bunger.towns.api.BungerTowns;
import town.bunger.towns.api.resident.Resident;
import town.bunger.towns.plugin.command.TownCommandBean;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static net.kyori.adventure.text.Component.text;

public final class CommandTownSetName extends TownCommandBean<CommandSender> {

    @Override
    protected Command.Builder<CommandSender> configure(Command.Builder<CommandSender> builder) {
        return builder
            .literal("set")
            .literal("name")
            .required("name", StringParser.stringParser());
    }

    @Override
    public CompletableFuture<@Nullable Void> executeFuture(
        CommandContext<CommandSender> context
    ) {
        final String name = context.get("name");
        final Optional<Resident> residentOpt = context.inject(Resident.class);
        if (residentOpt.isEmpty()) {
            context.sender().sendMessage(text("Your resident data is not loaded.", NamedTextColor.RED));
            return CompletableFuture.completedFuture(null);
        }
        final Resident resident = residentOpt.get();
        final BungerTowns api = context.inject(BungerTowns.class).orElseThrow();

        if (api.towns().contains(name)) {
            context.sender().sendMessage(text("A town with that name already exists.", NamedTextColor.RED));
            return CompletableFuture.completedFuture(null);
        }

        return resident.loadTown()
            .thenAccept(town -> {
                if (town == null) {
                    context.sender().sendMessage(text("You are not in a town.", NamedTextColor.RED));
                    return;
                }
                town.setName(name);
                context.sender().sendMessage(text("Set name to '" + name + "'", NamedTextColor.GREEN));
            });
    }
}
