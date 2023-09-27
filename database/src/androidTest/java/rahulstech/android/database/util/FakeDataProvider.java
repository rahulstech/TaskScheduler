package rahulstech.android.database.util;

import androidx.annotation.NonNull;
import androidx.sqlite.db.SupportSQLiteDatabase;

public abstract class FakeDataProvider {

    public static FakeDataProvider getForDBVersion(int dbVersion) {
        if (1 == dbVersion) {
            return new FakeDataProvider1();
        }
        else {
            throw new RuntimeException("unknown dbVersion="+dbVersion);
        }
    }

    public abstract void insert(@NonNull SupportSQLiteDatabase db);
}
