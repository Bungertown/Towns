package town.bunger.towns.plugin.command;

import cloud.commandframework.CommandBean;
import cloud.commandframework.CommandProperties;
import org.checkerframework.checker.nullness.qual.NonNull;

public abstract class TownCommandBean<C> extends CommandBean<C> {

    public static final CommandProperties PROPERTIES = CommandProperties.of("town", "t");

    @Override
    protected @NonNull CommandProperties properties() {
        return PROPERTIES;
    }
}
