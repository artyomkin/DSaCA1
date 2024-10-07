import classes.Person;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import static java.nio.file.Files.readAllBytes;

public class Main {
    public static void main(String... args) throws IOException, IllegalAccessException {
        Person grandChildPerson = new Person.Builder()
                .setName("grand-child-name")
                .setLastName("grand-child-lastname")
                .setHeight(1123)
                .setAge(1231212)
                .setCharVal('c')
                .setBooleanVal(true)
                .setByteVal((byte) 127)
                .setDoubleVal(32132.231)
                .setFloatVal(231.2341f)
                .setLongVal(9345984142L)
                .setShortVal((short) 312)
                .build();
        Person childPerson = new Person.Builder()
                .setName("child-name")
                .setLastName("child-lastname")
                .setHeight(1123)
                .setAge(1231212)
                .setCharVal('c')
                .setBooleanVal(true)
                .setByteVal((byte) 127)
                .setDoubleVal(32132.231)
                .setFloatVal(231.2341f)
                .setLongVal(9345984142L)
                .setShortVal((short) 312)
                .setChildPerson(grandChildPerson)
                .build();
        Person parentPerson = new Person.Builder()
                .setName("parent-name")
                .setLastName("parent-lastname")
                .setHeight(1123)
                .setAge(1231212)
                .setCharVal('c')
                .setBooleanVal(true)
                .setByteVal((byte) 127)
                .setDoubleVal(32132.231)
                .setFloatVal(231.2341f)
                .setLongVal(9345984142L)
                .setShortVal((short) 312)
                .setChildPerson(childPerson)
                .build();
        childPerson.writeToFile("./child-serialized-person");
        parentPerson.writeToFile("./parent-serialized-person");
        FileInputStream fis = new FileInputStream("./parent-serialized-person");
        byte[] bytes = fis.readAllBytes();
        for (byte b : bytes){
            System.out.println("byte: " + b);
        }

    }
}