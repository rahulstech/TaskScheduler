package rahulstech.android.background.alarm;

import android.app.AlarmManager;
import android.content.Context;

import androidx.annotation.NonNull;

@SuppressWarnings("unused")
public class Alarm {

    public static void once(@NonNull Context context) {
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

    }

    public static void cancel(@NonNull Context context) {}
}
