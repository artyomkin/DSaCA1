package classes;

import com.github.fluency03.varint.Varint;

import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.util.*;

public class DeserializerImpl implements Deserializer {
    private static final int PROTOCOL_SIGNATURE_OFFSET = 0;
    private static final int PROCOTOL_VERSION_OFFSET = PROTOCOL_SIGNATURE_OFFSET + 4;
    private static final int NULL_FIELDS_OFFSET = PROCOTOL_VERSION_OFFSET + 3;

    private ByteBuffer bytes;
    private String protocolSignature;
    private List<Byte> protocolVersion;
    private List<Byte> nullFields;

    public DeserializerImpl(byte[] bytes){
        this.bytes = ByteBuffer.wrap(bytes);
        this.protocolSignature = nextProtocolSignature();
        this.protocolVersion = nextProtocolVersion();
        this.nullFields = nextNullFieldPositions();
    }

    @Override
    public List<Byte> getNullFields(){
        return this.nullFields;
    }

    @Override
    public List<Byte> getProtocolVersion(){
        return this.protocolVersion;
    }

    @Override
    public String getProtocolSignature(){
        return this.protocolSignature;
    }

    private byte nextByte(){
        return nextBytes(1)[0];
    }

    private byte[] nextBytes(int length){
        byte[] nextBytes = new byte[length];
        this.bytes.limit(this.bytes.position() + length)
                .mark()
                .get(nextBytes);
        return nextBytes;
    }

    private ByteBuffer increaseNextLimit(int step){
        return this.bytes
                .limit(this.bytes.position() + step)
                .mark();
    }

    private String nextProtocolSignature(){
        return new String(nextBytes(HasdSerializableImpl.SERIALIZATION_PROTOCOL_NAME.getBytes().length));
    }

    private List<Byte> nextProtocolVersion(){
        List<Byte> res = new ArrayList<>();
        for (byte b : nextBytes(3)){
            res.add(b);
        }
        return res;
    }

    private Integer nextVarint(){
        Integer length = (Integer) Varint.decodeToInt(bytes.array(), bytes.position())._1();
        Integer lengthBytesSize = Varint.extractLength(bytes.array(), bytes.position());
        increaseNextLimit(lengthBytesSize);
        this.bytes.position(this.bytes.position() + lengthBytesSize)
                .mark();
        return length;
    }

    private List<Byte> nextNullFieldPositions(){
        Integer nullFieldsLength = nextVarint();
        List<Byte> res = new ArrayList<>();
        for (byte b : nextBytes(nullFieldsLength)){
            res.add(b);
        }
        return res;
    }

    private String nextString(){
        Integer stringLength = nextVarint();
        return new String(nextBytes(stringLength));
    }

    private List<?> nextList() throws ClassNotFoundException, IllegalAccessException {
        String elementClassName = nextString();
        Integer elementCount = nextVarint();

        Class<?> elementClass = Class.forName(elementClassName);

        ArrayList<Object> resultList = new ArrayList<>();
        for (int i = 0; i < elementCount; i++) {
            resultList.add(nextValue(elementClass));
        }
        return resultList;
    }

    private Map<?, ?> nextMap() throws ClassNotFoundException, IllegalAccessException {
        String keyClassName = nextString();
        String valClassName = nextString();
        Integer pairsCount = nextVarint();

        Class<?> keyClass = Class.forName(keyClassName);
        Class<?> valClass = Class.forName(valClassName);

        HashMap<Object, Object> resultMap = new HashMap<>();
        for (int i = 0; i < pairsCount; i++){
            resultMap.put(nextValue(keyClass), nextValue(valClass));
        }
        return resultMap;
    }

    private HasdSerializable nextHasdSerializable() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Integer length = nextVarint();
        increaseNextLimit(length);
        byte[] embeddedHasdSerializableArr = new byte[length];
        this.bytes.get(embeddedHasdSerializableArr);
        return HasdSerializable.deserialize(embeddedHasdSerializableArr, Person.class);
    }

    @Override
    public Object nextValue(Class<?> fieldType) throws IllegalAccessException, ClassNotFoundException {
        if (fieldType.isAssignableFrom(Boolean.class)) {
            return nextByte() != 0;
        } else if (fieldType.isAssignableFrom(Byte.class)) {
            return nextByte();
        } else if (fieldType.isAssignableFrom(Short.class)) {
            return increaseNextLimit(Short.BYTES).getShort();
        } else if (fieldType.isAssignableFrom(Integer.class)) {
            return increaseNextLimit(Integer.BYTES).getInt();
        } else if (fieldType.isAssignableFrom(Long.class)) {
            return increaseNextLimit(Long.BYTES).getLong();
        } else if (fieldType.isAssignableFrom(Float.class)) {
            return increaseNextLimit(Float.BYTES).getFloat();
        } else if (fieldType.isAssignableFrom(Double.class)) {
            return increaseNextLimit(Double.BYTES).getDouble();
        } else if (fieldType.isAssignableFrom(Character.class)) {
            return increaseNextLimit(Character.BYTES).getChar();
        } else if (fieldType.equals(String.class)) {
            return nextString();
        } else if (fieldType.equals(ArrayList.class)) {
            return nextList();
        } else if (Map.class.isAssignableFrom(fieldType) || HashMap.class.isAssignableFrom(fieldType)) {
            return nextMap();
        } else if (HasdSerializable.class.isAssignableFrom(fieldType)) {
            try {
                return nextHasdSerializable();
            } catch (InvocationTargetException | NoSuchMethodException | InstantiationException e) {
                System.out.println(e.getMessage());
                return null;
            }
        } else {
            throw new IllegalArgumentException("Unsupported field type: " + fieldType.getName());
        }
    }
}
