package rahulstech.android.database.datatype;

import org.junit.Test;

import java.util.Calendar;
import java.util.Locale;

import static org.junit.Assert.assertEquals;

public class DBDateTest {

    @Test
    public void of() {
        Calendar c = Calendar.getInstance(Locale.ENGLISH);
        c.set(2023,Calendar.AUGUST,3);
        DBDate expected = new DBDate(c);
        DBDate actual = DBDate.of(2023,7,3);
        assertEquals(expected,actual);
    }
    @Test
    public void valueOf() {
        Calendar c = Calendar.getInstance(Locale.ENGLISH);
        c.set(2023,Calendar.SEPTEMBER,25);
        DBDate expected = new DBDate(c);
        DBDate actual = DBDate.valueOf("2023-09-25");
        assertEquals(expected,actual);
    }

    @Test
    public void testToString() {
        String expected = "2023-09-25";
        Calendar c = Calendar.getInstance(Locale.ENGLISH);
        c.set(2023,Calendar.SEPTEMBER,25);
        DBDate date = new DBDate(c);
        String actual = date.toString();
        assertEquals(expected,actual);
    }
}