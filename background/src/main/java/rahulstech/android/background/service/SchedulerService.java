package rahulstech.android.background.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import rahulstech.android.background.notification.NotificationUtil;

@SuppressWarnings("unused")
public class SchedulerService extends Service {

    private static final String TAG = "SchedulerService";

    public static final String ACTION_START = "action_start_foreground";

    public static final String ACTION_STOP = "action_stop_foreground";

    public static final String ACTION_SET_REMINDER = "action_set_reminder";

    public static final String ACTION_CANCEL_REMINDER = "action_cancel_reminder";

    public static final String EXTRA_REMINDER_ARGS = "extra_reminder_args";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {return null;}

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String action = intent.getAction();

        switch (action) {
            case ACTION_START: {
                handleStartForeground();
            }
            break;
            case ACTION_STOP: {
                stopForeground(true);
            }
            break;
            case ACTION_SET_REMINDER: {
                handleSetReminder(intent.getBundleExtra(EXTRA_REMINDER_ARGS));
            }
            break;
            case ACTION_CANCEL_REMINDER: {
                handleCancelReminder(intent.getBundleExtra(EXTRA_REMINDER_ARGS));
            }
        }
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void handleStartForeground() {
        Log.i(TAG,"starting foreground scheduler service");
        Notification notification = NotificationUtil.newSchedulerServiceNotification(this);
        if (Build.VERSION.SDK_INT >= 29) {
            startForeground(NotificationUtil.NOTIFY_REMINDER_SERVICE,
                    notification,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_NONE);
        }
        else {
            startForeground(NotificationUtil.NOTIFY_REMINDER_SERVICE, notification);
        }
    }

    private void handleSetReminder(@NonNull Bundle args) {

    }

    private void handleCancelReminder(@NonNull Bundle args) {

    }
}