package rahulstech.android.database.datatype;

import android.text.TextUtils;

import androidx.room.TypeConverter;

@SuppressWarnings(value = {"unused"})
public class DatatypeConverters {

    @TypeConverter
    public static String dbDateToString(DBDate date) {
        if (null == date) return null;
        return date.toString();
    }

    @TypeConverter
    public static DBDate stringToDBDate(String dateString) {
        if (TextUtils.isEmpty(dateString)) return null;
        return DBDate.valueOf(dateString);
    }

    @TypeConverter
    public static String dbTimeString(DBTime time) {
        if (null == time) return null;
        return time.toString();
    }

    @TypeConverter
    public static DBTime stringToDBTime(String timeString) {
        if (TextUtils.isEmpty(timeString)) return null;
        return DBTime.valueOf(timeString);
    }
}
