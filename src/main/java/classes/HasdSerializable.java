package classes;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.stream.Stream;

import static classes.HasdSerializableImpl.*;

public interface HasdSerializable {
    byte[] serialize();
    void writeToFile(String path) throws IOException;

    static boolean checkSignature(String str){
        return SERIALIZATION_PROTOCOL_NAME.equals(str);
    }

    static boolean checkVersion(List<Byte> v){
        return v.get(0).equals(SERIALIZATION_PROTOCOL_MAJOR_VERSION)
                && v.get(1).equals(SERIALIZATION_PROTOCOL_MINOR_VERSION)
                && v.get(2).equals(SERIALIZATION_PROTOCOL_PATCH_VERSION);
    }

    static List<Field> getAllFields(Class<?> cl) {
        return Stream.of(cl.getDeclaredFields())
                .filter(field -> !Modifier.isStatic(field.getModifiers()) && !Modifier.isTransient(field.getModifiers()))
                .peek(field -> field.setAccessible(true))
                .toList();
    }

    static HasdSerializable deserialize(byte[] serializedObject, Class<?> cl) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        List<Field> allFields = getAllFields(cl);
        Deserializer deserializer = new DeserializerImpl(serializedObject);
        if (!checkSignature(deserializer.getProtocolSignature())){
            System.out.println("Signature is not " + SERIALIZATION_PROTOCOL_NAME);
            return null;
        }
        if (!checkVersion(deserializer.getProtocolVersion())){
            System.out.println("Incompatible versions");
            return null;
        }

        HasdSerializable result = (HasdSerializable) cl.getDeclaredConstructor().newInstance();
        List<Byte> nullFields = deserializer.getNullFields();
        for (int i = 0; i < allFields.size(); i++) {
            if (nullFields.contains((byte) i)) {
                continue;
            }
            try {
                allFields.get(i).set(result, deserializer.nextValue(allFields.get(i).getType()));
            } catch (IllegalAccessException e){
                System.out.println("Illegal access to field " + allFields.get(i).getName());
            } catch (ClassNotFoundException e){
                System.out.println(e.getMessage());
            }
        }
        return result;
    }

    static HasdSerializable readFromFile(String path, Class<?> cl) throws IOException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        FileInputStream fis = new FileInputStream(path);
        return HasdSerializable.deserialize(fis.readAllBytes(), cl);
    }

}
