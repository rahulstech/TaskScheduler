package rahulstech.android.background.alarm;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import rahulstech.android.background.service.SchedulerService;

@SuppressWarnings("unused")
public class Alarm {

    public static void startScheduler(@NonNull Context context) {
        Intent intent = new Intent(context, SchedulerService.class);
        intent.setAction(SchedulerService.ACTION_START);
        ContextCompat.startForegroundService(context,intent);
    }

    public static void stopScheduler(@NonNull Context context) {
        Intent intent = new Intent(context, SchedulerService.class);
        intent.setAction(SchedulerService.ACTION_STOP);
        ContextCompat.startForegroundService(context,intent);
    }

    public static void once(@NonNull Context context, long when, @NonNull Bundle args) {

    }

    public static void cancel(@NonNull Context context, @NonNull Bundle args) {

    }
}
