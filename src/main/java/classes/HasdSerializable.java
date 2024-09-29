package classes;

public interface HasdSerializable {
    byte[] serialize();
    byte[] serializeWithoutHeaders();
}
