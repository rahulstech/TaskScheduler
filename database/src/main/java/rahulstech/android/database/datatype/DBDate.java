package rahulstech.android.database.datatype;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import rahulstech.android.database.util.DBDateTimeUtil;

import static rahulstech.android.database.util.DBDateTimeUtil.parse;

@SuppressWarnings(value = {"unused"})
@Deprecated
public class DBDate {

    private static final String TAG = "DBDate";

    private static final String DATE_PATTERN = "yyyy-MM-dd";

    final Calendar mCalendar;

    DBDate(@NonNull Calendar c) {
        mCalendar = c;
        // set the time at start of day
        c.set(Calendar.HOUR_OF_DAY,0);
        c.set(Calendar.MINUTE,0);
        c.set(Calendar.SECOND,0);
        c.set(Calendar.MILLISECOND,0);
    }

    public int getYear() {
        return mCalendar.get(Calendar.YEAR);
    }

    public int getMonth() {
        return mCalendar.get(Calendar.MONTH);
    }

    public int getDate() {
        return mCalendar.get(Calendar.DAY_OF_MONTH);
    }

    @NonNull
    public static DBDate of(int year, int month, int date) {
        Calendar c = Calendar.getInstance(Locale.ENGLISH);
        c.set(year, month,date);
        return new DBDate(c);
    }

    @NonNull
    public static DBDate today() {
        return new DBDate(Calendar.getInstance(Locale.ENGLISH));
    }

    @NonNull
    public static DBDate valueOf(@NonNull String dateString) {
        Date local = parse(dateString, DATE_PATTERN);
        Calendar c = Calendar.getInstance(Locale.ENGLISH);
        c.setTime(local);
        return new DBDate(c);
    }

    public String format(@NonNull String pattern) {
        return DBDateTimeUtil.format(mCalendar.getTime(),pattern);
    }

    @NonNull
    @Override
    public String toString() {
        return format(DATE_PATTERN);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DBDate)) return false;
        DBDate that = (DBDate) o;
        return getYear() == that.getYear()
                && getMonth() == that.getMonth()
                && getDate() == that.getDate();
    }
}
