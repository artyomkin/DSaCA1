package classes;

import com.github.fluency03.varint.Varint;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;

public class Deserializer {
    private static final int PROTOCOL_SIGNATURE_OFFSET = 0;
    private static final int PROCOTOL_VERSION_OFFSET = PROTOCOL_SIGNATURE_OFFSET + 4;
    private static final int NULL_FIELDS_OFFSET = PROCOTOL_VERSION_OFFSET + 3;

    private static String deserializeProtocolSignature(byte[] bytes, int offset){
        return null;
    }
    private static String deserializeProtocolSignature(byte[] bytes){
        return deserializeProtocolSignature(bytes, PROTOCOL_SIGNATURE_OFFSET);
    }

    private static List<Integer> deserializeProtocolVersion(byte[] bytes, int offset){
        return null;
    }
    private static List<Integer> deserializeProtocolVersion(byte[] bytes){
        return deserializeProtocolVersion(bytes, PROCOTOL_VERSION_OFFSET);
    }

    private static List<Integer> deserializeNullFieldPositions(byte[] bytes, int offset){
        return null;
    }
    private static List<Integer> deserializeNullFieldPositions(byte[] bytes){
        return deserializeNullFieldPositions(bytes, NULL_FIELDS_OFFSET);
    }

    private static Object deserializeValue(Class<?> fieldType, byte[] bytes) throws IllegalAccessException {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);

        if (fieldType.equals(boolean.class) || fieldType.equals(Boolean.class)) {
            return buffer.get() != 0;
        } else if (fieldType.equals(byte.class) || fieldType.equals(Byte.class)) {
            return buffer.get();
        } else if (fieldType.equals(short.class) || fieldType.equals(Short.class)) {
            return buffer.getShort();
        } else if (fieldType.equals(int.class) || fieldType.equals(Integer.class)) {
            return buffer.getInt();
        } else if (fieldType.equals(long.class) || fieldType.equals(Long.class)) {
            return buffer.getLong();
        } else if (fieldType.equals(float.class) || fieldType.equals(Float.class)) {
            return buffer.getFloat();
        } else if (fieldType.equals(double.class) || fieldType.equals(Double.class)) {
            return buffer.getDouble();
        } else if (fieldType.equals(char.class) || fieldType.equals(Character.class)) {
            return buffer.getChar();
        } else if (fieldType.equals(String.class)) {
            int length = (Integer) Varint.decodeToInt(bytes)._1();
            int offset = Varint.extractLength(bytes, 0);
            byte[] stringBytes = new byte[length];
            buffer.get(offset, stringBytes);
            return new String(stringBytes);
        //} else if (List.class.isAssignableFrom(fieldType)) {
        //    // Обработка десериализации списка
        //    deserializedValue = deserializeList(buffer);
        //} else if (Map.class.isAssignableFrom(fieldType) || HashMap.class.isAssignableFrom(fieldType)) {
        //    // Обработка десериализации карты
        //    deserializedValue = deserializeMap(buffer);
        //} else if (HasdSerializable.class.isAssignableFrom(fieldType)) {
        //    // Обработка десериализации пользовательского объекта
        //    deserializedValue = deserializeCustomObject(buffer, fieldType);
        } else {
            throw new IllegalArgumentException("Unsupported field type: " + fieldType.getName());
        }
    }

    //private static List<?> deserializeList(ByteBuffer buffer) {
    //    // Логика десериализации списка
    //    // Необходимо считать длину списка и затем рекурсивно десериализовать каждый
    //    // элемент
    //    return null;
    //}

    //private static Map<?, ?> deserializeMap(ByteBuffer buffer) {
    //    // Логика десериализации карты
    //    // Необходимо считать количество пар ключ-значение и десериализовать их
    //    return null;
    //}

    //private static Object deserializeCustomObject(ByteBuffer buffer, Class<?> fieldType) {
    //    // Логика десериализации пользовательского объекта, реализующего
    //    // HasdSerializable
    //    try {
    //        HasdSerializable obj = (HasdSerializable) fieldType.getDeclaredConstructor().newInstance();
    //        int length = Varint.decodeInt(buffer);
    //        byte[] objBytes = new byte[length];
    //        buffer.get(objBytes);
    //        return obj.deserialize(objBytes);
    //    } catch (Exception e) {
    //        throw new RuntimeException("Failed to deserialize custom object", e);
    //    }
    //}
}
