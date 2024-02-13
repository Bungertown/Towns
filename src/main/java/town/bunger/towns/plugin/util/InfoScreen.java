package town.bunger.towns.plugin.util;

import net.kyori.adventure.text.Component;
import town.bunger.towns.api.resident.Resident;
import town.bunger.towns.api.town.Town;

import java.util.List;

import static net.kyori.adventure.text.Component.space;
import static net.kyori.adventure.text.Component.text;
import static town.bunger.towns.plugin.i18n.Messages.*;

/**
 * A collection of {@link Component}s for displaying information on the screen.
 */
public final class InfoScreen {

    /**
     * A {@link Component} representing a space.
     */
    public static final Component SPACE = space();

    /**
     * A {@link Component} representing two spaces.
     */
    public static final Component DOUBLE_SPACE = text("  ");

    /**
     * Print information about a resident.
     *
     * @param resident The resident
     * @return The information
     */
    public static List<Component> printResident(final Resident resident) {
        final Component banner = TextBanner.create(resident.name());
        final Component uuid = text()
            .append(
                DOUBLE_SPACE,
                SCREEN_RESIDENT_UUID_TEXT(resident.id())
            ).build();
        final Component dates = text()
            .append(
                DOUBLE_SPACE,
                SCREEN_RESIDENT_CREATED_TEXT(resident.created()),
                SPACE,
                SCREEN_RESIDENT_LAST_JOINED_TEXT(resident.lastJoined())
            ).build();
        final Component town = text()
            .append(
                DOUBLE_SPACE,
                SCREEN_RESIDENT_TOWN_TEXT(resident.townName())
            ).build();
        return List.of(banner, uuid, dates, town);
    }

    /**
     * Print information about a town.
     *
     * @param town The town
     * @return The information
     */
    public static List<Component> printTown(final Town town) {
        final Component banner = TextBanner.create(town.name());
        final Component sloganEst = text()
            .append(
                DOUBLE_SPACE,
                SCREEN_TOWN_SLOGAN_TEXT(town.slogan()),
                SPACE,
                SCREEN_TOWN_CREATED_TEXT(town.created())
            ).build();
        final Component mayorFounder = text()
            .append(
                DOUBLE_SPACE,
                SCREEN_TOWN_MAYOR_TEXT(town.ownerName()),
                SPACE,
                SCREEN_TOWN_FOUNDER_TEXT(town.ownerName()) // TODO: founder not owner
            ).build();
        final Component townSize = text()
            .append(
                DOUBLE_SPACE,
                SCREEN_TOWN_SIZE_TEXT(69, 420), // TODO
                SPACE,
                SCREEN_TOWN_SIZE_BONUS_TEXT(41) // TODO
            ).build();
        final Component economy = text()
            .append(
                DOUBLE_SPACE,
                SCREEN_TOWN_BANK_TEXT("$69,420"), // TODO
                SPACE,
                SCREEN_TOWN_UPKEEP_TEXT("$420") // TODO
            ).build();
        final Component buttons = text()
            .append(
                DOUBLE_SPACE,
                SCREEN_TOWN_BTN_RESIDENTS_TEXT(town.name(), town.residentIds().size())
            ).build();
        return List.of(banner, sloganEst, mayorFounder, townSize, economy, buttons);
    }
}
