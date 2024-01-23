package town.bunger.towns.plugin.command.town;

import cloud.commandframework.Command;
import cloud.commandframework.arguments.standard.StringParser;
import cloud.commandframework.context.CommandContext;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.jspecify.annotations.Nullable;
import town.bunger.towns.api.resident.Resident;
import town.bunger.towns.plugin.command.TownCommandBean;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static net.kyori.adventure.text.Component.text;

public final class CommandTownSetSlogan extends TownCommandBean<CommandSender> {

    @Override
    protected Command.Builder<CommandSender> configure(Command.Builder<CommandSender> builder) {
        return builder
            .literal("set")
            .literal("slogan")
            .optional("slogan", StringParser.greedyStringParser());
    }

    @Override
    public CompletableFuture<@Nullable Void> executeFuture(
        CommandContext<CommandSender> context
    ) {
        final String slogan = context.<String>optional("slogan").orElse(null);
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
                town.setSlogan(slogan);
                if (slogan != null) {
                    context.sender().sendMessage(text("Set slogan to '" + slogan + "'", NamedTextColor.GREEN));
                } else {
                    context.sender().sendMessage(text("Removed slogan.", NamedTextColor.GREEN));
                }
            });
    }
}
