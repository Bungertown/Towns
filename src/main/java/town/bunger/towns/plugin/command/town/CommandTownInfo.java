package town.bunger.towns.plugin.command.town;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.Command;
import org.incendo.cloud.context.CommandContext;
import town.bunger.towns.api.command.TownParser;
import town.bunger.towns.api.town.Town;
import town.bunger.towns.plugin.command.TownCommandBean;
import town.bunger.towns.plugin.util.InfoScreen;

import java.util.List;

import static net.kyori.adventure.text.Component.text;

public final class CommandTownInfo<C extends Audience> extends TownCommandBean<C> {

    @Override
    protected Command.Builder<C> configure(Command.Builder<C> builder) {
        return builder.literal("info", "i")
            .optional("town", TownParser.of());
    }

    @Override
    public void execute(@NonNull CommandContext<C> context) {
        final Town town = context.<Town>optional("town")
            .or(() -> context.inject(Town.class))
            .orElse(null);
        if (town == null) {
            context.sender().sendMessage(text("Must specify a town.", NamedTextColor.RED));
            return;
        }

        final List<Component> components = InfoScreen.printTown(town);
        for (final Component component : components) {
            context.sender().sendMessage(component);
        }
    }
}
