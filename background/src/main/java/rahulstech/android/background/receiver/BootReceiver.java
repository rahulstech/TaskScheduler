package rahulstech.android.background.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.content.ContextCompat;
import rahulstech.android.background.service.SetAlarmsOnBootService;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Intent serviceIntent = new Intent(context, SetAlarmsOnBootService.class);
            ContextCompat.startForegroundService(context, serviceIntent);
        }
    }
}