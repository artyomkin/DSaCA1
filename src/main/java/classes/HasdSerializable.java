package classes;

import java.io.IOException;

public interface HasdSerializable {
    byte[] serialize();
    void writeToFile(String path) throws IOException;
    HasdSerializable deserialize(byte[] serializedObject);
    HasdSerializable readFromFile(String path) throws IOException;
}
