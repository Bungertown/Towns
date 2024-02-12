package town.bunger.towns.plugin.command.town;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.Command;
import org.incendo.cloud.context.CommandContext;
import town.bunger.towns.api.command.TownParser;
import town.bunger.towns.api.town.Town;
import town.bunger.towns.plugin.command.TownCommandBean;
import town.bunger.towns.plugin.util.TextBanner;

import static net.kyori.adventure.text.Component.text;

public final class CommandTownResidents extends TownCommandBean<CommandSender> {

    public static final Component BANNER = TextBanner.create("All Residents");

    @Override
    protected Command.Builder<CommandSender> configure(Command.Builder<CommandSender> builder) {
        return builder
            .literal("residents")
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
        for (String name : town.residentNames()) {
            context.sender().sendMessage(text("  " + name, NamedTextColor.GREEN));
        }
    }
}
