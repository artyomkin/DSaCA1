package classes;

import java.nio.ByteBuffer;

public class FromBytesConverter {

    private static short bytesToShort(byte[] bytes){
        return ByteBuffer.wrap(bytes).getShort();
    }

    private static int bytes(byte[] bytes){
        return ByteBuffer.wrap(bytes).getShort();
    }

    private static byte[] longToBytes(long number) {
        return ByteBuffer.allocate(Long.BYTES).putLong(number).array();
    }

    private static byte[] floatToBytes(float number) {
        return ByteBuffer.allocate(Float.BYTES).putFloat(number).array();
    }

    private static byte[] doubleToBytes(double number) {
        return ByteBuffer.allocate(Double.BYTES).putDouble(number).array();
    }

    private static byte booleanToBytes(boolean bool) {
        return (byte) (bool ? 1 : 0);
    }

    private static byte[] charToBytes(char c) {
        return ByteBuffer.allocate(Character.BYTES).putChar(c).array();
    }
}
