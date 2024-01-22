package town.bunger.towns.plugin.command;

import cloud.commandframework.paper.PaperCommandManager;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import town.bunger.towns.impl.BungerTownsImpl;
import town.bunger.towns.plugin.util.DateFormats;
import town.bunger.towns.plugin.util.TextBanner;

import java.util.Objects;

import static net.kyori.adventure.text.Component.text;

public final class ResidentCommands {

    public static void register(PaperCommandManager<CommandSender> manager, BungerTownsImpl api) {
        final var base = manager.commandBuilder("resident");

        manager.command(base
            .senderType(Player.class)
            .handler(context -> api.residents().load(context.sender().getUniqueId())
                .whenComplete((resident, ex) -> {
                    if (ex != null) {
                        context.sender().sendMessage(text("Failed to load resident data", NamedTextColor.RED));
                        api.logger().error("Failed to load resident data", ex);
                        return;
                    }
                    Objects.requireNonNull(resident, "Resident does not exist");

                    context.sender().sendMessage(TextBanner.create(resident.name()));
                    context.sender().sendMessage(text()
                        .append(
                            text("  UUID: ", NamedTextColor.DARK_GREEN),
                            text(resident.id().toString(), NamedTextColor.GREEN)
                        )
                        .hoverEvent(text("This is the player's 'UUID', or Universally Unique Identifier. " +
                            "It's used to identify the player across name changes.", NamedTextColor.GRAY)));
                    context.sender().sendMessage(text().append(
                        text()
                            .append(
                                text("  Registered: ", NamedTextColor.DARK_GREEN),
                                text(resident.created().format(DateFormats.DATE), NamedTextColor.GREEN)
                            )
                            .hoverEvent(text("The player was first registered in the town system on this date.", NamedTextColor.GRAY)),
                        text()
                            .append(
                                text(" Last Joined: ", NamedTextColor.DARK_GREEN),
                                text(resident.lastJoined() != null ? resident.lastJoined().format(DateFormats.DATE) : "*Never*", NamedTextColor.GREEN)
                            )
                            .hoverEvent(text("The player last joined the server on this date.", NamedTextColor.GRAY))
                    ));
                    final String townName = resident.townName();
                    context.sender().sendMessage(text()
                        .append(
                            text("  Town: ", NamedTextColor.DARK_GREEN),
                            text(townName != null ? townName : "*none*", NamedTextColor.GREEN)
                        )
                        .hoverEvent(text("The town the player is a member of." + (townName != null ? "\n\nClick to view their town." : ""), NamedTextColor.GRAY))
                        .clickEvent(townName != null ? ClickEvent.runCommand("/town info " + townName) : null)
                    );
                }))
        );
    }
}
