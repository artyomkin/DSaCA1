package classes;

import com.github.fluency03.varint.Varint;
import exceptions.CannotSerializeFieldException;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

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

    @Override
    public byte[] serializeWithoutHeaders() {
        Field[] fields = this.getClass().getDeclaredFields();
        List<byte[]> serializedFields = new ArrayList<>();
        List<Integer> nullFieldsList = new ArrayList<>();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            if (Modifier.isStatic(field.getModifiers()) || Modifier.isTransient(field.getModifiers())) {
                continue;
            }
            field.setAccessible(true);
            try {
                Object fieldValue = field.get(this);
                if (fieldValue != null){
                    serializedFields.add(BytesConverter.serializeObject(fieldValue));
                } else {
                    nullFieldsList.add(i);
                }
            } catch (IllegalAccessException | CannotSerializeFieldException e) {
                System.out.println(e.getMessage());
            }
        }
        byte[] nullFields = new byte[nullFieldsList.size()];
        for (int i = 0; i < nullFieldsList.size(); i++){
            nullFields[i] = nullFieldsList.get(i).byteValue();
        }
        serializedFields.add(0, BytesConverter.insertLength(nullFields));
        return BytesConverter.flattenBytes(serializedFields);
    }

    @Override
    public byte[] serialize() {
        byte[] fileHeader = BytesConverter.flattenBytes(defaultHeaders);
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
}
