package cesar.hardware;

import cesar.utils.Shorts;

public class Alu {
    static final int PC = 7;

    Cpu cpu;
    ConditionRegister cond;
    short[] registers;

    public enum Operation {
        Plus, Minus
    };

    public Alu(Cpu cpu) {
        this.cpu = cpu;
        this.registers = cpu.getRegisters();
        cond = cpu.getConditionRegister();
    }

    void conditionalBranch(final Instruction instruction, final byte offset) {
        switch (instruction) {
            case BR:
                registers[PC] += offset;
                break;
            case BNE:
                if (!cond.isZero()) {
                    registers[PC] += offset;
                }
                break;
            case BEQ:
                if (cond.isZero()) {
                    registers[PC] += offset;
                }
                break;
            case BPL:
                if (!cond.isNegative()) {
                    registers[PC] += offset;
                }
                break;
            case BMI:
                if (cond.isNegative()) {
                    registers[PC] += offset;
                }
                break;
            case BVC:
                if (!cond.isOverflow()) {
                    registers[PC] += offset;
                }
                break;
            case BVS:
                if (cond.isOverflow()) {
                    registers[PC] += offset;
                }
                break;
            case BCC:
                if (!cond.isCarry()) {
                    registers[PC] += offset;
                }
                break;
            case BCS:
                if (cond.isCarry()) {
                    registers[PC] += offset;
                }
                break;
            case BGE:
                if (cond.isNegative() == cond.isOverflow()) {
                    registers[PC] += offset;
                }
                break;
            case BLT:
                if (cond.isNegative() != cond.isOverflow()) {
                    registers[PC] += offset;
                }
                break;
            case BGT:
                if (cond.isNegative() == cond.isOverflow() && !cond.isZero()) {
                    registers[PC] += offset;
                }
                break;
            case BLE:
                if (cond.isNegative() != cond.isOverflow() || cond.isZero()) {
                    registers[PC] += offset;
                }
                break;
            case BHI:
                if (!cond.isCarry() && !cond.isZero()) {
                    registers[PC] += offset;
                }
                break;
            case BLS:
                if (cond.isCarry() || cond.isZero()) {
                    registers[PC] += offset;
                }
                break;
            default:
                break;
        }
    }

    short oneOperandInstruction(final Instruction instruction, short value) {
        short result = 0;

        switch (instruction) {
            case CLR:
                result = 0;
                cond.setNegative(false);
                cond.setZero(true);
                cond.setOverflow(false);
                cond.setCarry(false);
                break;

            case NOT:
                result = (short) ~(0xFFFF & value);
                cond.setNegative(isNegative(result));
                cond.setZero(isZero(result));
                cond.setOverflow(false);
                cond.setCarry(true);
                break;

            case INC:
                result = (short) (value + 1);
                cond.setOverflow(isOverflow(value, (short) 1, (short) (value + 1)));
                cond.setCarry(Shorts.toUnsignedInt(value) == 0xFFFF);
                cond.setNegative(isNegative(result));
                cond.setZero(isZero(result));
                break;

            case DEC:
                result = (short) (value - 1);
                cond.setOverflow(isOverflow(value, (short) 1, (short) (value - 1)));
                cond.setCarry(value == 0);
                cond.setNegative(isNegative(result));
                cond.setZero(isZero(result));
                break;

            case NEG: {
                result = (short) -value;
                int temp = ~(value & 0xFFFF) + 1;
                cond.setOverflow(isOverflow((short) ~value, (short) 1, (short) (~value + 1)));
                cond.setCarry((temp & 0x10000) == 16);
                cond.setNegative(isNegative(result));
                cond.setZero(isZero(result));
                break;
            }

            case TST:
                result = value;
                cond.setNegative(isNegative(result));
                cond.setZero(isZero(result));
                cond.setOverflow(false);
                cond.setCarry(false);
                break;

            case ROR: {
                final int temp = Shorts.toUnsignedInt(value);
                final int lsb = temp & 1;
                result = (short) (lsb << 15 | temp >> 1);
                cond.setCarry(lsb == 1);
                cond.setNegative(isNegative(result));
                cond.setZero(isZero(result));
                cond.setOverflow(cond.isNegative() ^ cond.isCarry());
                break;
            }

            case ROL: {
                final int temp = Shorts.toUnsignedInt(value);
                final int msb = (temp & 0x8000) >> 15;
                result = (short) ((temp << 1) | msb);
                cond.setCarry(msb == 1);
                cond.setNegative(isNegative(result));
                cond.setZero(isZero(result));
                cond.setOverflow(cond.isNegative() ^ cond.isCarry());
                break;
            }

            case ASR: {
                final int temp = Shorts.toUnsignedInt(value);
                final int lsb = temp & 0x0001;
                final int msb = temp & 0x8000;
                result = (short) (msb | (temp >> 1));
                cond.setCarry(lsb == 1);
                cond.setNegative(isNegative(result));
                cond.setZero(isZero(result));
                cond.setOverflow(cond.isNegative() ^ cond.isCarry());
                break;
            }

            case ASL: {
                final int temp = Shorts.toUnsignedInt(value);
                final int msb = (temp & 0x8000) >> 15;
                result = (short) (temp << 1);
                cond.setCarry(msb == 1);
                cond.setNegative(isNegative(result));
                cond.setZero(isZero(result));
                cond.setOverflow(cond.isNegative() ^ cond.isCarry());
                break;
            }

            case ADC: {
                short c = (short) (cond.isCarry() ? 1 : 0);
                result = (short) (value + c);
                cond.setNegative(isNegative(result));
                cond.setZero(isZero(result));
                cond.setOverflow(isOverflow(value, c, (short) (value + c)));
                cond.setCarry(isCarry(value, c, Operation.Plus));
                break;
            }

            case SBC: {
                short c = (short) (cond.isCarry() ? 1 : 0);
                result = (short) (value - c);
                cond.setNegative(isNegative(result));
                cond.setZero(isZero(result));
                cond.setOverflow(isOverflow(value, c, (short) (value - c)));
                cond.setCarry(isCarry(value, c, Operation.Minus));
                break;
            }

            default:
                result = value;
                break;
        }

        return result;
    }

