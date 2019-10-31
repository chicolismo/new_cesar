package cesar.hardware;

public enum AddressMode {
    Register, RegisterPostIncremented, RegisterPreDecremented, Indexed, RegisterIndirect, PostIncrementedIndirect,
    PreDrecrementedIndirect, IndexedIndirect;

    static AddressMode[] array = AddressMode.values();
}
