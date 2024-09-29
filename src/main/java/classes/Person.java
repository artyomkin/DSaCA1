package classes;

import com.github.fluency03.varint.Varint;
import scala.util.control.Exception;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class Person implements HasdSerializable {
    private String name;
    private String lastName;
    private int age;
    private Integer height;

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

    private Person(Builder builder){
        this.name = builder.name;
        this.lastName = builder.lastName;
        this.age = builder.age;
        this.height = builder.height;
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
        return ByteBuffer.allocate(2).putInt(number).array();
    }

    private byte[] intToBytes(int number){
        return ByteBuffer.allocate(4).putInt(number).array();
    }

    private byte[] longToBytes(long number) {
        return ByteBuffer.allocate(8).putLong(number).array();
    }

    private byte[] floatToBytes(float number) {
        return ByteBuffer.allocate(4).putFloat(number).array();
    }

    private byte[] doubleToBytes(double number) {
        return ByteBuffer.allocate(8).putDouble(number).array();
    }

    private byte[] stringToBytes(String str) {
        int len = str.getBytes().length;
        byte[] strLength = Varint.encodeInt(len);
        byte[] result = new byte[strLength.length + len];
        System.arraycopy(strLength, 0, result, 0, strLength.length);
        System.arraycopy(str.getBytes(), 0, result, strLength.length, len);
        return result;
    }

    private byte booleanToBytes(boolean bool) {
        return (byte) (bool ? 1 : 0);
    }

    private byte[] charToBytes(char c) {
        return ByteBuffer.allocate(2).putChar(c).array();
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
    public byte[] serializeWithoutHeaders() {
        // здесь должен генерироваться код
        // для каждого поля вызывается свой метод из перечисленных выше, если поле - это примитив
        // если поле - объект HasdSerializable - для него должен вызываться serializeWithoutHeaders
        // каждый генерируемый класс должен реализовывать интерфейс HasdSerializable
        // если поле не принадлежит интерфейсу HasdSerializable, оно не должно сериализовываться
        // если поле - это массив - пока этот случай не рассмотрен, это остается в TODO.
        return new byte[0];
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

        public Person build() {
            return new Person(this);
        }

    }
}
