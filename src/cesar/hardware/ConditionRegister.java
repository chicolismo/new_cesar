package cesar.hardware;
/*
    bool is_negative(std::int16_t value) const { return value < 0; };
    bool is_zero(std::int16_t value) const { return value == 0; }
    bool is_overflow(std::int16_t a, std::int16_t b, std::int16_t c) const {
        return ((a > 0) && (b > 0) && (c < 0)) || ((a < 0) && (b < 0) && (c > 0));
    }
    bool is_carry(std::int16_t a, std::int16_t b, CarryOperation operation) const {
        const auto ua = static_cast<std::uint16_t>(a);
        const auto ub = static_cast<std::uint16_t>(b);
        switch (operation) {
        case Plus: {
            uint32_t result = ua + ub;
            return (result & 0x10000u) > 0;
        }
        case Minus: {
            uint32_t result = ua - ub;
            return (result & 0x10000u) > 0;
        }
        }
    }
 */

public class ConditionRegister {
    private boolean negative;
    private boolean zero;
    private boolean overflow;
    private boolean carry;
    private byte value;

    public ConditionRegister() {
        value = (byte) 0;
        setValue(value);
    }

    public byte getValue() {
        return value;
    }

    public void setValue(byte condition) {
        setNegative((condition & 8) == 8);
        setZero((condition & 4) == 4);
        setOverflow((condition & 2) == 2);
        setCarry((condition & 1) == 1);
    }

    public void setNegative(boolean value) {
        negative = value;
    }

    public boolean isNegative() {
        return negative;
    }

    public void setZero(boolean value) {
        zero = value;
    }

    public boolean isZero() {
        return zero;
    }

    public void setOverflow(boolean value) {
        overflow = value;
    }

    public boolean isOverflow() {
        return overflow;
    }

    public void setCarry(boolean value) {
        carry = value;
    }

    public boolean isCarry() {
        return carry;
    }
}