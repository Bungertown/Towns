package town.bunger.towns.plugin.command;

import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.injection.ParameterInjector;
import org.incendo.cloud.util.annotation.AnnotationAccessor;
import org.slf4j.Logger;

public final class LoggerInjector<C> implements ParameterInjector<C, Logger> {

    private final Logger logger;

    public LoggerInjector(Logger logger) {
        this.logger = logger;
    }

    @Override
    public Logger create(CommandContext<C> context, AnnotationAccessor annotationAccessor) {
        return this.logger;
    }
}
