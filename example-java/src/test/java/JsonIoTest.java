import com.cedarsoftware.util.io.JsonReader;
import com.cedarsoftware.util.io.JsonWriter;
import jdk.nashorn.internal.ir.debug.JSONWriter;
import org.bitemporal.Period;
import org.bitemporal.mongogson.SimpleTemporal;
import org.junit.Test;

import java.io.IOException;

public class JsonIoTest {

    @Test
    public void test() throws IOException {
        Room r = new Room(2.0f, 3.0f);
        SimpleTemporal<Room> st = new SimpleTemporal<Room>(r, new Period());
        System.err.println(JsonWriter.objectToJson(st));
        SimpleTemporal st2 = this.store(r);
        System.err.println(JsonWriter.objectToJson(st2));
    }

    private <T> SimpleTemporal<T> store(T t) throws IOException {
        String json = JsonWriter.objectToJson(new SimpleTemporal<T>(t, new Period()));
        SimpleTemporal<T> parsed = (SimpleTemporal<T>) JsonReader.jsonToJava(json);
        return parsed;
    }

}
