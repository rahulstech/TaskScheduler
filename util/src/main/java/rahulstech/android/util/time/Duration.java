package rahulstech.android.util.time;

import androidx.annotation.NonNull;

public class Duration {

    private int year;
    private int month;
    private int days;
    private int hours;
    private int minutes;

    @NonNull
    public static Duration between(@NonNull DateTime from, @NonNull DateTime to) {
        return null;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
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
