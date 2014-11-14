import com.google.gson.reflect.TypeToken;
import org.bitemporal.BitemporalContext;
import org.bitemporal.Period;
import org.bitemporal.mongogson.BitemporalMongoDb;
import org.bitemporal.mongogson.SimpleTemporal;
import org.joda.time.DateTime;
import org.junit.Test;

import java.lang.reflect.Type;
import java.util.Date;

import static org.junit.Assert.assertEquals;


public class BitemporalTest {

    private static final Date JAN2015 = new DateTime(2015,1,1,0,0,0).toDate();
    private static final Date BETWEEN = new DateTime(2015, 1, 15, 0, 0, 0).toDate();
    private static final Date FEB2015 = new DateTime(2015,2,1,0,0,0).toDate();
    private static final Date AFTER = new DateTime(2015,2,15,0,0,0).toDate();

    @Test
    public void test() {
        BitemporalMongoDb bitemporalMongoDb = new BitemporalMongoDb();
        bitemporalMongoDb.clearCollection(new Room());
        String id1 = bitemporalMongoDb.store(new Room(25.0f, 2.0f), new Period(), this.roomType());
        bitemporalMongoDb.update(new Room(24.0f, 2.0f), id1, new Period(JAN2015, FEB2015), roomType());
        Room roomInJanuary = bitemporalMongoDb.find(Room.class, id1, new BitemporalContext(new Date(), BETWEEN), roomType()).get();
        assertEquals(roomInJanuary.getSize(), 24.0f, 0.1f);
        Room roomInFebruary = bitemporalMongoDb.find(Room.class, id1, new BitemporalContext(new Date(), AFTER), roomType()).get();
        assertEquals(roomInFebruary.getSize(), 25.0f, 0.1f);
    }

    private Type roomType() {
        return new TypeToken<SimpleTemporal<Room>>(){}.getType();
    }

}
