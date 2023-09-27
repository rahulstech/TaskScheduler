package rahulstech.android.background.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class SetAlarmsOnBootService extends Service {
    public SetAlarmsOnBootService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}