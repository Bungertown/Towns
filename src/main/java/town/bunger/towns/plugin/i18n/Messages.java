package town.bunger.towns.plugin.i18n;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jspecify.annotations.Nullable;
import town.bunger.towns.plugin.util.DateFormats;

import java.time.Instant;
import java.util.UUID;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.event.ClickEvent.runCommand;
import static net.kyori.adventure.text.format.TextColor.color;

public final class Messages {

    public static final TranslatableComponent ERROR_RESIDENT_NOT_LOADED =
        translatable("bungertowns.error.resident.not_loaded", NamedTextColor.RED);

    public static TranslatableComponent ERROR_TOWN_CREATE_FAILED(String townName) {
        return translatable("bungertowns.error.town.create.failed", NamedTextColor.RED)
            .arguments(text(townName, NamedTextColor.DARK_RED));
    }

    public static TranslatableComponent ERROR_TOWN_DELETE_FAILED(String townName) {
        return translatable("bungertowns.error.town.delete.failed", NamedTextColor.RED)
            .arguments(text(townName, NamedTextColor.DARK_RED));
    }

    public static TranslatableComponent ERROR_TOWN_JOIN_FAILED(String townName) {
        return translatable("bungertowns.error.town.join.failed", NamedTextColor.RED)
            .arguments(text(townName, NamedTextColor.DARK_RED));
    }

    public static TranslatableComponent ERROR_TOWN_KICK_FAILED(String townName, String targetName) {
        return translatable("bungertowns.error.town.kick.failed", NamedTextColor.RED)
            .arguments(
                text(townName, NamedTextColor.DARK_RED),
                text(targetName, NamedTextColor.DARK_RED)
            );
    }

    public static TranslatableComponent ERROR_TOWN_LEAVE_FAILED(String townName) {
        return translatable("bungertowns.error.town.leave.failed", NamedTextColor.RED)
            .arguments(text(townName, NamedTextColor.DARK_RED));
    }

    public static final TranslatableComponent ERROR_TOWN_MEMBERSHIP_REQUIRED =
        translatable("bungertowns.error.town.membership_required", NamedTextColor.RED);

    public static final TranslatableComponent ERROR_TOWN_MUST_LEAVE_FIRST =
        translatable("bungertowns.error.town.must_leave_first", NamedTextColor.RED);

    public static final TranslatableComponent ERROR_TOWN_MUST_SPECIFY_ARGUMENT =
        translatable("bungertowns.error.town.must_specify_argument", NamedTextColor.RED);

    public static final TranslatableComponent ERROR_TOWN_NAME_TAKEN =
        translatable("bungertowns.error.town.name_taken", NamedTextColor.RED);

    public static final TranslatableComponent ERROR_TOWN_NOT_LOADED =
        translatable("bungertowns.error.town.not_loaded", NamedTextColor.RED);

    public static final TranslatableComponent ERROR_TOWN_NOT_OPEN =
        translatable("bungertowns.error.town.not_open", NamedTextColor.RED);

    public static final TranslatableComponent ERROR_TOWN_OWNERSHIP_CANNOT_LEAVE =
        translatable("bungertowns.error.town.ownership_cannot_leave", NamedTextColor.RED);

    public static final TranslatableComponent ERROR_TOWN_OWNERSHIP_REQUIRED =
        translatable("bungertowns.error.town.ownership_required", NamedTextColor.RED);

    public static TranslatableComponent ERROR_TOWN_SET_NAME_FAILED(String townName) {
        return translatable("bungertowns.error.town.set.name.failed", NamedTextColor.RED)
            .arguments(text(townName, NamedTextColor.DARK_RED));
    }

    public static TranslatableComponent ERROR_TOWN_SET_OPEN_FAILED(boolean open) {
        return translatable("bungertowns.error.town.set.open.failed", NamedTextColor.RED)
            .arguments(text(open ? "open" : "close", NamedTextColor.DARK_RED));
    }

