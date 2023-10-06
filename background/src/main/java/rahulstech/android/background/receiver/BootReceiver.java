package rahulstech.android.background.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import rahulstech.android.background.alarm.Alarm;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Alarm.startScheduler(context);
        }
    }
}