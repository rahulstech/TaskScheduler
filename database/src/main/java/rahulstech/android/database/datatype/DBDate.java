package rahulstech.android.database.datatype;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;

import static rahulstech.android.database.util.DBDateTimeUtil.format;
import static rahulstech.android.database.util.DBDateTimeUtil.parse;

@SuppressWarnings(value = {"unused"})
public class DBDate {

    private static final String TAG = "DBDate";

    private static final String DATE_PATTERN = "yyyy-MM-dd";

    final Calendar mCalender;

    DBDate(@NonNull Calendar c) {
        mCalender = c;
        // set the time at start of day
        c.set(Calendar.HOUR_OF_DAY,0);
        c.set(Calendar.MINUTE,0);
        c.set(Calendar.SECOND,0);
        c.set(Calendar.MILLISECOND,0);
    }

    public int getYear() {
        return mCalender.get(Calendar.YEAR);
    }

    public int getMonth() {
        return mCalender.get(Calendar.MONTH);
    }

    public int getDate() {
        return mCalender.get(Calendar.DAY_OF_MONTH);
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

    @NonNull
    @Override
    public String toString() {
        return format(mCalender.getTime(),DATE_PATTERN);
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
