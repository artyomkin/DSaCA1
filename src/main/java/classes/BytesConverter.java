package classes;

import com.github.fluency03.varint.Varint;
import exceptions.CannotSerializeFieldException;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

public class BytesConverter {

    private static byte[] shortToBytes(short number){
        return ByteBuffer.allocate(Short.BYTES).putShort(number).array();
    }

    private static byte[] intToBytes(int number){
        return ByteBuffer.allocate(Integer.BYTES).putInt(number).array();
    }

    private static byte[] longToBytes(long number) {
        return ByteBuffer.allocate(Long.BYTES).putLong(number).array();
    }

    private static byte[] floatToBytes(float number) {
        return ByteBuffer.allocate(Float.BYTES).putFloat(number).array();
    }

    private static byte[] doubleToBytes(double number) {
        return ByteBuffer.allocate(Double.BYTES).putDouble(number).array();
    }

    private static byte booleanToBytes(boolean bool) {
        return (byte) (bool ? 1 : 0);
    }

    private static byte[] charToBytes(char c) {
        return ByteBuffer.allocate(Character.BYTES).putChar(c).array();
    }


    protected static byte[] insertLength(byte[] bytes){
        byte[] lengthValue = Varint.encodeInt(bytes.length);
        byte[] result = new byte[lengthValue.length + bytes.length];
        System.arraycopy(lengthValue, 0, result, 0, lengthValue.length);
        System.arraycopy(bytes, 0, result, lengthValue.length, bytes.length);
        return result;
    }

    private static byte[] hasdSerializableToBytes(HasdSerializable hasdSerializable){
        return insertLength(hasdSerializable.serializeWithoutHeaders());
    }

    private static byte[] stringToBytes(String str) {
        return insertLength(str.getBytes());
    }

    private static int getTotalFieldsLength(List<byte[]> serializedFields) {
        return serializedFields.stream()
                .map(fieldBytes -> fieldBytes.length)
                .reduce(Integer::sum).get();
    }

    protected static byte[] flattenBytes(List<byte[]> bytesList) {
        byte[] flattenedBytes = new byte[getTotalFieldsLength(bytesList)];
        int copyPosition = 0;
        for (byte[] bytes : bytesList) {
            System.arraycopy(bytes, 0, flattenedBytes, copyPosition, bytes.length);
            copyPosition += bytes.length;
        }
        return flattenedBytes;
    }

    private static byte[] listToBytes(List<?> list) {
        return insertLength(flattenBytes(
                list.stream()
                    .map(item -> {
                        try {
                            return serializeObject(item);
                        } catch (CannotSerializeFieldException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .toList()
        ));
    }

    private static byte[] mapToBytes(HashMap<?, ?> map){
       return insertLength(flattenBytes(
               map.entrySet().stream()
                    .flatMap(entry -> Stream.of(entry.getKey(), entry.getValue()))
                    .map(item -> {
                        try {
                            return serializeObject(item);
                        } catch (CannotSerializeFieldException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .toList()
       ));
    }

    protected static byte[] serializeObject(Object fieldValue) throws CannotSerializeFieldException{
        Class<?> fieldType = fieldValue.getClass();
        if (fieldType.equals(Boolean.class) || fieldType.equals(boolean.class)) {
            return new byte[]{booleanToBytes((boolean) fieldValue)};
        } else if (fieldType.equals(Short.class) || fieldType.equals(short.class)) {
            return shortToBytes((short) fieldValue);
        } else if (fieldType.equals(Integer.class) || fieldType.equals(int.class)) {
            return intToBytes((int) fieldValue);
        } else if (fieldType.equals(Long.class) || fieldType.equals(long.class)) {
            return longToBytes((long) fieldValue);
        } else if (fieldType.equals(Double.class) || fieldType.equals(double.class)) {
            return doubleToBytes((double) fieldValue);
        } else if (fieldType.equals(Float.class) || fieldType.equals(float.class)) {
            return floatToBytes((float) fieldValue);
        } else if (fieldType.equals(Character.class) || fieldType.equals(char.class)) {
            return charToBytes((char) fieldValue);
        } else if (fieldType.equals(Byte.class) || fieldType.equals(byte.class)) {
            return new byte[]{ (byte) fieldValue };
        } else if (fieldType.equals(String.class)) {
            return stringToBytes((String) fieldValue);
        } else if (fieldValue instanceof List<?>){
            return listToBytes((List<?>) fieldValue);
        } else if (fieldValue instanceof HashMap<?,?>){
            return mapToBytes((HashMap<?,?>) fieldValue);
        } else if (fieldValue instanceof HasdSerializable) {
            return hasdSerializableToBytes((HasdSerializable) fieldValue);
        } else {
            throw new CannotSerializeFieldException("Cannot serialize field with type " + fieldType.getName());
        }
    }


}
