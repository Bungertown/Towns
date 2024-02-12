package town.bunger.towns.plugin.util;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public final class DateFormats {

    public static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("MMM dd yyyy").withZone(ZoneId.systemDefault());
}
