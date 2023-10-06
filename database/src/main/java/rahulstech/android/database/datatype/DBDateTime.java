package rahulstech.android.database.datatype;

import java.util.Calendar;
import java.util.Locale;

import androidx.annotation.NonNull;
import rahulstech.android.database.util.DBDateTimeUtil;

@SuppressWarnings("unused")
public class DBDateTime {

    private static final String TAG = "DBDate";

    private static final String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    final Calendar mCalendar;

    DBDateTime(@NonNull Calendar c) {
        mCalendar = c;
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

    public int getHourOfDay() {
        return mCalendar.get(Calendar.HOUR_OF_DAY);
    }

    public int getMinute() {
        return mCalendar.get(Calendar.MINUTE);
    }

    public int getSecond() {
        return mCalendar.get(Calendar.SECOND);
    }

    @NonNull
    public static DBDateTime from(@NonNull DBDate date, @NonNull DBTime time) {
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.set(date.getYear(),date.getMonth(),date.getDate(),time.getHourOfDay(),time.getMinute());
        return new DBDateTime(calendar);
    }

    @NonNull
    public String format(@NonNull String pattern) {
        return DBDateTimeUtil.format(mCalendar.getTime(),pattern);
    }

    @Override
    public String toString() {
        return DBDateTimeUtil.format(mCalendar.getTime(),DATETIME_PATTERN);
    }
}
