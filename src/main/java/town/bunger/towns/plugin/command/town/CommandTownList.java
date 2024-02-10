package town.bunger.towns.plugin.command.town;

import org.incendo.cloud.Command;
import org.incendo.cloud.context.CommandContext;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jspecify.annotations.Nullable;
import town.bunger.towns.api.BungerTowns;
import town.bunger.towns.plugin.command.TownCommandBean;
import town.bunger.towns.plugin.util.TextBanner;

import java.util.concurrent.CompletableFuture;

public final class CommandTownList<C extends Audience> extends TownCommandBean<C> {

    public static final Component BANNER = TextBanner.create("Towns");

    @Override
    protected Command.Builder<C> configure(Command.Builder<C> builder) {
        return builder
            .literal("list", "l");
    }

    @Override
    public CompletableFuture<@Nullable Void> executeFuture(
        CommandContext<C> context
    ) {
        final BungerTowns api = context.inject(BungerTowns.class).orElseThrow();

        context.sender().sendMessage(BANNER);
        // TODO: SQL pagination
        return api.towns().all().thenAccept(towns -> {
            for (var town : towns) {
                context.sender().sendMessage(Component.text("  " + town.name(), NamedTextColor.GREEN));
            }
        });
    }
}