package rahulstech.android.database.migration;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.room.testing.MigrationTestHelper;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import rahulstech.android.database.SchedulerDB;
import rahulstech.android.database.util.FakeDataProvider;

@RunWith(AndroidJUnit4.class)
public class MigrationTest {

    private static final String MIGRATION_TEST_DB_NAME = "migration-test-scheduler_db.sqlite3";

    @Rule
    public MigrationTestHelper mTestHelper;

    public MigrationTest() {
        mTestHelper = new MigrationTestHelper(InstrumentationRegistry.getInstrumentation(), SchedulerDB.class);
    }

    @Test
    public void testMigration_1_to_2() throws Exception {
        SupportSQLiteDatabase db = mTestHelper.createDatabase(MIGRATION_TEST_DB_NAME,1);
        FakeDataProvider.getForDBVersion(1).insert(db);
        mTestHelper.closeWhenFinished(db);

        db = mTestHelper.runMigrationsAndValidate(MIGRATION_TEST_DB_NAME,2,true,new MIGRATION_1_2());
        mTestHelper.closeWhenFinished(db);
    }
}
