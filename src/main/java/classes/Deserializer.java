package classes;

import com.github.fluency03.varint.Varint;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.*;

public class Deserializer {
    private static final int PROTOCOL_SIGNATURE_OFFSET = 0;
    private static final int PROCOTOL_VERSION_OFFSET = PROTOCOL_SIGNATURE_OFFSET + 4;
    private static final int NULL_FIELDS_OFFSET = PROCOTOL_VERSION_OFFSET + 3;

    private ByteBuffer bytes;
    private String protocolSignature;
    private List<Byte> protocolVersion;
    private List<Byte> nullFields;

    public Deserializer(byte[] bytes){
        this.bytes = ByteBuffer.wrap(bytes);
        this.protocolSignature = deserializeProtocolSignature();
        this.protocolVersion = deserializeProtocolVersion();
        this.nullFields = deserializeNullFieldPositions();
    }

    private byte nextByte(ByteBuffer bb){
        return nextBytes(bb, 1)[0];
    }

    private byte[] nextBytes(ByteBuffer bb, int length){
        byte[] nextBytes = new byte[length];
        this.bytes.limit(bb.position() + length)
                .mark()
                .get(nextBytes);
        return nextBytes;
    }

    private ByteBuffer increaseNextLimit(int step){
        return this.bytes
                .limit(this.bytes.position() + step)
                .mark();
    }

    private String deserializeProtocolSignature(){
        return new String(nextBytes(this.bytes, HasdSerializableImpl.SERIALIZATION_PROTOCOL_NAME.getBytes().length));
    }

    private List<Byte> deserializeProtocolVersion(){
        List<Byte> res = new ArrayList<>();
        for (byte b : nextBytes(this.bytes, 3)){
            res.add(b);
        }
        return res;
    }

    private Integer nextNearestValueLength(){
        Integer length = (Integer) Varint.decodeToInt(bytes.array(), bytes.position())._1();
        Integer lengthBytesSize = Varint.extractLength(bytes.array(), bytes.position());
        this.bytes.position(this.bytes.position() + lengthBytesSize)
                .mark();
        return length;
    }

    private List<Byte> deserializeNullFieldPositions(){
        Integer nullFieldsLength = nextNearestValueLength();
        List<Byte> res = new ArrayList<>();
        for (byte b : nextBytes(this.bytes, nullFieldsLength)){
            res.add(b);
        }
        return res;
    }

    private String deserializeNextString(){
        Integer stringLength = nextNearestValueLength();
        return new String(nextBytes(this.bytes, stringLength));
    }

    private <T> List<T> deserializeNextList(Class<T> itemClass) {
        Integer listLength = nextNearestValueLength();
        //List<T> deserializedList = new
        return null;
    }

    private Map<?, ?> deserializeNextMap(ByteBuffer bb) {
        return null;
    }

    private HasdSerializable deserializeNextHasdSerializable(ByteBuffer bb) {
        return null;
    }

    private Object deserializeNextValue(Class<?> fieldType, Field field, Object o) throws IllegalAccessException {
        if (fieldType.equals(boolean.class) || fieldType.equals(Boolean.class)) {
            return nextByte(this.bytes) != 0;
        } else if (fieldType.equals(byte.class) || fieldType.equals(Byte.class)) {
            return nextByte(this.bytes);
        } else if (fieldType.equals(short.class) || fieldType.equals(Short.class)) {
            return increaseNextLimit(Short.BYTES).getShort();
        } else if (fieldType.equals(int.class) || fieldType.equals(Integer.class)) {
            return increaseNextLimit(Integer.BYTES).getInt();
        } else if (fieldType.equals(long.class) || fieldType.equals(Long.class)) {
            return increaseNextLimit(Long.BYTES).getLong();
        } else if (fieldType.equals(float.class) || fieldType.equals(Float.class)) {
            return increaseNextLimit(Float.BYTES).getFloat();
        } else if (fieldType.equals(double.class) || fieldType.equals(Double.class)) {
            return increaseNextLimit(Double.BYTES).getDouble();
        } else if (fieldType.equals(char.class) || fieldType.equals(Character.class)) {
            return increaseNextLimit(Character.BYTES).getChar();
        } else if (fieldType.equals(String.class)) {
            return deserializeNextString();
        //} else if (List.class.isAssignableFrom(fieldType)) {
        //    List val = ((List) field.get(o)).;
        //    return deserializeNextList();
        //} else if (Map.class.isAssignableFrom(fieldType) || HashMap.class.isAssignableFrom(fieldType)) {
        //    return deserializeMap(buffer);
        //} else if (HasdSerializable.class.isAssignableFrom(fieldType)) {
        //    return deserializeHasdSerializable(buffer);
        } else {
            throw new IllegalArgumentException("Unsupported field type: " + fieldType.getName());
        }
    }
}
