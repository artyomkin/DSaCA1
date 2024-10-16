package classes;

import java.nio.ByteBuffer;
import java.util.List;

public interface OrdinarySerializer {
    byte[] toBytes(boolean b);
    byte[] toBytes(short val);
    byte[] toBytes(int val);
    byte[] toBytes(long val);
    byte[] toBytes(double val);
    byte[] toBytes(float val);
    byte[] toBytes(char val);
    byte[] toBytes(byte val);
}
