package rahulstech.android.database.util;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.CountDownLatch;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import rahulstech.android.database.SchedulerDB;

public class DBUtils {

    public static SchedulerDB createInMemorySchedulerDB(@NonNull Context context, RoomDatabase.Callback callback) {
        RoomDatabase.Builder<SchedulerDB> builder = Room.inMemoryDatabaseBuilder(context,SchedulerDB.class);
        if (null != callback) {
            builder.addCallback(callback);
        }
        return builder.build();
    }

    public static <T> void testLiveData(LiveData<T> liveData, @NonNull Observer<T> callback) throws Exception {
        if (null == liveData) return;
        CountDownLatch latch = new CountDownLatch(1);
        Handler mainHandler = new Handler(Looper.getMainLooper());
        Observer<T> wrapper = (data) -> {
            callback.onChanged(data);
            latch.countDown();
        };
        mainHandler.post(() -> liveData.observeForever(wrapper));
        latch.await();
        mainHandler.post(()->liveData.removeObserver(wrapper));
    }
}
