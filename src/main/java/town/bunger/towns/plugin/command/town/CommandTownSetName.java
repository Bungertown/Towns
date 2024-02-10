package town.bunger.towns.plugin.command.town;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.Command;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.parser.standard.StringParser;
import town.bunger.towns.api.BungerTowns;
import town.bunger.towns.api.town.Town;
import town.bunger.towns.plugin.command.TownCommandBean;

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
    public void execute(
        CommandContext<CommandSender> context
    ) {
        final String name = context.get("name");

        final Town town = context.inject(Town.class).orElse(null);
        if (town == null) {
            context.sender().sendMessage(text("You are not in a town.", NamedTextColor.RED));
            return;
        }

        final BungerTowns api = context.inject(BungerTowns.class).orElseThrow();
        if (api.towns().contains(name)) {
            context.sender().sendMessage(text("A town with that name already exists.", NamedTextColor.RED));
            return;
        }

        town.setName(name);
        context.sender().sendMessage(text("Set name to '" + name + "'", NamedTextColor.GREEN));
    }
}
