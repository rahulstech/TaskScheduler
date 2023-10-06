package rahulstech.android.util.time;

import androidx.annotation.NonNull;

public class Duration {

    private int years;
    private int months;
    private int days;
    private int hours;
    private int minutes;

    private Duration(int years, int months, int days, int hours, int minutes) {
        this.years = years;
        this.months = months;
        this.days = days;
        this.hours = hours;
        this.minutes = minutes;
    }

    @NonNull
    public static Duration between(@NonNull DateTime from, @NonNull DateTime to) {
        int years = to.getYear()-from.getYear();
        int months = to.getMonth()-from.getMonth();
        int days = to.getDate()-from.getDate();
        int hours = to.getHourOfDay()-from.getHourOfDay();
        int minutes = to.getMinute()-from.getMinute();
        return new Duration(years,months,days,hours,minutes);
    }

    public int getYears() {
        return years;
    }

    public int getMonths() {
        return months;
    }

    public int getDays() {
        return days;
    }

    public int getHours() {
        return hours;
    }

    public int getMinutes() {
        return minutes;
    }
}
