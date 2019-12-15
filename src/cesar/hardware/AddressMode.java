package cesar.hardware;

import java.util.HashMap;

public enum AddressMode {
    REGISTER, REGISTER_POST_INCREMENTED, REGISTER_PRE_DECREMENTED, INDEXED, REGISTER_INDIRECT, POST_INCREMENTED_INDIRECT,
    PRE_DECREMENTED_INDIRECT, INDEXED_INDIRECT;

    private static final AddressMode[] array = AddressMode.values();
    private static final HashMap<AddressMode, String> FORMAT;

    static {
        FORMAT = new HashMap<>();
        FORMAT.put(AddressMode.REGISTER, "R%d");
        FORMAT.put(AddressMode.REGISTER_POST_INCREMENTED, "(R%d)+");
        FORMAT.put(AddressMode.REGISTER_PRE_DECREMENTED, "-(R%d)");
        FORMAT.put(AddressMode.INDEXED, "%d(R%d)");
        FORMAT.put(AddressMode.REGISTER_INDIRECT, "(R%d)");
        FORMAT.put(AddressMode.POST_INCREMENTED_INDIRECT, "((R%d)+)");
        FORMAT.put(AddressMode.PRE_DECREMENTED_INDIRECT, "(-(R%d))");
        FORMAT.put(AddressMode.INDEXED_INDIRECT, "(%d(R%d))");
    }

    public static AddressMode fromInt(int index) {
        return array[index];
    }

    public String toString(final int register) {
        return String.format(FORMAT.get(this), register);
    }

    public String toString(final int ddd, final int register) {
        return String.format(FORMAT.get(this), ddd, register);
    }

    public boolean isIndexed() {
        return this == INDEXED || this == INDEXED_INDIRECT;
    }

    public boolean isPostIncremented() {
        return this == REGISTER_POST_INCREMENTED || this == POST_INCREMENTED_INDIRECT;
    }
}
