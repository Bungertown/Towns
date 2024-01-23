package town.bunger.towns.plugin.command.town;

import cloud.commandframework.Command;
import cloud.commandframework.context.CommandContext;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;
import town.bunger.towns.api.command.TownParser;
import town.bunger.towns.api.resident.Resident;
import town.bunger.towns.api.town.Town;
import town.bunger.towns.plugin.command.TownCommandBean;
import town.bunger.towns.plugin.util.TextBanner;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static net.kyori.adventure.text.Component.text;

public final class CommandTownOnline extends TownCommandBean<CommandSender> {

    public static final Component BANNER = TextBanner.create("Online Players");

    @Override
    protected Command.Builder<CommandSender> configure(Command.Builder<CommandSender> builder) {
        return builder
            .literal("online")
            .optional("town", TownParser.of());
    }

    @Override
    public CompletableFuture<@Nullable Void> executeFuture(
        CommandContext<CommandSender> context
    ) {
        final Town townArg = context.<Town>optional("town").orElse(null);
        final CompletableFuture<? extends @Nullable Town> townFuture;
        if (townArg != null) {
            townFuture = CompletableFuture.completedFuture(townArg);
        } else {
            final Optional<Resident> residentOpt = context.inject(Resident.class);
            if (residentOpt.isEmpty()) {
                context.sender().sendMessage(text("Your resident data is not loaded.", NamedTextColor.RED));
                return CompletableFuture.completedFuture(null);
            }
            final Resident resident = residentOpt.get();
            townFuture = resident.loadTown();
        }

        return townFuture
            .thenAccept(town -> {
                if (town == null) {
                    context.sender().sendMessage(text("You are not in a town.", NamedTextColor.RED));
                    return;
                }
                context.sender().sendMessage(BANNER);
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (town.hasResident(player.getUniqueId())) {
                        context.sender().sendMessage(text("  " + player.getName(), NamedTextColor.GREEN));
                    }
                }
            });
    }
}
