package rahulstech.android.database.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.annotation.NonNull;

@SuppressWarnings(value = {"unused","SimpleDateFormat"})
@Deprecated
public class DBDateTimeUtil {

    public static Date parse(@NonNull String value, @NonNull String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        try {
            return format.parse(value);
        }
        catch (ParseException ex) {
            throw new RuntimeException("unable to parse \""+value+"\" with pattern \""+pattern+"\"");
        }
    }

    public static String format(@NonNull Date date, @NonNull String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }
}