    public static TranslatableComponent ERROR_TOWN_SET_SLOGAN_FAILED(@Nullable String slogan) {
        return translatable("bungertowns.error.town.set.slogan.failed", NamedTextColor.RED)
            .arguments(text(slogan != null ? slogan : "nothing", NamedTextColor.DARK_RED));
    }

    private static TranslatableComponent SCREEN_RESIDENT_CREATED_HOVER(Instant created) {
        return translatable("bungertowns.screen.resident.created.hover", NamedTextColor.GRAY)
            .arguments(text(DateFormats.DATE_TIME.format(created), NamedTextColor.GREEN));
    }

    public static TranslatableComponent SCREEN_RESIDENT_CREATED_TEXT(Instant created) {
        return translatable("bungertowns.screen.resident.created.text", NamedTextColor.DARK_GREEN)
            .arguments(text(DateFormats.DATE.format(created), NamedTextColor.GREEN))
            .hoverEvent(SCREEN_RESIDENT_CREATED_HOVER(created));
    }

    private static final TranslatableComponent SCREEN_RESIDENT_LAST_JOINED_EMPTY =
        translatable("bungertowns.screen.resident.last_joined.empty", NamedTextColor.GREEN);

    private static TranslatableComponent SCREEN_RESIDENT_LAST_JOINED_HOVER(@Nullable Instant lastJoined) {
        final Component arg1 = lastJoined != null
            ? text(DateFormats.DATE_TIME.format(lastJoined), NamedTextColor.GREEN)
            : SCREEN_RESIDENT_LAST_JOINED_EMPTY;
        return translatable("bungertowns.screen.resident.last_joined.hover", NamedTextColor.GRAY)
            .arguments(arg1);
    }

    public static TranslatableComponent SCREEN_RESIDENT_LAST_JOINED_TEXT(@Nullable Instant lastJoined) {
        final Component arg1 = lastJoined != null
            ? text(DateFormats.DATE.format(lastJoined), NamedTextColor.GREEN)
            : SCREEN_RESIDENT_LAST_JOINED_EMPTY;
        return translatable("bungertowns.screen.resident.last_joined.text", NamedTextColor.DARK_GREEN)
            .arguments(arg1)
            .hoverEvent(SCREEN_RESIDENT_LAST_JOINED_HOVER(lastJoined));
    }

    private static final TranslatableComponent SCREEN_RESIDENT_TOWN_EMPTY =
        translatable("bungertowns.screen.resident.town.empty", NamedTextColor.GREEN);

    private static final TranslatableComponent SCREEN_RESIDENT_TOWN_HOVER =
        translatable("bungertowns.screen.resident.town.hover", NamedTextColor.GRAY);

    public static TranslatableComponent SCREEN_RESIDENT_TOWN_TEXT(@Nullable String townName) {
        final Component arg1 = townName != null
            ? text(townName, NamedTextColor.GREEN)
            : SCREEN_RESIDENT_TOWN_EMPTY;
        return translatable("bungertowns.screen.resident.town.text", NamedTextColor.DARK_GREEN)
            .arguments(arg1)
            .hoverEvent(SCREEN_RESIDENT_TOWN_HOVER)
            .clickEvent(townName != null ? runCommand("/town info " + townName) : null);
    }

    private static final TranslatableComponent SCREEN_RESIDENT_UUID_HOVER =
        translatable("bungertowns.screen.resident.uuid.hover", NamedTextColor.GRAY);

    public static TranslatableComponent SCREEN_RESIDENT_UUID_TEXT(UUID uuid) {
        return translatable("bungertowns.screen.resident.uuid.text", NamedTextColor.DARK_GREEN)
            .arguments(text(uuid.toString(), NamedTextColor.GREEN))
            .hoverEvent(SCREEN_RESIDENT_UUID_HOVER);
    }

    private static final TranslatableComponent SCREEN_TOWN_BANK_HOVER =
        translatable("bungertowns.screen.town.bank.hover", NamedTextColor.GRAY);

