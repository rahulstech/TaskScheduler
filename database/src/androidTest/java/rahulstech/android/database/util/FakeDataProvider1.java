package rahulstech.android.database.util;

import androidx.annotation.NonNull;
import androidx.sqlite.db.SupportSQLiteDatabase;

class FakeDataProvider1 extends FakeDataProvider {

    @Override
    public void insert(@NonNull SupportSQLiteDatabase db) {
        addTasks(db);
    }

    void addTasks(@NonNull SupportSQLiteDatabase db) {
        db.execSQL("INSERT INTO `tasks` (`id`,`description`,`dateStart`,`state`) " +
                "VALUES (1,\"Task 1\",\"2023-04-06\",\"COMPLETE\");");

        db.execSQL("INSERT INTO `tasks` (`id`,`description`,`dateStart`,`state`) " +
                "VALUES (2,\"Task 2\",\"2023-04-07\",\"CANCEL\");");

        db.execSQL("INSERT INTO `tasks` (`id`,`description`,`dateStart`,`state`) " +
                "VALUES (3,\"Task 3\",\"2023-04-08\",\"PAUSE\");");

        db.execSQL("INSERT INTO `tasks` (`id`,`description`,`dateStart`,`state`) " +
                "VALUES (4,\"Task 4\",\"2023-04-09\",\"CREATE\");");

        db.execSQL("INSERT INTO `tasks` (`id`,`description`,`dateStart`,`state`) " +
                "VALUES (5,\"Task 5\",\"2023-04-08\",\"START\");");
    }
}
