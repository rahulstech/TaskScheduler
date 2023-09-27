package rahulstech.android.util.time;

import android.annotation.SuppressLint;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;

@SuppressWarnings("unused")
public class DateTime implements Serializable {

    final Calendar mCalendar;

    DateTime(@NonNull Calendar calendar) {
        mCalendar = calendar;
        mCalendar.set(Calendar.SECOND,0);
        mCalendar.set(Calendar.MILLISECOND,0);
    }

    @NonNull
    public static DateTime ofDate(int year, int month, int date) {
        Calendar c = Calendar.getInstance(Locale.ENGLISH);
        c.set(year,month,date);
        c.set(Calendar.HOUR_OF_DAY,0);
        c.set(Calendar.MINUTE,0);
        return new DateTime(c);
    }

    @NonNull
    public static DateTime ofTime(int hourOfDay, int minute) {
        Calendar c = Calendar.getInstance(Locale.ENGLISH);
        c.set(Calendar.HOUR_OF_DAY,hourOfDay);
        c.set(Calendar.MINUTE,minute);
        return new DateTime(c);
    }

    @NonNull
    public static DateTime ofDateTime(int year, int month, int date, int hourOfDay, int minute) {
        Calendar c = Calendar.getInstance(Locale.ENGLISH);
        c.set(year,month,date);
        c.set(Calendar.HOUR_OF_DAY,hourOfDay);
        c.set(Calendar.MINUTE,minute);
        return new DateTime(c);
    }

    @NonNull
    public static DateTime today() {
        Calendar c = Calendar.getInstance(Locale.ENGLISH);
        c.set(Calendar.HOUR_OF_DAY,0);
        c.set(Calendar.MINUTE,0);
        return new DateTime(c);
    }

    @NonNull
    public static DateTime now() {
        Calendar c = Calendar.getInstance(Locale.ENGLISH);
        return new DateTime(c);
    }

    @NonNull
    @SuppressLint("SimpleDateFormat")
    public static DateTime parse(@NonNull String value, @NonNull String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        try {
            Date date = format.parse(value);
            Calendar c = Calendar.getInstance(Locale.ENGLISH);
            c.setTime(date);
            return new DateTime(c);
        }
        catch (ParseException ex) {
            throw new RuntimeException("unable to parse \""+value+"\" with pattern \""+pattern+"\"",ex);
        }
    }

    @NonNull
    public static DateTime parseISODate(@NonNull String value) {
        return parse(value,"yyyy-MM-dd");
    }

    @NonNull
    public static DateTime parseISOTime(@NonNull String value) {
        return parse(value,"HH:mm:ss");
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

    @NonNull
    @SuppressLint("SimpleDateFormat")
    public String format(@NonNull String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(mCalendar.getTime());
    }

    @NonNull
    public String formatISODate(){
        return format("yyyy-MM-dd");
    }

    @NonNull
    public String formatISOTime(){
        return format("HH:mm:ss");
    }

    @NonNull
    public DateTime addMinutes(int minutes) {
        return add(Calendar.MINUTE,minutes);
    }

    @NonNull
    public DateTime addHours(int hours) {
        return add(Calendar.HOUR_OF_DAY,hours);
    }

    public DateTime addDays(int days) {
        return add(Calendar.DAY_OF_YEAR,days);
    }

    @NonNull
    DateTime add(int what, int quantity){
        Calendar copy = (Calendar) mCalendar.clone();
        copy.add(what,quantity);
        return new DateTime(copy);
    }

    @NonNull
    @Override
    public String toString() {
        return format("yyyy-MM-dd HH:mm");
    }
}
