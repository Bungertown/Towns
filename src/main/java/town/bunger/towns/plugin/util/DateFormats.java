package town.bunger.towns.plugin.util;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public final class DateFormats {

    public static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("MMM dd yyyy").withZone(ZoneId.systemDefault());
    public static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("MMM dd yyyy KK:mm:ss a zz").withZone(ZoneId.systemDefault());
}
