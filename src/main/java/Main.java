import classes.Person;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String... args) throws IOException, IllegalAccessException {
        Object p = new Person[]{new Person.Builder()
                .setName("A")
                .setLastName("B")
                .setHeight(123)
                .setAge(123)
                .build()};

        List<Integer> a = new ArrayList<Integer>();
        a.add(1);
        a.add(2);
        Field[] fields = a.getClass().getDeclaredFields();
        for (Field field : fields){
            if (!Modifier.isStatic(field.getModifiers())){
                System.out.println(field.getName());
                field.setAccessible(tru);
                System.out.println(field.get(a));
            }
        }
        //System.out.println(p instanceof Object[]);
    }
}