package classes;

import java.nio.ByteBuffer;
import java.util.List;

public class OrdinarySerializerImpl implements OrdinarySerializer {
    @Override
    public byte[] toBytes(boolean b){
        return new byte[]{(byte) (b ? 1 : 0)};
    }
    @Override
    public byte[] toBytes(short val){
        return ByteBuffer.allocate(Short.BYTES).putShort(val).array();
    }
    @Override
    public byte[] toBytes(int val) {
        return ByteBuffer.allocate(Integer.BYTES).putInt(val).array();
    }
    @Override
    public byte[] toBytes(long val) {
        return ByteBuffer.allocate(Long.BYTES).putLong(val).array();
    }
    @Override
    public byte[] toBytes(double val) {
        return ByteBuffer.allocate(Double.BYTES).putDouble(val).array();
    }
    @Override
    public byte[] toBytes(float val) {
        return ByteBuffer.allocate(Float.BYTES).putFloat(val).array();
    }
    @Override
    public byte[] toBytes(char val) {
        return ByteBuffer.allocate(Character.BYTES).putChar(val).array();
    }
    @Override
    public byte[] toBytes(byte val) {
        return new byte[] { val };
    }
}
