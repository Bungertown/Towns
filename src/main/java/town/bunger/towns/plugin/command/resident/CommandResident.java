package town.bunger.towns.plugin.command.resident;

import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.Command;
import org.incendo.cloud.context.CommandContext;
import town.bunger.towns.api.resident.Resident;
import town.bunger.towns.plugin.command.ResidentCommandBean;
import town.bunger.towns.plugin.util.InfoScreen;

import java.util.List;

import static town.bunger.towns.plugin.i18n.Messages.ERROR_RESIDENT_NOT_LOADED;

public final class CommandResident<C extends CommandSender> extends ResidentCommandBean<C> {

    @Override
    protected Command.Builder<C> configure(Command.Builder<C> builder) {
        return builder;
    }

    @Override
    public void execute(CommandContext<C> context) {
        final Resident resident = context.inject(Resident.class).orElse(null);
        if (resident == null) {
            context.sender().sendMessage(ERROR_RESIDENT_NOT_LOADED);
            return;
        }

        final List<Component> components = InfoScreen.printResident(resident);
        for (final Component component : components) {
            context.sender().sendMessage(component);
        }
    }
}
