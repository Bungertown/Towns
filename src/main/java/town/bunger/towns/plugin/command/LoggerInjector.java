package town.bunger.towns.plugin.command;

import cloud.commandframework.annotations.AnnotationAccessor;
import cloud.commandframework.annotations.injection.ParameterInjector;
import cloud.commandframework.context.CommandContext;
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
