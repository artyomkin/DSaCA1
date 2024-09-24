import java.io.*;
import java.util.Arrays;

import classes.Person;
import com.github.fluency03.varint.Varint;

public class Main {
    public static void main(String... args) throws IOException {
        Person p = new Person.Builder()
                .setName("test")
                .setLastName("test")
                .setHeight(123)
                .setAge(321)
                .build();

        System.out.println(Arrays.toString(p.toBytes()));
    }
}