    public static TranslatableComponent SCREEN_TOWN_BANK_TEXT(String balance) {
        return translatable("bungertowns.screen.town.bank.text", NamedTextColor.DARK_GREEN)
            .arguments(text(balance, NamedTextColor.GREEN))
            .hoverEvent(SCREEN_TOWN_BANK_HOVER);
    }

    private static final TranslatableComponent SCREEN_TOWN_BTN_RESIDENTS_HOVER =
        translatable("bungertowns.screen.town.btn.residents.hover", NamedTextColor.GRAY);

    public static TranslatableComponent SCREEN_TOWN_BTN_RESIDENTS_TEXT(String townName, int count) {
        return translatable("bungertowns.screen.town.btn.residents.text", NamedTextColor.GRAY)
            .arguments(text(count, NamedTextColor.GREEN))
            .hoverEvent(SCREEN_TOWN_BTN_RESIDENTS_HOVER)
            .clickEvent(runCommand("/town residents " + townName));
    }

    private static TranslatableComponent SCREEN_TOWN_CREATED_HOVER(Instant created) {
        return translatable("bungertowns.screen.town.created.hover", NamedTextColor.GRAY)
            .arguments(text(DateFormats.DATE_TIME.format(created), NamedTextColor.GREEN));
    }

    public static TranslatableComponent SCREEN_TOWN_CREATED_TEXT(Instant created) {
        return translatable("bungertowns.screen.town.created.text", NamedTextColor.DARK_GREEN)
            .arguments(text(DateFormats.DATE.format(created), NamedTextColor.GREEN))
            .hoverEvent(SCREEN_TOWN_CREATED_HOVER(created));
    }

    private static final TranslatableComponent SCREEN_TOWN_FOUNDER_HOVER =
        translatable("bungertowns.screen.town.founder.hover", NamedTextColor.GRAY);

    public static TranslatableComponent SCREEN_TOWN_FOUNDER_TEXT(String founderName) {
        return translatable("bungertowns.screen.town.founder.text", NamedTextColor.DARK_GREEN)
            .arguments(text(founderName, NamedTextColor.GREEN))
            .hoverEvent(SCREEN_TOWN_FOUNDER_HOVER);
    }

    private static final TranslatableComponent SCREEN_TOWN_MAYOR_HOVER =
        translatable("bungertowns.screen.town.mayor.hover", NamedTextColor.GRAY);

    public static TranslatableComponent SCREEN_TOWN_MAYOR_TEXT(String mayorName) {
        return translatable("bungertowns.screen.town.mayor.text", NamedTextColor.DARK_GREEN)
            .arguments(text(mayorName, NamedTextColor.GREEN))
            .hoverEvent(SCREEN_TOWN_MAYOR_HOVER);
    }

    private static final TranslatableComponent SCREEN_TOWN_SIZE_HOVER =
        translatable("bungertowns.screen.town.size.hover", NamedTextColor.GRAY);

    public static TranslatableComponent SCREEN_TOWN_SIZE_TEXT(int currentSize, int maxSize) {
        return translatable("bungertowns.screen.town.size.text", NamedTextColor.DARK_GREEN)
            .arguments(
                text(currentSize, NamedTextColor.GREEN),
                text(maxSize, NamedTextColor.GREEN)
            )
            .hoverEvent(SCREEN_TOWN_SIZE_HOVER);
    }

    private static final TranslatableComponent SCREEN_TOWN_SIZE_BONUS_HOVER =
        translatable("bungertowns.screen.town.size.bonus.hover", NamedTextColor.GRAY);

    public static TranslatableComponent SCREEN_TOWN_SIZE_BONUS_TEXT(int bonusSize) {
        return translatable("bungertowns.screen.town.size.bonus.text", color(73, 189, 227))
            .arguments(text(bonusSize, color(73, 189, 227)))
            .hoverEvent(SCREEN_TOWN_SIZE_BONUS_HOVER);
    }

