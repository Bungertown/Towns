package town.bunger.towns.plugin.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import town.bunger.towns.api.resident.Resident;
import town.bunger.towns.api.town.Town;

import java.time.LocalDateTime;
import java.util.List;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.TextColor.color;

public final class InfoScreen {

    public static List<Component> printResident(final Resident resident) {
        final Component banner = TextBanner.create(resident.name());
        final Component uuid = text()
            .append(
                text("  UUID: ", NamedTextColor.DARK_GREEN),
                text(resident.id().toString(), NamedTextColor.GREEN)
            )
            .hoverEvent(text("This is the player's 'UUID', or Universally Unique Identifier. " +
                "It's used to identify the player across name changes.", NamedTextColor.GRAY))
            .build();
        final LocalDateTime lastJoined = resident.lastJoined();
        final Component dates = text().append(
            text()
                .append(
                    text("  Registered: ", NamedTextColor.DARK_GREEN),
                    text(resident.created().format(DateFormats.DATE), NamedTextColor.GREEN)
                )
                .hoverEvent(text("The player was first registered in the town system on this date.", NamedTextColor.GRAY)),
            text()
                .append(
                    text(" Last Joined: ", NamedTextColor.DARK_GREEN),
                    text(lastJoined != null ? lastJoined.format(DateFormats.DATE) : "*Never*", NamedTextColor.GREEN)
                )
                .hoverEvent(text("The player last joined the server on this date.", NamedTextColor.GRAY))
        ).build();
        final String townName = resident.townName();
        final Component town = text()
            .append(
                text("  Town: ", NamedTextColor.DARK_GREEN),
                text(townName != null ? townName : "*none*", NamedTextColor.GREEN)
            )
            .hoverEvent(text("The town the player is a member of." + (townName != null ? "\n\nClick to view their town." : ""), NamedTextColor.GRAY))
            .clickEvent(townName != null ? ClickEvent.runCommand("/town info " + townName) : null)
            .build();

        return List.of(banner, uuid, dates, town);
    }

    public static List<Component> printTown(final Town town) {
        final Component banner = TextBanner.create(town.name());
        final String slogan = town.slogan();
        final Component sloganEst = text()
            .append(
                text("  \"" + (slogan != null ? slogan : "YOUR SLOGAN HERE") + "\"", color(73, 189, 227)) // TODO
                    .hoverEvent(text("The town's slogan, or message of the day.", NamedTextColor.GRAY)),
                text()
                    .append(
                        text(" est. ", NamedTextColor.DARK_GREEN),
                        text(town.created().format(DateFormats.DATE), NamedTextColor.GREEN)
                    )
                    .hoverEvent(text("The town was created on this date.", NamedTextColor.GRAY))
            ).build();
        final Component mayorFounder = text()
            .append(
                text()
                    .append(
                        text("  Mayor: ", NamedTextColor.DARK_GREEN),
                        text(town.ownerName(), NamedTextColor.GREEN)
                    )
                    .hoverEvent(text("The player who currently runs the town.", NamedTextColor.GRAY)),
                text()
                    .append(
                        text(" Founder: ", NamedTextColor.DARK_GREEN),
                        text(town.ownerName(), NamedTextColor.GREEN) // TODO
                    )
                    .hoverEvent(text("The player who created the town.", NamedTextColor.GRAY))
            ).build();
        final Component townSize = text()
            .append(
                text()
                    .append(
                        text("  Town Size: ", NamedTextColor.DARK_GREEN),
                        text("69/420", NamedTextColor.GREEN) // TODO
                    )
                    .hoverEvent(text("The number of chunks the town has claimed out of the total number of chunks they can claim.", NamedTextColor.GRAY)),
                text()
                    .append(
                        text(" +41", color(73, 189, 227))
                    )
                    .hoverEvent(text("The number of chunks the town is awarded as a bonus through various factors.", NamedTextColor.GRAY))
            ).build();
        final Component economy = text()
            .append(
                text()
                    .append(
                        text("  Bank: ", NamedTextColor.DARK_GREEN),
                        text("$69,420", NamedTextColor.GREEN) // TODO
                    )
                    .hoverEvent(text("The amount of money the town has in its bank.", NamedTextColor.GRAY)),
                text()
                    .append(
                        text(" Upkeep: ", NamedTextColor.DARK_GREEN),
                        text("$420", NamedTextColor.RED) // TODO
                    )
                    .hoverEvent(text("The amount of money the town must pay to keep its chunks claimed.", NamedTextColor.GRAY))
            ).build();
        final Component buttons = text()
            .color(NamedTextColor.GRAY)
            .append(
                text("  ["),
                text(town.residentIds().size() + " Residents", NamedTextColor.GREEN) // TODO
                    .hoverEvent(text("Click to view the residents of the town.", NamedTextColor.GRAY)),
                text("]")
            ).build();

        return List.of(banner, sloganEst, mayorFounder, townSize, economy, buttons);
    }
}
