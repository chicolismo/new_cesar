package cesar.hardware;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import cesar.utils.Shorts;

public class Cpu {
    public static final int MEMORY_SIZE = 1 << 16;
    public static final int BEGIN_DISPLAY_ADDRESS = 65500;
    public static final int END_DISPLAY_ADDRESS = 65535;

    private static final int PC = 7;


    short[] registers;
    byte[] memory;
    Alu alu;
    ConditionRegister conditionRegister;
    boolean isHalted;

    public Cpu() {
        registers = new short[8];
        memory = new byte[MEMORY_SIZE];
        conditionRegister = new ConditionRegister();
        alu = new Alu(this);
        isHalted = true;
    }

    public byte[] getMemory() {
        return memory;
    }

    public void setMemory(final byte[] bytes) {
        final int offset = bytes.length > MEMORY_SIZE ? bytes.length - MEMORY_SIZE : 0;
        final int max = Math.max(bytes.length, MEMORY_SIZE);
        for (int i = offset; i < max; ++i) {
            memory[i - offset] = bytes[i];
        }
    }

    boolean isDisplayAddress(final short address) {
        int unsigned = Shorts.toUnsignedInt(address);
        return (unsigned >= BEGIN_DISPLAY_ADDRESS && unsigned <= END_DISPLAY_ADDRESS);
    }


    boolean isTwoOperandInstruction(final Instruction instruction) {
        return (instruction == Instruction.MOV || instruction == Instruction.ADD || instruction == Instruction.SUB
            || instruction == Instruction.CMP || instruction == Instruction.AND || instruction == Instruction.OR);
    }


    private byte fetchNextByte() {
        // TODO: Incrementar acessos de memória?
        byte nextByte = memory[registers[PC]];
        ++registers[PC];
        return nextByte;
    }

    short getWord(short address) {
        if (isDisplayAddress(address)) {
            byte lsb = memory[0xFFFF & address];
            return Shorts.fromBytes((byte) 0, lsb);
        }
        else {
            byte msb = memory[0xFFFF & address];
            byte lsb = memory[0xFFFF & (address + 1)];
            return Shorts.fromBytes(msb, lsb);
        }
    }

    void setWord(final short address, final short value) {
        byte msb = (byte) ((value & 0xFF00) >> 8);
        byte lsb = (byte) (value & 0x00FF);
        if (isDisplayAddress(address)) {
            memory[0xFFFF & address] = lsb;
        }
        else {
            memory[0xFFFF & address] = msb;
            memory[0xFFFF & (address + 1)] = lsb;
        }
    }

    short getAddress(final AddressMode mode, final int registerNumber) {
        short address = 0;

        switch (mode) {
            case Register:
                address = (short) registerNumber;
                break;

            case RegisterPostIncremented:
                address = registers[registerNumber];
                registers[registerNumber] += 2;
                break;

            case RegisterPreDecremented:
                registers[registerNumber] -= 2;
                address = registers[registerNumber];
                break;

            case Indexed: {
                final short nextWord = getWord(registers[PC]);
                registers[PC] += 2;
                address = (short) (registers[registerNumber] + nextWord);
                break;
            }

            case RegisterIndirect: {
                final short regValue = registers[registerNumber];
                address = getWord(regValue);
                break;
            }

            case PostIncrementedIndirect: {
                final short regValue = registers[registerNumber];
                registers[registerNumber] += 2;
                address = getWord(regValue);
                break;
            }

            case PreDrecrementedIndirect: {
                registers[registerNumber] -= 2;
                final short regValue = registers[registerNumber];
                address = getWord(regValue);
                break;
            }

            case IndexedIndirect: {
                final short nextWord = getWord(registers[PC]);
                registers[PC] += 2;
                final short temp = (short) (nextWord + registers[registerNumber]);
                address = getWord(temp);
                break;
            }
        }

        return address;
    }

    short getValueFromAddress(final AddressMode mode, final short address) {
        if (mode == AddressMode.Register) {
            return registers[address];
        }
        else {
            return getWord(address);
        }
    }

    void setValueToAddress(final AddressMode mode, final short address, final short value) {
        if (mode == AddressMode.Register) {
            registers[address] = value;
        }
        else {
            setWord(address, value);
        }
    }

