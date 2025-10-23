// Credit to Tsay Yong for code inspiration.
package garfield.core;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class Dates {
    private static final DateTimeFormatter[] DT_INPUTS = new DateTimeFormatter[] {
            DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    };
    private static final DateTimeFormatter[] D_INPUTS = new DateTimeFormatter[] {
            DateTimeFormatter.ISO_LOCAL_DATE
    };

    private static final DateTimeFormatter D_OUT = DateTimeFormatter.ofPattern("MMM d yyyy");
    private static final DateTimeFormatter DT_OUT = DateTimeFormatter.ofPattern("MMM d yyyy, h:mm a");

    public static LocalDateTime tryParseDateTime(String s) {
        for (DateTimeFormatter f : DT_INPUTS) {
            try {
                return LocalDateTime.parse(s, f);
            } catch (Exception ignore) {
            }
        }
        return null;
    }

    public static LocalDate tryParseDate(String s) {
        for (DateTimeFormatter f : D_INPUTS) {
            try {
                return LocalDate.parse(s, f);
            } catch (Exception ignore) {
            }
        }
        return null;
    }

    public static String format(LocalDate d) {
        return d.format(D_OUT);
    }

    public static String format(LocalDateTime dt) {
        return dt.format(DT_OUT);
    }

    private Dates() {
    }
}