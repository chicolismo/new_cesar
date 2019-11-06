package cesar.hardware;

import java.util.HashMap;

public enum AddressMode {
    Register, RegisterPostIncremented, RegisterPreDecremented, Indexed, RegisterIndirect, PostIncrementedIndirect,
    PreDrecrementedIndirect, IndexedIndirect;

    private static final AddressMode[] array = AddressMode.values();
    private static final HashMap<AddressMode, String> FORMAT;

    static {
        FORMAT = new HashMap<>();
        FORMAT.put(AddressMode.Register, "R%d");
        FORMAT.put(AddressMode.RegisterPostIncremented, "(R%d)+");
        FORMAT.put(AddressMode.RegisterPreDecremented, "-(R%d)");
        FORMAT.put(AddressMode.Indexed, "%d(R%d)");
        FORMAT.put(AddressMode.RegisterIndirect, "(R%d)");
        FORMAT.put(AddressMode.PostIncrementedIndirect, "((R%d)+)");
        FORMAT.put(AddressMode.PreDrecrementedIndirect, "(-(R%d))");
        FORMAT.put(AddressMode.IndexedIndirect, "(%d(%Rd))");
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
        return this == Indexed || this == IndexedIndirect;
    }
}
