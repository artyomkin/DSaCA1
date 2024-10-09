package classes;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Deserializer {
    private static Short deserializeShort(int position, int length, byte[] serializedObject){
        return ByteBuffer.wrap(Arrays.copyOfRange(serializedObject, position, position + length)).getShort();
    }

    private static Integer deserializeInteger(int position, int length, byte[] serializedObject){
        return ByteBuffer.wrap(Arrays.copyOfRange(serializedObject, position, position + length)).getInt();
    }

    private static Long deserializeLong(int position, int length, byte[] serializedObject){
        return null;
    }

    private static Double deserializeDouble(int position, int length, byte[] serializedObject){
        return null;
    }

    private static Float deserializeFloat(int position, int length, byte[] serializedObject){
        return null;
    }

    private static Character deserializeCharacter(int position, int length, byte[] serializedObject){
        return null;
    }

    private static String deserializeString(int position, int length, byte[] serializedObject){
        return null;
    }

    private static List<?> deserializeList(int position, int length, byte[] serializedObject){
        return null;
    }

    private static HashMap deserializeHashMap(int position, int length, byte[] serializedObject){
        return null;
    }

    private static HasdSerializable deserializeHasdSerializable(int position, int length, byte[] serializedObject){
        return null;
    }
}
