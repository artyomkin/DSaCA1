package classes;

import exceptions.CannotSerializeFieldException;

import java.util.List;

public interface Serializer {
    byte[] flattenBytes(List<byte[]> l);
    byte[] serializeObject(Object o) throws CannotSerializeFieldException;
    byte[] insertLength(byte[] b);
}
