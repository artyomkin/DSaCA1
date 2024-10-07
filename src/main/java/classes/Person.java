package classes;

import com.github.fluency03.varint.Varint;
import exceptions.CannotSerializeFieldException;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class Person implements HasdSerializable {
    private String name;
    private String lastName;
    private int age;
    private Integer height;
    private short shortVal;
    private byte byteVal;
    private long longVal;
    private double doubleVal;
    private float floatVal;
    private boolean booleanVal;
    private char charVal;
    private HasdSerializable childPerson;

    private static String SERIALIZATION_PROTOCOL_NAME = "TEAA";
    private static int SERIALIZATION_PROTOCOL_MAJOR_VERSION = 0;
    private static int SERIALIZATION_PROTOCOL_MINOR_VERSION = 0;
    private static int SERIALIZATION_PROTOCOL_PATCH_VERSION = 1;
    private static byte[] serializationProtocolName = SERIALIZATION_PROTOCOL_NAME.getBytes();
    private static byte[] serializationMajorVersion = Varint.encodeInt(SERIALIZATION_PROTOCOL_MAJOR_VERSION);
    private static byte[] serializationMinorVersion = Varint.encodeInt(SERIALIZATION_PROTOCOL_MINOR_VERSION);
    private static byte[] serializationPatchVersion = Varint.encodeInt(SERIALIZATION_PROTOCOL_PATCH_VERSION);
    private static List<byte[]> defaultHeaders = List.of(
            serializationProtocolName,
            serializationMajorVersion,
            serializationMinorVersion,
            serializationPatchVersion
    );

    public Person(Builder builder) {
        this.name = builder.name;
        this.lastName = builder.lastName;
        this.age = builder.age;
        this.height = builder.height;
        this.shortVal = builder.shortVal;
        this.byteVal = builder.byteVal;
        this.longVal = builder.longVal;
        this.doubleVal = builder.doubleVal;
        this.floatVal = builder.floatVal;
        this.booleanVal = builder.booleanVal;
        this.charVal = builder.charVal;
        this.childPerson = builder.childPerson;
    }

    public String getName() {
        return name;
    }

    public String getLastName() {
        return lastName;
    }

    public int getAge() {
        return age;
    }

    public Integer getHeight() {
        return height;
    }

    private byte[] initFileHeaderToBytes() {
        int fullHeaderLength = defaultHeaders.stream()
                .map(header -> header.length)
                .reduce(0, Integer::sum);
        return new byte[fullHeaderLength];
    }

    private byte[] fileHeaderToBytes() {
        byte[] fileHeader = initFileHeaderToBytes();

        int bytePointer = 0;
        for (byte[] header : defaultHeaders){
            System.arraycopy(header, 0, fileHeader, bytePointer, header.length);
            bytePointer += header.length;
        }
        return fileHeader;
    }

    private byte[] shortToBytes(short number){
        return ByteBuffer.allocate(Short.BYTES).putShort(number).array();
    }

    private byte[] intToBytes(int number){
        return ByteBuffer.allocate(Integer.BYTES).putInt(number).array();
    }

    private byte[] longToBytes(long number) {
        return ByteBuffer.allocate(Long.BYTES).putLong(number).array();
    }

    private byte[] floatToBytes(float number) {
        return ByteBuffer.allocate(Float.BYTES).putFloat(number).array();
    }

    private byte[] doubleToBytes(double number) {
        return ByteBuffer.allocate(Double.BYTES).putDouble(number).array();
    }

    private byte booleanToBytes(boolean bool) {
        return (byte) (bool ? 1 : 0);
    }

    private byte[] charToBytes(char c) {
        return ByteBuffer.allocate(Character.BYTES).putChar(c).array();
    }

    private byte[] insertLength(byte[] bytes){
        byte[] lengthValue = Varint.encodeInt(bytes.length);
        byte[] result = new byte[lengthValue.length + bytes.length];
        System.arraycopy(lengthValue, 0, result, 0, lengthValue.length);
        System.arraycopy(bytes, 0, result, lengthValue.length, bytes.length);
        return result;
    }

    private byte[] hasdSerializableToBytes(HasdSerializable hasdSerializable){
        return insertLength(hasdSerializable.serializeWithoutHeaders());
    }

    private byte[] stringToBytes(String str) {
        return insertLength(str.getBytes());
    }

    private byte[] serializeField(Class fieldType, Object fieldValue) throws CannotSerializeFieldException{
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
        } else if (fieldType.equals(HasdSerializable.class)) {
            return hasdSerializableToBytes((HasdSerializable) fieldValue);
        } else {
            throw new CannotSerializeFieldException("Cannot serialize field with type " + fieldType.getName());
        }
    }

    private int getTotalFieldsLength(List<byte[]> serializedFields) {
        return serializedFields.stream()
                .map(fieldBytes -> fieldBytes.length)
                .reduce(Integer::sum).get();
    }

    private byte[] flattenSerializedFields(List<byte[]> serializedFields) {
        byte[] serializedObject = new byte[getTotalFieldsLength(serializedFields)];
        int copyPosition = 0;
        for (byte[] serializedField : serializedFields) {
            System.arraycopy(serializedField, 0, serializedObject, copyPosition, serializedField.length);
            copyPosition += serializedField.length;
        }
        return serializedObject;
    }

    @Override
    public byte[] serializeWithoutHeaders() {
        Field[] fields = this.getClass().getDeclaredFields();
        List<byte[]> serializedFields = new ArrayList<>();
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers()) || Modifier.isTransient(field.getModifiers())) {
                continue;
            }
            field.setAccessible(true);
            Class fieldType = field.getType();
            try {
                Object fieldValue = field.get(this);
                if (fieldValue != null){
                    serializedFields.add(serializeField(fieldType, fieldValue));
                }
            } catch (IllegalAccessException | CannotSerializeFieldException e) {
                System.out.println(e.getMessage());
            }
        }
        return flattenSerializedFields(serializedFields);
    }

    @Override
    public byte[] serialize() {
        byte[] fileHeader = fileHeaderToBytes();
        byte[] body = serializeWithoutHeaders();
        byte[] result = new byte[fileHeader.length + body.length];
        System.arraycopy(fileHeader, 0, result, 0, fileHeader.length);
        System.arraycopy(body, 0, result, fileHeader.length, body.length);
        return result;
    }

    @Override
    public void writeToFile(String path) throws IOException {
        FileOutputStream fos = new FileOutputStream(path);
        fos.write(this.serialize());
    }

    @Override
    public String toString(){
        String report = String.format("person:\n" +
                "name: %s;\n" +
                "lastName: %s;\n" +
                "age: %d;\n" +
                "height: %d;\n",
                this.name,
                this.lastName,
                this.age,
                this.height);
        return report;
    }

    public static class Builder {
        private String name;
        private String lastName;
        private int age;
        private Integer height;
        private short shortVal;
        private byte byteVal;
        private long longVal;
        private double doubleVal;
        private float floatVal;
        private boolean booleanVal;
        private char charVal;
        private HasdSerializable childPerson;

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder setAge(int age) {
            this.age = age;
            return this;
        }

        public Builder setHeight(Integer height) {
            this.height = height;
            return this;
        }

        public Builder setShortVal(short shortVal) {
            this.shortVal = shortVal;
            return this;
        }

        public Builder setByteVal(byte byteVal) {
            this.byteVal = byteVal;
            return this;
        }

        public Builder setLongVal(long longVal) {
            this.longVal = longVal;
            return this;
        }

        public Builder setDoubleVal(double doubleVal) {
            this.doubleVal = doubleVal;
            return this;
        }

        public Builder setFloatVal(float floatVal) {
            this.floatVal = floatVal;
            return this;
        }

        public Builder setBooleanVal(boolean booleanVal) {
            this.booleanVal = booleanVal;
            return this;
        }

        public Builder setCharVal(char charVal) {
            this.charVal = charVal;
            return this;
        }

        public Builder setChildPerson(HasdSerializable childPerson){
            this.childPerson = childPerson;
            return this;
        }

        public Person build() {
            return new Person(this);
        }

        public Person deserialize(byte[] serializedPerson){
            // Здесь должен генерироваться код десериализации объекта из байтов
            return null;
        }

        public Person readFromFile(String path) throws IOException {
            FileInputStream fis = new FileInputStream(path);
            Person person = deserialize(fis.readAllBytes());
            fis.close();
            return person;
        }

    }
}
