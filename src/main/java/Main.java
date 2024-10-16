import classes.HasdSerializable;
import classes.Person;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {
    public static void main(String... args) throws IOException, IllegalAccessException, NoSuchFieldException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException {

        // инициализация тестовых значений
        HashMap<Integer, String> m = new HashMap<Integer, String>();
        m.put(1, "hashmap-item1");
        m.put(2, "hashmap-item2");
        m.put(3, "hashmap-item3");

        ArrayList<String> s = new ArrayList();
        s.add("l1");
        s.add("l2");

        Person c = new Person.Builder()
                .setName("child")
                .setAge(10)
                .setLastName("childLastName")
                .setMapVals(m)
                .build();

        Person p = new Person.Builder()
                .setName("artyom")
                .setLastName("artyomkin")
                .setAge(22)
                .setHeight(185)
                .setStringVals(s)
                .setChildPerson(c)
                .build();
        // Добавить List<Person> внутрь p
        p.setEmbeddedVals();

        // сериализация
        p.writeToFile("./serialized-person");

        // десериализация
        Person deserializedPerson = (Person) HasdSerializable.readFromFile("./serialized-person", Person.class);

        // вывод информации
        System.out.println(deserializedPerson);
    }
}