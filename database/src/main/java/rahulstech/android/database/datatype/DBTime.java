package rahulstech.android.database.datatype;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import rahulstech.android.database.util.DBDateTimeUtil;

import static rahulstech.android.database.util.DBDateTimeUtil.parse;

@SuppressWarnings(value = {"unused"})
public class DBTime {

    private static final String TAG = "DBTime";

    private static final String TIME_PATTERN = "HH:mm:ss";

    final Calendar mCalendar;

    DBTime(@NonNull Calendar c) {
        mCalendar = c;
        mCalendar.set(Calendar.SECOND,0);
        mCalendar.set(Calendar.MILLISECOND,0);
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
    public static DBTime of(int hourOfDay, int minute) {
        Calendar c = Calendar.getInstance(Locale.ENGLISH);
        c.set(Calendar.HOUR_OF_DAY,hourOfDay);
        c.set(Calendar.MINUTE,minute);
        return new DBTime(c);
    }

    @NonNull
    public static DBTime now() {
        return new DBTime(Calendar.getInstance(Locale.ENGLISH));
    }

    @NonNull
    public static DBTime valueOf(@NonNull String timeString) {
        Date local = parse(timeString, TIME_PATTERN);
        Calendar c = Calendar.getInstance(Locale.ENGLISH);
        c.setTime(local);
        return new DBTime(c);
    }

    @NonNull
    public String format(@NonNull String pattern) {
        return DBDateTimeUtil.format(mCalendar.getTime(),pattern);
    }

    @NonNull
    @Override
    public String toString() {
        return DBDateTimeUtil.format(mCalendar.getTime(),TIME_PATTERN);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DBTime)) return false;
        DBTime that = (DBTime) o;
        return this.getHourOfDay() == that.getHourOfDay()
                && this.getMinute() == that.getMinute()
                && this.getSecond() == that.getSecond();
    }
}
