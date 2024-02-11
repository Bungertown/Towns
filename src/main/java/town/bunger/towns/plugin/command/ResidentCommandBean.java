package town.bunger.towns.plugin.command;

import org.incendo.cloud.bean.CommandBean;
import org.incendo.cloud.bean.CommandProperties;

public abstract class ResidentCommandBean<C> extends CommandBean<C> {

    private static final CommandProperties PROPERTIES = CommandProperties.of("resident", "res");

    @Override
    protected CommandProperties properties() {
        return PROPERTIES;
    }
}
