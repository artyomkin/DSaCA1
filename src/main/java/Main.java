import classes.Person;

import java.io.*;
import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Main {
    public static void main(String... args) throws IOException, IllegalAccessException {
        List<Integer> l = List.of(1,2,3,4,5);
        List<String> s = List.of("asdfks", "lkfjsdl", "sdfjsdew", "oiudf");
        HashMap<Integer, String> m = new HashMap<Integer, String>();
        m.put(12, "slkdf");
        m.put(12312, "sdfslkdf");
        m.put(1221, "xxxsslkdf");
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