    public void executeNextInstruction() {
        if (registers[PC] == -1) {
            isHalted = true;
            return;
        }

        byte firstByte = fetchNextByte();
        int opcode = 0x0F & (firstByte >> 4);

        Instruction instruction = Instruction.getInstruction(opcode);
        if (instruction == Instruction.NOP) {
            return;
        }

        if (instruction == Instruction.ConditionalBranch) {
            final Instruction branchInstruction = Instruction.getInstruction(firstByte);
            byte offset = fetchNextByte();
            alu.conditionalBranch(branchInstruction, offset);
        }
        else if (instruction == Instruction.OneOperandInstruction) {
            final Instruction oneOpInstruction = Instruction.getInstruction(firstByte);
            final byte nextByte = fetchNextByte();
            final int mmm = (nextByte & 0b0011_1000) >> 3;
            final int rrr = (nextByte & 0b0000_0111);
            final AddressMode mode = AddressMode.array[mmm];
            final short address = getAddress(mode, rrr);
            final short value = getValueFromAddress(mode, address);
            final short result = alu.oneOpernadInstruction(oneOpInstruction, value);
            setValueToAddress(mode, address, result);
        }
        else {
            switch (instruction) {
                case CCC:
                    alu.ccc(firstByte);
                    break;
                case SCC:
                    alu.scc(firstByte);
                    break;
                case JMP: {
                    short nextByte = fetchNextByte();
                    final int mmm = (nextByte & 0b0011_1000) >> 3;
                    final int rrr = (nextByte & 0b0000_0111);
                    final AddressMode mode = AddressMode.array[mmm];
                    final short address = getAddress(mode, rrr);
                    alu.jmp(mode, address);
                    break;
                }
                case SOB: {
                    final int registerNumber = (firstByte & 0b0000_0111);
                    final byte offset = fetchNextByte();
                    alu.sob(registerNumber, offset);
                    break;
                }
                case JSR: {
                    final byte nextByte = fetchNextByte();
                    final short word = Shorts.fromBytes(firstByte, nextByte);
                    final int reg = (word & 0b0000_0111_0000_0000) >> 8;
                    final int mmm = (word & 0b0000_0000_0011_1000) >> 3;
                    final int rrr = (word & 0b0000_0000_0000_0111);
                    final AddressMode mode = AddressMode.array[mmm];
                    final short address = getAddress(mode, rrr);
                    alu.jsr(mode, address, reg);
                    break;
                }
                case RTS:
                    alu.rts(firstByte);
                    break;
                default: {
                    if (isTwoOperandInstruction(instruction)) {
                        final byte nextByte = fetchNextByte();
                        final short word = Shorts.fromBytes(firstByte, nextByte);
                        final int mmm1 = (word & 0b0000111000000000) >> 9;
                        final int rrr1 = (word & 0b0000000111000000) >> 6;
                        final int mmm2 = (word & 0b0000000000111000) >> 3;
                        final int rrr2 = (word & 0b0000000000000111);
                        final AddressMode srcMode = AddressMode.array[mmm1];
                        final AddressMode dstMode = AddressMode.array[mmm2];
                        final short srcAddress = getAddress(srcMode, rrr1);
                        final short dstAddress = getAddress(dstMode, rrr2);
                        final short src = getValueFromAddress(srcMode, srcAddress);
                        final short dst = getValueFromAddress(dstMode, dstAddress);
                        final short value = alu.twoOperandInstruction(instruction, src, dst);
                        if (instruction != Instruction.CMP) {
                            setValueToAddress(dstMode, dstAddress, value);
                        }
                    }
                    break;
                }
            }
        }
    }

    public void run() {
        isHalted = false;
        while (!isHalted) {
            executeNextInstruction();
        }
    }

    void push(short word) {
        int unsigned = Shorts.toUnsignedInt(word);
        byte msb = (byte) (unsigned >> 8);
        byte lsb = (byte) ((unsigned & 0xFF) >> 8);
        registers[6] -= 2;
        memory[registers[6]] = msb;
        memory[registers[6] + 1] = lsb;
    }

    short pop() {
        byte msb = memory[registers[6]];
        byte lsb = memory[registers[6] + 1];
        registers[6] += 2;
        return Shorts.fromBytes(msb, lsb);
    }

    public void readBinaryFile(String filename) throws IOException {
        File file = new File(filename);
        FileInputStream fileInputStream = new FileInputStream(file);

        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream)) {
            var buffer = bufferedInputStream.readAllBytes();
            setMemory(buffer);
        }
    }
}
