package classes;

import com.github.fluency03.varint.Varint;
import exceptions.CannotSerializeFieldException;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class HasdSerializableImpl implements HasdSerializable {

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

    private List<Field> getAllFields() {
        return Stream.of(this.getClass().getDeclaredFields())
                .filter(field -> !Modifier.isStatic(field.getModifiers()) && !Modifier.isTransient(field.getModifiers()))
                .peek(field -> field.setAccessible(true))
                .toList();
    }

    private List<Integer> getNullFields(List<Field> fields) {
        return IntStream.range(0, fields.size())
                .filter(i -> {
                    try {
                        return fields.get(i).get(this) == null;
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                })
                .boxed()
                .toList();
    }

    private List<Field> getValuableFields(List<Field> fields) {
        return fields.stream()
                .filter(field -> {
                    try {
                        return field.get(this) != null;
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();
    }

    public byte[] serializeBody() {
        List<Field> allFields = getAllFields();
        List<Integer> nullFieldsIndexes = getNullFields(allFields);
        List<Field> fields = getValuableFields(allFields);

        List<byte[]> serializedFields = new ArrayList<>(fields.stream()
                .map(field -> {
                    try {
                        return Serializer.serializeObject(field.get(this));
                    } catch (IllegalAccessException | CannotSerializeFieldException e) {
                        throw new RuntimeException();
                    }
                })
                .toList());

        byte[] nullFields = new byte[nullFieldsIndexes.size()];
        for (int i = 0; i < nullFieldsIndexes.size(); i++){
            nullFields[i] = nullFieldsIndexes.get(i).byteValue();
        }
        serializedFields.add(0, Serializer.insertLength(nullFields));
        return Serializer.flattenBytes(serializedFields);
    }

    @Override
    public byte[] serialize() {
        byte[] fileHeader = Serializer.flattenBytes(defaultHeaders);
        byte[] body = serializeBody();
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

    public HasdSerializable deserialize(byte[] serializedObject){
        List<Field> allFields = getAllFields();
        int pointer = 0;
        //while (pointer < serializedObject.length) {
        //
        //}
        return null;
    }

    @Override
    public HasdSerializable readFromFile(String path) throws IOException {
        return null;
    }
}
