package town.bunger.towns.plugin.util;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;

import static net.kyori.adventure.key.Key.key;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.Style.style;

public final class TextBanner {

    private static final Key MONOCRAFT_FONT = key("monocraft", "default");


    private static final int MAX_MONOSPACED_WIDTH = 52;

    private static final Style BOLD = style(TextDecoration.BOLD);
    private static final Style STRIKETHROUGH = style(TextDecoration.STRIKETHROUGH);

    private static final Component LEFT_ANGLE = text(">", BOLD);
    private static final Component RIGHT_ANGLE = text("<", BOLD);

    public static Component create(String title) {
        // Remove excess whitespace
        final String trimmed = title.trim();

        int width = MAX_MONOSPACED_WIDTH;
        // Subtract the length of the title
        width -= trimmed.length();
        // Subtract the length of the surrounding spaces and the angle brackets
        width -= 2 + 4;
        // The number of spaces on each side of the title
        final int spaces = width / 2;
        final var spacesText = text(" ".repeat(spaces), STRIKETHROUGH);

        final var side = text()
            .color(NamedTextColor.GOLD)
            .append(LEFT_ANGLE)
            .append(spacesText)
            .append(RIGHT_ANGLE)
            .build();

        return text()
            .font(MONOCRAFT_FONT)
            .append(side)
            .append(text(" " + trimmed + " ", NamedTextColor.YELLOW))
            .append(side)
            .build();
    }
}
