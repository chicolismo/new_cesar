package cesar.hardware;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ConditionRegisterTest {
    ConditionRegister cond;

    @BeforeEach
    void initConditionRegister() {
        cond = new ConditionRegister();
    }

    @Test
    void testInitialState() {
        assertFalse(cond.isNegative());
        assertFalse(cond.isZero());
        assertFalse(cond.isOverflow());
        assertFalse(cond.isCarry());
    }

    @Test
    void testSetValue() {
        cond.setValue((byte) 0b1010); // N = 1, Z = 0, V = 1, C = 0
        assertTrue(cond.isNegative());
        assertFalse(cond.isZero());
        assertTrue(cond.isOverflow());
        assertFalse(cond.isCarry());

        cond.setValue((byte) 0b0110); // N = 0, Z = 1, V = 1, C = 0
        assertFalse(cond.isNegative());
        assertTrue(cond.isZero());
        assertTrue(cond.isOverflow());
        assertFalse(cond.isCarry());

        cond.setValue((byte) 0b0001); // N = 0, Z = 0, V = 0, C = 1
        assertFalse(cond.isNegative());
        assertFalse(cond.isZero());
        assertFalse(cond.isOverflow());
        assertTrue(cond.isCarry());
    }
}
