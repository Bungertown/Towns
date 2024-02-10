package town.bunger.towns.plugin.command;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.bean.CommandBean;
import org.incendo.cloud.bean.CommandProperties;

public abstract class TownCommandBean<C> extends CommandBean<C> {

    public static final CommandProperties PROPERTIES = CommandProperties.of("town", "t");

    @Override
    protected @NonNull CommandProperties properties() {
        return PROPERTIES;
    }
}
