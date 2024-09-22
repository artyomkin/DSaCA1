import java.io.*;
import classes.Person;

public class Main {
    public static void main(String... args) throws IOException {
        Person p = new Person.Builder()
                .setName("test")
                .setLastName("testln")
                .setAge(12)
                .setHeight(1332)
                .build();
        System.out.println(p.toString());
    }
}