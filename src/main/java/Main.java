import classes.Person;
import com.github.fluency03.varint.Varint;
import org.apache.maven.profiles.activation.SystemPropertyProfileActivator;
import scala.Tuple2;

import java.io.*;
import java.nio.ByteBuffer;
import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Main {
    public static void main(String... args) throws IOException, IllegalAccessException {
        List<Integer> l = List.of(1,2,3,4,5);
        List<String> s = List.of("l1", "l2", "l3", "l4");
        HashMap<Integer, String> m = new HashMap<Integer, String>();
        m.put(1, "hashmap-item1");
        m.put(2, "hashmap-item2");
        m.put(3, "hashmap-item3");
        Person p = new Person.Builder()
                .setIntVals(l)
                .setStringVals(s)
                .setMapVals(m)
                .build();
        p.writeToFile("./serialized-person");
        FileInputStream fis = new FileInputStream("./serialized-person");
        byte[] bytes = fis.readAllBytes();
        for (byte b : bytes){
               System.out.println("byte: " + b);
        }
    }
}