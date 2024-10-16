package classes;

import java.util.List;

public interface Deserializer {
    Object nextValue(Class<?> cl) throws IllegalAccessException, ClassNotFoundException;
    List<Byte> getNullFields();
    List<Byte> getProtocolVersion();
    String getProtocolSignature();
}
