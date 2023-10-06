package rahulstech.android.background.notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationChannelCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import rahulstech.android.background.R;

@SuppressWarnings("unused")
public class NotificationUtil {

    public static final String CHANNEL_SCHEDULER_SERVICE = "scheduler_service";

    public static final String CHANNEL_REMINDER = "reminder";

    public static final int NOTIFY_REMINDER_SERVICE = 1;

    public static final int NOTIFY_TASK_START = 100;

    public static void newChannelForSchedulerService(@NonNull Context context) {
        NotificationChannelCompat channel = new NotificationChannelCompat.Builder(CHANNEL_SCHEDULER_SERVICE,NotificationManagerCompat.IMPORTANCE_LOW)
                .setName("Scheduler Service")
                .setDescription("Notification for foreground service for managing reminders")
                .setShowBadge(false)
                .setVibrationEnabled(false)
                .setLightsEnabled(false)
                .build();
        NotificationManagerCompat.from(context).createNotificationChannel(channel);
    }

    public static void newChannelForReminder(@NonNull Context context) {
        NotificationChannelCompat channel = new NotificationChannelCompat.Builder(CHANNEL_REMINDER,NotificationManagerCompat.IMPORTANCE_MAX)
                .setName("Reminder")
                .setDescription("Notifications related to task reminder")
                .setLightsEnabled(false)
                .setShowBadge(false)
                //.setVibrationEnabled(true)
                //.setVibrationPattern(new long[0]) // TODO: add vibration pattern
                //.setSound(null,null) // TODO: set notification sound
                .build();
        NotificationManagerCompat.from(context).createNotificationChannel(channel);
    }

    public static boolean isChannelExists(@NonNull Context context, @NonNull String channelId) {
        if (Build.VERSION.SDK_INT >= 26) {
            return null != NotificationManagerCompat.from(context).getNotificationChannelCompat(channelId);
        }
        return true;
    }

    public static void createChannelIfNeeded(@NonNull Context context, @NonNull String channelId) {
        if (Build.VERSION.SDK_INT >= 26 && !isChannelExists(context,channelId)) {
            if (CHANNEL_SCHEDULER_SERVICE.equals(channelId)) {
                newChannelForSchedulerService(context);
            }
            else if (CHANNEL_REMINDER.equals(channelId)) {
                newChannelForReminder(context);
            }
            else {
                throw new RuntimeException("channel with id="+channelId+" can not be created");
            }
        }
    }

    public static Notification newSchedulerServiceNotification(@NonNull Context context) {
        createChannelIfNeeded(context, CHANNEL_SCHEDULER_SERVICE);
        return new NotificationCompat.Builder(context, CHANNEL_SCHEDULER_SERVICE)
                .setSmallIcon(R.drawable.ic_scheduler)
                .setContentTitle(context.getText(R.string.scheduler_service_notification_title))
                .setContentText(context.getText(R.string.reminder_service_notification_message))
                .setOngoing(true)
                .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
                .build();
    }

    @NonNull
    public static Notification newSimpleNotification(@NonNull Context context, @NonNull String channelId,
                                             @NonNull CharSequence title, @NonNull CharSequence message,
                                             @Nullable PendingIntent contentIntent) {
        createChannelIfNeeded(context,channelId);
        return new NotificationCompat.Builder(context,channelId)
                .setSmallIcon(R.drawable.ic_reminder)
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(contentIntent)
                .build();
    }

    public static void notify(@NonNull Context context, int id, @NonNull Notification notification) {
        NotificationManagerCompat.from(context).notify(id,notification);
    }
}