    short twoOperandInstruction(final Instruction instruction, final short src, final short dst) {
        short result = 0;
        switch (instruction) {
            case MOV:
                result = mov(src);
                break;
            case ADD:
                result = add(src, dst);
                break;
            case SUB:
                result = sub(src, dst);
                break;
            case CMP:
                result = cmp(src, dst);
            case AND:
                result = and(src, dst);
                break;
            case OR:
                result = or(src, dst);
                break;
            default: {
                String message = String.format("Erro em 'twoOperandInstruction', instrução: %s",
                    instruction.toString());
                System.err.println(message);
                break;
            }
        }
        return result;
    }

    void ccc(final byte newCondition) {
        final byte condition = this.cond.getValue();
        this.cond.setValue((byte) (condition & ~newCondition));
    }

    void scc(final byte newCondition) {
        final byte condition = this.cond.getValue();
        this.cond.setValue((byte) (condition | newCondition));
    }

    void jmp(final AddressMode mode, final int address) {
        if (mode != AddressMode.REGISTER) {
            registers[PC] = (short) (0xFFFF & address);
        }
    }

    void sob(final int registerNumber, final byte offset) {
        if ((--registers[registerNumber]) != 0) {
            registers[PC] -= offset;
        }
    }

    void jsr(final AddressMode mode, final int address, final int registerNumber) {
        if (mode != AddressMode.REGISTER) {
            cpu.push(registers[registerNumber]);
            registers[registerNumber] = registers[PC];
            registers[PC] = (short) (0xFFFF & address);
        }
    }

    void rts(final byte b) {
        int registerNumber = (b & 0b0111);
        registers[PC] = registers[registerNumber];
        registers[registerNumber] = cpu.pop();
    }

    short mov(final short src) {
        cond.setNegative(isNegative(src));
        cond.setZero(isZero(src));
        cond.setOverflow(false);
        return src;
    }

    short add(final short src, final short dst) {
        final short result = (short) (dst + src);
        cond.setNegative(isNegative(result));
        cond.setZero(isZero(result));
        cond.setOverflow(isOverflow(dst, src, result));
        cond.setCarry(isCarry(dst, src, Operation.Plus));
        return result;
    }

    short sub(final short src, final short dst) {
        final short result = (short) (dst - src);
        cond.setNegative(isNegative(result));
        cond.setZero(isZero(result));
        cond.setOverflow(isOverflow(dst, src, result));
        cond.setCarry(isCarry(dst, src, Operation.Minus));
        return result;
    }

    short cmp(final short src, final short dst) {
        final short result = (short) (src - dst);
        cond.setNegative(isNegative(result));
        cond.setZero(isZero(result));
        cond.setOverflow(isOverflow(src, dst, result));
        cond.setCarry(isCarry(src, dst, Operation.Minus));
        return dst;
    }

    short and(final short src, final short dst) {
        final short result = (short) (src & dst);
        cond.setNegative(isNegative(result));
        cond.setZero(isZero(result));
        cond.setOverflow(false);
        return result;
    }

    short or(final short src, final short dst) {
        final short result = (short) (src | dst);
        cond.setNegative(isNegative(result));
        cond.setZero(isZero(result));
        cond.setOverflow(false);
        return result;
    }

    boolean isNegative(short value) {
        return value < 0;
    }

    boolean isZero(short value) {
        return value == 0;
    }

    boolean isOverflow(short a, short b, short c) {
        return ((a > 0) && (b > 0) && (c < 0)) || ((a < 0) && (b < 0) && (c > 0));
    }

    private static final int CARRY_MASK = 0x1_0000;

    boolean isCarry(short a, short b, Operation op) {
        final int ua = Shorts.toUnsignedInt(a);
        final int ub = Shorts.toUnsignedInt(b);
        if (op == Operation.Plus) {
            return ((ua + ub) & CARRY_MASK) == CARRY_MASK;
        }
        else {
            return ((ua - ub) & CARRY_MASK) == CARRY_MASK;
        }
    }
}
