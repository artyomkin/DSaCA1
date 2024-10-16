package classes;

import com.github.fluency03.varint.Varint;
import exceptions.CannotSerializeFieldException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

public class SerializerImpl implements Serializer{

    private OrdinarySerializer ordinarySerializer;

    public SerializerImpl(){
        this.ordinarySerializer = new OrdinarySerializerImpl();
    }

    private int getTotalFieldsLength(List<byte[]> serializedFields) {
        return serializedFields.stream()
                .map(fieldBytes -> fieldBytes.length)
                .reduce(Integer::sum).get();
    }

    // TODO: избавиться от этого метода, считать байты под сериализацию заранее.
    public byte[] flattenBytes(List<byte[]> bytesList) {
        byte[] flattenedBytes = new byte[getTotalFieldsLength(bytesList)];
        int copyPosition = 0;
        for (byte[] bytes : bytesList) {
            System.arraycopy(bytes, 0, flattenedBytes, copyPosition, bytes.length);
            copyPosition += bytes.length;
        }
        return flattenedBytes;
    }

    private byte[] stringToBytes(String s){
        return insertLength(s.getBytes());
    }

    private byte[] hasdSerializableToBytes(HasdSerializable h){
        return insertLength(h.serialize());
    }

    private byte[] listToBytes(List<?> list) {
        if (list.isEmpty()) return new byte[0];

        byte[] listBytes = flattenBytes(list.stream()
                        .map(item -> {
                            try {
                                return serializeObject(item);
                            } catch (CannotSerializeFieldException e) {
                                throw new RuntimeException(e);
                            }
                        })
                        .toList());
        listBytes = insertToBeginning(((Integer)list.size()).byteValue(), listBytes);
        String elementClassName = list.get(0).getClass().getName();
        listBytes = insertToBeginning(stringToBytes(elementClassName), listBytes);
        return listBytes;
    }

    private byte[] mapToBytes(HashMap<?, ?> map){
        if (map.isEmpty()) return new byte[0];

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
        String keyClassName = map.entrySet().iterator().next().getKey().getClass().getName();
        String valClassName = map.entrySet().iterator().next().getValue().getClass().getName();
        mapBytes = insertToBeginning(((Integer) map.size()).byteValue(), mapBytes);
        mapBytes = insertToBeginning(stringToBytes(valClassName), mapBytes);
        mapBytes = insertToBeginning(stringToBytes(keyClassName), mapBytes);
        return mapBytes;
    }

    public byte[] insertLength(byte[] bytes){
        byte[] lengthValue = Varint.encodeInt(bytes.length);
        return insertToBeginning(lengthValue, bytes);
    }

    protected byte[] insertToBeginning(byte beginning, byte[] bytes){
        return insertToBeginning(new byte[] { beginning }, bytes);
    }

    protected byte[] insertToBeginning(byte[] beginning, byte[] bytes){
        byte[] result = new byte[beginning.length + bytes.length];
        System.arraycopy(beginning, 0, result, 0, beginning.length);
        System.arraycopy(bytes, 0, result, beginning.length, bytes.length);
        return result;
    }

    public byte[] serializeObject(Object object) throws CannotSerializeFieldException{
        Class<?> objectClass = object.getClass();

        if (objectClass.isAssignableFrom(Boolean.class)) {
            return ordinarySerializer.toBytes((Boolean) object);
        } else if (objectClass.isAssignableFrom(Short.class)) {
            return ordinarySerializer.toBytes((Short) object);
        } else if (objectClass.isAssignableFrom(Integer.class)) {
            return ordinarySerializer.toBytes((Integer) object);
        } else if (objectClass.isAssignableFrom(Long.class)) {
            return ordinarySerializer.toBytes((Long) object);
        } else if (objectClass.isAssignableFrom(Double.class)) {
            return ordinarySerializer.toBytes((Double) object);
        } else if (objectClass.isAssignableFrom(Float.class)) {
            return ordinarySerializer.toBytes((Float) object);
        } else if (objectClass.isAssignableFrom(Character.class)) {
            return ordinarySerializer.toBytes((Character) object);
        } else if (objectClass.isAssignableFrom(Byte.class)) {
            return ordinarySerializer.toBytes((Byte) object);
        } else if (objectClass.isAssignableFrom(String.class)) {
            return stringToBytes((String) object);
        } else if (object instanceof ArrayList<?>){
            return listToBytes((List<?>) object);
        } else if (object instanceof HashMap<?,?>){
            return mapToBytes((HashMap<?,?>) object);
        } else if (object instanceof HasdSerializable) {
            return hasdSerializableToBytes((HasdSerializable) object);
        } else {
            throw new CannotSerializeFieldException("Cannot serialize field with type " + objectClass.getName());
        }
    }


}
