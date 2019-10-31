package cesar.utils;

public class Shorts {
    private Shorts() {
    }

    public static short fromBytes(byte msb, byte lsb) {
        return (short) (0xFFFF & (msb << 8 | lsb));
    }

    public static int toUnsignedInt(short s) {
        return s & 0xffff;
    }
}
