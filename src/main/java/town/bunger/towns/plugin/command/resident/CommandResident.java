package town.bunger.towns.plugin.command.resident;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.Command;
import org.incendo.cloud.context.CommandContext;
import town.bunger.towns.api.resident.Resident;
import town.bunger.towns.plugin.command.ResidentCommandBean;
import town.bunger.towns.plugin.util.InfoScreen;

import java.util.List;

import static net.kyori.adventure.text.Component.text;

public final class CommandResident<C extends CommandSender> extends ResidentCommandBean<C> {

    @Override
    protected Command.Builder<C> configure(Command.Builder<C> builder) {
        return builder;
    }

    @Override
    public void execute(@NonNull CommandContext<C> context) {
        final Resident resident = context.inject(Resident.class).orElse(null);
        if (resident == null) {
            context.sender().sendMessage(text("You are not a resident of any town.", NamedTextColor.RED));
            return;
        }

        final List<Component> components = InfoScreen.printResident(resident);
        for (final Component component : components) {
            context.sender().sendMessage(component);
        }
    }
}
