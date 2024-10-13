package classes;

import com.github.fluency03.varint.Varint;
import exceptions.CannotSerializeFieldException;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

public class Serializer {

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
        byte[] listBytes = flattenBytes(list.stream()
                        .map(item -> {
                            try {
                                return serializeObject(item);
                            } catch (CannotSerializeFieldException e) {
                                throw new RuntimeException(e);
                            }
                        })
                        .toList());
        if (!list.isEmpty()){
            listBytes = insertToBeginning(new byte[] { TypeCodes.getTypeCode(list.get(0).getClass()) }, listBytes);
        }
        return insertLength(listBytes);
    }

    private static byte[] mapToBytes(HashMap<?, ?> map){
        byte[] mapBytes = flattenBytes(
                map.entrySet().stream()
                        .flatMap(entry -> Stream.of(entry.getKey(), entry.getValue()))
                        .map(item -> {
                            try {
                                return serializeObject(item);
                            } catch (CannotSerializeFieldException e) {
                                throw new RuntimeException(e);
                            }
                        })
                        .toList());
        if (!map.isEmpty()){
            Class<?> keyClass = map.entrySet().iterator().next().getKey().getClass();
            Class<?> valClass = map.entrySet().iterator().next().getValue().getClass();
            mapBytes = insertToBeginning(new byte[] { TypeCodes.getTypeCode(valClass) }, mapBytes);
            mapBytes = insertToBeginning(new byte[] { TypeCodes.getTypeCode(keyClass) }, mapBytes);
        }
        return insertLength(mapBytes);
    }

    protected static byte[] insertLength(byte[] bytes){
        byte[] lengthValue = Varint.encodeInt(bytes.length);
        return insertToBeginning(lengthValue, bytes);
    }

    protected static byte[] insertToBeginning(byte[] beginning, byte[] bytes){
        byte[] result = new byte[beginning.length + bytes.length];
        System.arraycopy(beginning, 0, result, 0, beginning.length);
        System.arraycopy(bytes, 0, result, beginning.length, bytes.length);
        return result;
    }

    protected static byte[] serializeObject(Object object) throws CannotSerializeFieldException{
        Class<?> clazz = object.getClass();
        if (clazz.equals(Boolean.class) || clazz.equals(boolean.class)) {
            return new byte[]{(byte) ((boolean) object ? 1 : 0)};
        } else if (clazz.equals(Short.class) || clazz.equals(short.class)) {
            return ByteBuffer.allocate(Short.BYTES).putShort((short) object).array();
        } else if (clazz.equals(Integer.class) || clazz.equals(int.class)) {
            return ByteBuffer.allocate(Integer.BYTES).putInt((int) object).array();
        } else if (clazz.equals(Long.class) || clazz.equals(long.class)) {
            return ByteBuffer.allocate(Long.BYTES).putLong((long) object).array();
        } else if (clazz.equals(Double.class) || clazz.equals(double.class)) {
            return ByteBuffer.allocate(Double.BYTES).putDouble((double) object).array();
        } else if (clazz.equals(Float.class) || clazz.equals(float.class)) {
            return ByteBuffer.allocate(Float.BYTES).putFloat((float) object).array();
        } else if (clazz.equals(Character.class) || clazz.equals(char.class)) {
            return ByteBuffer.allocate(Character.BYTES).putChar((char) object).array();
        } else if (clazz.equals(Byte.class) || clazz.equals(byte.class)) {
            return new byte[]{ (byte) object };
        } else if (clazz.equals(String.class)) {
            return insertLength(((String) object).getBytes());
        } else if (object instanceof List<?>){
            return listToBytes((List<?>) object);
        } else if (object instanceof HashMap<?,?>){
            return mapToBytes((HashMap<?,?>) object);
        } else if (object instanceof HasdSerializable) {
            return insertLength(((HasdSerializable) object).serialize());
        } else {
            throw new CannotSerializeFieldException("Cannot serialize field with type " + clazz.getName());
        }
    }


}
