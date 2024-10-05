package classes;

import java.io.IOException;

public interface HasdSerializable {
    byte[] serialize();
    byte[] serializeWithoutHeaders();
    void writeToFile(String path) throws IOException;
}
