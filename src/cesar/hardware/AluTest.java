package cesar.hardware;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import cesar.utils.Shorts;

class AluTest {
    private final Cpu cpu = new Cpu();
    private Alu alu;

    @BeforeEach
    void initAlu() {
        alu = cpu.alu;
    }

    @Test
    void testNegative() {
        short value = (short) 0xFFFF;
        assertTrue(alu.isNegative(value));

        value = (short) 0x7FFF;
        assertFalse(alu.isNegative(value));

        value = -1;
        assertTrue(alu.isNegative(value));

        value = 0;
        assertFalse(alu.isNegative(value));
    }

    @Test
    void testZero() {
        short value = 0;
        assertTrue(alu.isZero(value));
        value = -1;
        assertFalse(alu.isZero(value));
        value = 1;
        assertFalse(alu.isZero(value));
    }

    @Test
    void testCarry() {
        short a = (short) 0xFFFF;
        short b = (short) 0x1;
        assertTrue(alu.isCarry(a, b, Alu.Operation.Plus));

        a = 0;
        b = 1;
        assertTrue(alu.isCarry(a, b, Alu.Operation.Minus));

        a = (short) 0xFFFE;
        b = (short) 0x1;
        assertFalse(alu.isCarry(a, b, Alu.Operation.Plus));

        a = 1;
        b = 1;
        assertFalse(alu.isCarry(a, b, Alu.Operation.Minus));
    }

    @Test
    void testOverflow() {
        short a = (short) 0x7FFF;
        short b = 1;
        short sum = (short) (a + b);
        assertEquals(0x8000, Shorts.toUnsignedInt(sum));
        short sub = (short) (a - b);
        assertEquals(0x7FFE, Shorts.toUnsignedInt(sub));
        assertTrue(alu.isOverflow(a, b, sum));
        assertFalse(alu.isOverflow(a, b, sub));

        a = 0;
        b = 1;
        sub = (short) (a - b);
        assertEquals(0xFFFF, Shorts.toUnsignedInt(sub));
        assertFalse(alu.isOverflow(a, b, sub));
        assertTrue(alu.isCarry(a, b, Alu.Operation.Minus));
    }

    @Test
    void testNeg() {
        short a = (short) 0xFFFF;
        short b = alu.oneOperandInstruction(Instruction.NEG, a);
        assertEquals(b, 1);

        a = 1;
        b = alu.oneOperandInstruction(Instruction.NEG, a);
        assertEquals(b, -1);

        a = 0;
        b = alu.oneOperandInstruction(Instruction.NEG, a);
        assertEquals(b, 0);
    }

    @Test
    void testClr() {
        ConditionRegister cond = alu.cond;
        cond.setValue((byte) 0b0000);

        short a = (short) 0b10101010_10101010;
        assertEquals(0, alu.oneOperandInstruction(Instruction.CLR, a));
        assertEquals(0, alu.oneOperandInstruction(Instruction.CLR, (short) 1));
        assertEquals(0, alu.oneOperandInstruction(Instruction.CLR, (short) -1));
        assertEquals(0, alu.oneOperandInstruction(Instruction.CLR, (short) 0xFFFF));

        assertTrue(cond.isZero());
        assertFalse(cond.isNegative());
        assertFalse(cond.isOverflow());
        assertFalse(cond.isCarry());
    }

    @Test
    void testNot() {
        ConditionRegister cond = alu.cond;
        cond.setValue((byte) 0b0000);

        assertEquals(0, alu.oneOperandInstruction(Instruction.NOT, (short) 0xFFFF));
        assertTrue(cond.isZero());
        assertFalse(cond.isNegative());
        assertFalse(cond.isOverflow());
        assertTrue(cond.isCarry());

        assertEquals((short) 0xFFFF, alu.oneOperandInstruction(Instruction.NOT, (short) 0));
        assertFalse(cond.isZero());
        assertTrue(cond.isNegative());
        assertFalse(cond.isOverflow());
        assertTrue(cond.isCarry());

        short a = (short) 0b10101010_10101010;
        short b = (short) 0b01010101_01010101;
        assertEquals(a, alu.oneOperandInstruction(Instruction.NOT, b));
        assertFalse(cond.isZero());
        assertTrue(cond.isNegative());
        assertFalse(cond.isOverflow());
        assertTrue(cond.isCarry());

        assertEquals(b, alu.oneOperandInstruction(Instruction.NOT, a));
        assertFalse(cond.isZero());
        assertFalse(cond.isNegative());
        assertFalse(cond.isOverflow());
        assertTrue(cond.isCarry());
    }

}
