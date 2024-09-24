package classes;

import com.github.fluency03.varint.Varint;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class Person {
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

    private <T> byte[] fieldToBytes(Field field, T value) {
        // do serialization
        return null;
    }

    private byte[] toBytes() {
        byte[] fileHeader = fileHeaderToBytes();
        return fileHeader;
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