    private static final TranslatableComponent SCREEN_TOWN_SLOGAN_HOVER =
        translatable("bungertowns.screen.town.slogan.hover", NamedTextColor.GRAY);

    public static final TranslatableComponent SCREEN_TOWN_SLOGAN_DEFAULT =
        translatable("bungertowns.screen.town.slogan.default", color(73, 189, 227))
            .hoverEvent(SCREEN_TOWN_SLOGAN_HOVER);

    public static TranslatableComponent SCREEN_TOWN_SLOGAN_TEXT(@Nullable String slogan) {
        if (slogan == null) {
            return SCREEN_TOWN_SLOGAN_DEFAULT;
        }
        return translatable("bungertowns.screen.town.slogan.text", color(73, 189, 227))
            .arguments(text(slogan, NamedTextColor.GREEN))
            .hoverEvent(SCREEN_TOWN_SLOGAN_HOVER);
    }

    private static final TranslatableComponent SCREEN_TOWN_UPKEEP_HOVER =
        translatable("bungertowns.screen.town.upkeep.hover", NamedTextColor.GRAY);

    public static TranslatableComponent SCREEN_TOWN_UPKEEP_TEXT(String upkeep) {
        return translatable("bungertowns.screen.town.upkeep.text", NamedTextColor.DARK_GREEN)
            .arguments(text(upkeep, NamedTextColor.RED))
            .hoverEvent(SCREEN_TOWN_UPKEEP_HOVER);
    }

    public static TranslatableComponent NOTIFY_TOWN_CREATED_SELF(String townName) {
        return translatable("bungertowns.notify.town.created.self", NamedTextColor.GREEN)
            .arguments(text(townName, NamedTextColor.DARK_GREEN));
    }

    public static TranslatableComponent NOTIFY_TOWN_CREATED_OTHER(String townName, String ownerName) {
        return translatable("bungertowns.notify.town.created.other", NamedTextColor.GREEN)
            .arguments(text(townName, NamedTextColor.DARK_GREEN), text(ownerName, NamedTextColor.DARK_GREEN));
    }

    public static TranslatableComponent NOTIFY_TOWN_DELETED_SELF(String townName) {
        return translatable("bungertowns.notify.town.deleted.self", NamedTextColor.GREEN)
            .arguments(text(townName, NamedTextColor.DARK_GREEN));
    }

    public static TranslatableComponent NOTIFY_TOWN_DELETED_OTHER(String townName, String ownerName) {
        return translatable("bungertowns.notify.town.deleted.other", NamedTextColor.GREEN)
            .arguments(
                text(townName, NamedTextColor.DARK_GREEN),
                text(ownerName, NamedTextColor.DARK_GREEN)
            );
    }

    public static TranslatableComponent NOTIFY_TOWN_JOINED_SELF(String townName) {
        return translatable("bungertowns.notify.town.joined.self", NamedTextColor.GREEN)
            .arguments(text(townName, NamedTextColor.DARK_GREEN));
    }

    public static TranslatableComponent NOTIFY_TOWN_JOINED_OTHER(String townName, String residentName) {
        return translatable("bungertowns.notify.town.joined.other", NamedTextColor.GREEN)
            .arguments(
                text(townName, NamedTextColor.DARK_GREEN),
                text(residentName, NamedTextColor.DARK_GREEN)
            );
    }

    public static TranslatableComponent NOTIFY_TOWN_KICKED_OTHER(String townName, String targetName, String sourceName) {
        return translatable("bungertowns.notify.town.kicked.other", NamedTextColor.GREEN)
            .arguments(
                text(townName, NamedTextColor.DARK_GREEN),
                text(targetName, NamedTextColor.DARK_GREEN),
                text(sourceName, NamedTextColor.DARK_GREEN)
            );
    }

