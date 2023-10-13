package rahulstech.android.database.datatype;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.room.TypeConverter;
import rahulstech.android.database.exception.DatabaseException;

@SuppressWarnings(value = {"unused"})
public class DatatypeConverters {

    @TypeConverter
    @Deprecated
    public static String dbDateToString(DBDate date) {
        if (null == date) return null;
        return date.toString();
    }

    @TypeConverter
    @Deprecated
    public static DBDate stringToDBDate(String dateString) {
        if (TextUtils.isEmpty(dateString)) return null;
        return DBDate.valueOf(dateString);
    }

    @TypeConverter
    @Deprecated
    public static String dbTimeString(DBTime time) {
        if (null == time) return null;
        return time.toString();
    }

    @TypeConverter
    @Deprecated
    public static DBTime stringToDBTime(String timeString) {
        if (TextUtils.isEmpty(timeString)) return null;
        return DBTime.valueOf(timeString);
    }

    @TypeConverter
    public static String localDateToString(LocalDate date) {
        if (null == date) return null;
        return date.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    @TypeConverter
    public static LocalDate stringToLocalDate(String value) {
        if (TextUtils.isEmpty(value)) return null;
        return LocalDate.parse(value,DateTimeFormatter.ISO_LOCAL_DATE);
    }

    @TypeConverter
    public static String localTimeToString(LocalTime time) {
        if (null == time) return null;
        return time.format(DateTimeFormatter.ISO_LOCAL_TIME);
    }

    @TypeConverter
    public static LocalTime stringToLocalTime(String value) {
        if (TextUtils.isEmpty(value)) return null;
        return LocalTime.parse(value,DateTimeFormatter.ISO_LOCAL_TIME);
    }

    @TypeConverter
    public static LocalDateTime stringToLocalDateTime(String value) {
        if (TextUtils.isEmpty(value)) return null;
        return LocalDateTime.parse(value,DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    @TypeConverter
    public static String localDateTimeToString(LocalDateTime dateTime) {
        if (null == dateTime) return null;
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
