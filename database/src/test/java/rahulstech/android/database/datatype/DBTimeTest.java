package rahulstech.android.database.datatype;

import org.junit.Test;

import java.util.Calendar;
import java.util.Locale;

import static org.junit.Assert.assertEquals;

public class DBTimeTest {

    @Test
    public void valueOf() {
        Calendar c = Calendar.getInstance(Locale.ENGLISH);
        c.set(Calendar.HOUR_OF_DAY,18);
        c.set(Calendar.MINUTE,20);
        DBTime expected = new DBTime(c);
        DBTime actual = DBTime.valueOf("18:20:00");
        assertEquals(expected,actual);
    }

    @Test
    public void testToString() {
        String expected = "17:15:00";
        Calendar c = Calendar.getInstance(Locale.ENGLISH);
        c.set(Calendar.HOUR_OF_DAY,17);
        c.set(Calendar.MINUTE,15);
        DBTime time = new DBTime(c);
        String actual = time.toString();
        assertEquals(expected,actual);
    }
}