import classes.Person;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import static java.nio.file.Files.readAllBytes;

public class Main {
    public static void main(String... args) throws IOException, IllegalAccessException {
        Person p = new Person.Builder()
                .setName("some-name")
                .setLastName("some-lastname")
                .setHeight(1123)
                .setAge(1231212)
                .build();
        p.writeToFile("./serialized-person");
        FileInputStream fis = new FileInputStream("./serialized-person");
        byte[] bytes = fis.readAllBytes();
        for (byte b : bytes){
            System.out.println("byte: " + b);
        }

    }
}