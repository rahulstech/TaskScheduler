package rahulstech.android.database.migration;

import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@SuppressWarnings("unused")
public class MIGRATION_1_2 extends Migration {

    public MIGRATION_1_2() {
        super(1, 2);
    }

    @Override
    public void migrate(@NonNull SupportSQLiteDatabase db) {
        db.execSQL("DELETE FROM `task_data`");
        db.execSQL("CREATE INDEX `index_task_data_taskId` ON `task_data`(`taskId`)");
        db.execSQL("INSERT INTO `task_data` (`taskId`,`type`,`text4`) SELECT `id` `taskId`, \"task_frequency\" `type`, \"ONCE\" `text4` FROM `tasks`");

        db.execSQL("CREATE TABLE `tasks_new` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `description` TEXT NOT NULL, `state` TEXT NOT NULL, `date` TEXT NOT NULL, `time` TEXT, `reminder` TEXT, `origin` INTEGER, `note` TEXT)");
        db.execSQL("INSERT INTO `tasks_new` (`id`,`description`,`state`,`date`,`time`) SELECT `id`,`description`,`state`,`dateStart` `date`,`timeStart` `time` FROM `tasks`");
        db.execSQL("DROP TABLE `tasks`");
        db.execSQL("ALTER TABLE `tasks_new` RENAME TO `tasks`");

        db.execSQL("CREATE VIEW `tasks_brief` AS "+"SELECT t1.*," +
                "       t2.connections,t2.rangeStart,t2.rangeEnd," +
                "       t3.text4 frequency," +
                "       t3.text5 unit," +
                "       t3.long1 interval" +
                "  FROM tasks t1" +
                "       LEFT JOIN" +
                "       (" +
                "           SELECT id, CASE WHEN origin IS NOT NULL THEN origin ELSE id END origin," +
                "                  count(id) connections," +
                "                  min(date(date) ) rangeStart," +
                "                  max(date(date) ) rangeEnd" +
                "             FROM tasks" +
                "            GROUP BY CASE WHEN origin IS NOT NULL THEN origin ELSE id END" +
                "       )" +
                "       t2 ON t1.id = t2.origin OR " +
                "             t1.origin = t2.origin" +
                "       LEFT JOIN" +
                "       task_data t3 ON type IN(\"task_frequency\") AND " +
                "                       taskId = t2.origin;");
    }
}
