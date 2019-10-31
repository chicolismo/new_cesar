package cesar.hardware;

public enum Base {
    Decimal, Hexadecimal;

    public static int toInt(Base b) {
        return b == Decimal ? 10 : 16;
    }
}
