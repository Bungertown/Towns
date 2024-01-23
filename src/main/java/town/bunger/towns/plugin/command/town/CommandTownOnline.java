package town.bunger.towns.plugin.command.town;

import cloud.commandframework.Command;
import cloud.commandframework.context.CommandContext;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import town.bunger.towns.api.command.TownParser;
import town.bunger.towns.api.town.Town;
import town.bunger.towns.plugin.command.TownCommandBean;
import town.bunger.towns.plugin.util.TextBanner;

import static net.kyori.adventure.text.Component.text;

public final class CommandTownOnline extends TownCommandBean<CommandSender> {

    public static final Component BANNER = TextBanner.create("Online Residents");

    @Override
    protected Command.Builder<CommandSender> configure(Command.Builder<CommandSender> builder) {
        return builder
            .literal("online")
            .optional("town", TownParser.of());
    }

    @Override
    public void execute(
        CommandContext<CommandSender> context
    ) {
        final Town town = context.<Town>optional("town")
            .or(() -> context.inject(Town.class))
            .orElse(null);
        if (town == null) {
            context.sender().sendMessage(text("Must specify a town.", NamedTextColor.RED));
            return;
        }

        context.sender().sendMessage(BANNER);
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (town.hasResident(player.getUniqueId())) {
                context.sender().sendMessage(text("  " + player.getName(), NamedTextColor.GREEN));
            }
        }
    }
}