    public static TranslatableComponent NOTIFY_TOWN_KICKED_SOURCE(String townName, String targetName) {
        return translatable("bungertowns.notify.town.kicked.source", NamedTextColor.GREEN)
            .arguments(
                text(townName, NamedTextColor.DARK_GREEN),
                text(targetName, NamedTextColor.DARK_GREEN)
            );
    }

    public static TranslatableComponent NOTIFY_TOWN_KICKED_TARGET(String townName) {
        return translatable("bungertowns.notify.town.kicked.target", NamedTextColor.GREEN)
            .arguments(text(townName, NamedTextColor.DARK_GREEN));
    }

    public static TranslatableComponent NOTIFY_TOWN_LEFT_SELF(String townName) {
        return translatable("bungertowns.notify.town.left.self", NamedTextColor.GREEN)
            .arguments(text(townName, NamedTextColor.DARK_GREEN));
    }

    public static TranslatableComponent NOTIFY_TOWN_LEFT_OTHER(String townName, String residentName) {
        return translatable("bungertowns.notify.town.left.other", NamedTextColor.GREEN)
            .arguments(
                text(townName, NamedTextColor.DARK_GREEN),
                text(residentName, NamedTextColor.DARK_GREEN)
            );
    }

    private static final TranslatableComponent NOTIFY_TOWN_OPENED_TRUE =
        translatable("bungertowns.notify.town.opened.true", NamedTextColor.DARK_GREEN);

    private static final TranslatableComponent NOTIFY_TOWN_OPENED_FALSE =
        translatable("bungertowns.notify.town.opened.false", NamedTextColor.DARK_GREEN);

    public static TranslatableComponent NOTIFY_TOWN_OPENED_SELF(boolean open, String townName) {
        return translatable("bungertowns.notify.town.opened.self", NamedTextColor.GREEN)
            .arguments(
                open ? NOTIFY_TOWN_OPENED_TRUE : NOTIFY_TOWN_OPENED_FALSE,
                text(townName, NamedTextColor.DARK_GREEN)
            );
    }

    public static TranslatableComponent NOTIFY_TOWN_OPENED_OTHER(boolean open, String townName, String residentName) {
        return translatable("bungertowns.notify.town.opened.other", NamedTextColor.GREEN)
            .arguments(
                open ? NOTIFY_TOWN_OPENED_TRUE : NOTIFY_TOWN_OPENED_FALSE,
                text(townName, NamedTextColor.DARK_GREEN),
                text(residentName, NamedTextColor.DARK_GREEN)
            );
    }

    public static TranslatableComponent NOTIFY_TOWN_RENAMED_SELF(String oldName, String newName) {
        return translatable("bungertowns.notify.town.renamed.self", NamedTextColor.GREEN)
            .arguments(
                text(oldName, NamedTextColor.DARK_GREEN),
                text(newName, NamedTextColor.DARK_GREEN)
            );
    }

    public static TranslatableComponent NOTIFY_TOWN_RENAMED_OTHER(String oldName, String newName, String residentName) {
        return translatable("bungertowns.notify.town.renamed.other", NamedTextColor.GREEN)
            .arguments(
                text(oldName, NamedTextColor.DARK_GREEN),
                text(newName, NamedTextColor.DARK_GREEN),
                text(residentName, NamedTextColor.DARK_GREEN)
            );
    }

    public static TranslatableComponent NOTIFY_TOWN_SLOGAN_CHANGED_SELF(String townName, String slogan) {
        return translatable("bungertowns.notify.town.slogan.changed.self", NamedTextColor.GREEN)
            .arguments(
                text(townName, NamedTextColor.DARK_GREEN),
                text(slogan, NamedTextColor.DARK_GREEN)
            );
    }

    public static TranslatableComponent NOTIFY_TOWN_SLOGAN_CLEARED_SELF(String townName) {
        return translatable("bungertowns.notify.town.slogan.cleared.self", NamedTextColor.GREEN)
            .arguments(text(townName, NamedTextColor.DARK_GREEN));
    }

    private Messages() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }
}
