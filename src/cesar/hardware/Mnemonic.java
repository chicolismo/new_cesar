package cesar.hardware;

import cesar.utils.Bytes;
import cesar.utils.Shorts;

public class Mnemonic {
    public int size;
    public String value;

    static String conditionToString(final byte opcode) {
        final int lsb = opcode & 0x0F;
        final String n = (lsb & 0b1000) > 0 ? "N" : "";
        final String z = (lsb & 0b0100) > 0 ? "Z" : "";
        final String o = (lsb & 0b0010) > 0 ? "V" : "";
        final String v = (lsb & 0b0001) > 0 ? "C" : "";
        return n + z + o + v;
    }

    public static int updateMnemonics(final byte[] memory, final String[] mnemonics, final int startAt) {
        return updateMnemonics(memory, mnemonics, startAt, false);
    }

    public static int updateMnemonics(final byte[] memory, final String[] mnemonics, final int startAt,
        boolean refreshAll) {
        int row = startAt;

        /*
         * Só avança no (R7)+ ou nos casos de ddd(Rx) ou (ddd(Rx))
         */
        while (row < Cpu.MEMORY_SIZE) {
            // Termina quando chegar no final ou quando o mnemônico produzido para um
            // determinado índice for igual ao do arranjo de mnemônicos.
            byte opcode = memory[0xFFFF & row];
            String mnemonic;
            int increment = 1;
            Instruction instruction = Instruction.getInstruction(opcode);
            String format = instruction.getFormatString();

            // Se a linha for vazia, é sinal que se trada de um operando para outra
            // instrução, e portanto não precisa ser rotulada.
            if (mnemonics[row] != null && mnemonics[row].equals("")) {
                break;
            }

            switch (instruction) {
                case NOP:
                case HLT:
                    mnemonic = format;
                    break;

                case CCC:
                case SCC: {
                    mnemonic = String.format(format, conditionToString(opcode));
                    break;
                }

                case BR:
                case BNE:
                case BEQ:
                case BPL:
                case BMI:
                case BVC:
                case BVS:
                case BCC:
                case BCS:
                case BGE:
                case BLT:
                case BGT:
                case BLE:
                case BHI:
                case BLS: {
                    byte nextByte = memory[0xFFFF & (row + increment++)];
                    mnemonic = String.format(format, Bytes.toUnsignedInt(nextByte));
                    break;
                }

                case JMP: {
                    final byte nextByte = memory[0xFFFF & (row + increment++)];
                    final int mmm = (nextByte & 0b00111000) >> 3;
                    final int rrr = (nextByte & 0b00000111);
                    final AddressMode addressMode = AddressMode.fromInt(mmm);
                    if (addressMode.isIndexed()) {
                        final byte msb = memory[0xFFFF & (row + increment++)];
                        final byte lsb = memory[0xFFFF & (row + increment++)];
                        final int ddd = 0xFFFF & Shorts.fromBytes(msb, lsb);
                        mnemonic = String.format(format, addressMode.toString(ddd, rrr));
                    }
                    else {
                        mnemonic = String.format(format, addressMode.toString(rrr));
                    }
                    break;
                }
                case SOB: {
                    final int register = (opcode & 0b0000_0111);
                    final int ddd = Bytes.toUnsignedInt(memory[0xFFFF & (row + increment++)]);
                    mnemonic = String.format(format, register, ddd);
                    break;
                }
                case JSR: {
                    final int register = opcode & 0b0000_0111;
                    final byte nextByte = memory[0xFFFF & (row + increment++)];
                    final int mmm = (nextByte & 0b00111000) >> 3;
                    final int rrr = (nextByte & 0b00000111);
                    final AddressMode addressMode = AddressMode.fromInt(mmm);
                    if (addressMode.isIndexed()) {
                        final byte msb = memory[0xFFFF & (row + increment++)];
                        final byte lsb = memory[0xFFFF & (row + increment++)];
                        final int ddd = 0xFFFF & Shorts.fromBytes(msb, lsb);
                        mnemonic = String.format(format, register, addressMode.toString(ddd, rrr));
                    }
                    else {
                        mnemonic = String.format(format, register, addressMode.toString(rrr));
                    }

                    if (addressMode.isPostIncremented() && rrr == Cpu.PC) {
                        increment += 2;
                    }
                    break;
                }

                case RTS: {
                    final int register = (opcode & 0b0000_0111);
                    mnemonic = String.format(format, register);
                    break;
                }

                case CLR:
                case NOT:
                case INC:
                case DEC:
                case NEG:
                case TST:
                case ROR:
                case ROL:
                case ASR:
                case ASL:
                case ADC:
                case SBC: {
                    final byte nextByte = memory[0xFFFF & (row + increment++)];
                    final int mmm = (nextByte & 0b00111000) >> 3;
                    final int rrr = (nextByte & 0b00000111);
                    final AddressMode addressMode = AddressMode.fromInt(mmm);
                    if (addressMode.isIndexed()) {
                        final byte msb = memory[0xFFFF & (row + increment++)];
                        final byte lsb = memory[0xFFFF & (row + increment++)];
                        final int ddd = 0xFFFF & Shorts.fromBytes(msb, lsb);
                        mnemonic = String.format(format, addressMode.toString(ddd, rrr));
                    }
                    else {
                        mnemonic = String.format(format, addressMode.toString(rrr));
                    }

                    if (addressMode.isPostIncremented() && rrr == Cpu.PC) {
                        increment += 2;
                    }

                    break;
                }

                case MOV:
                case ADD:
                case SUB:
                case CMP:
                case AND:
                case OR: {
                    final byte nextByte = memory[0xFFFF & (row + increment++)];
                    final int word = 0xFFFF & Shorts.fromBytes(opcode, nextByte);
                    final int mmm_1 = (word & 0b0000_1110_0000_0000) >> 9;
                    final int rrr_1 = (word & 0b0000_0001_1100_0000) >> 6;
                    final int mmm_2 = (word & 0b0000_0000_0011_1000) >> 3;
                    final int rrr_2 = (word & 0b0000_0000_0000_0111);

                    final AddressMode srcMode = AddressMode.fromInt(mmm_1);

                    String srcString;
                    if (srcMode.isIndexed()) {
                        final byte msb = memory[0xFFFF & (row + increment++)];
                        final byte lsb = memory[0xFFFF & (row + increment++)];
                        final int ddd = 0xFFFF & Shorts.fromBytes(msb, lsb);
                        srcString = srcMode.toString(ddd, rrr_1);
                    }
                    else {
                        srcString = srcMode.toString(rrr_1);
                    }

                    if (srcMode.isPostIncremented() && rrr_1 == Cpu.PC) {
                        increment += 2;
                    }

                    final AddressMode dstMode = AddressMode.fromInt(mmm_2);
                    String dstString;
                    if (dstMode.isIndexed()) {
                        final byte msb = memory[0xFFFF & (row + increment++)];
                        final byte lsb = memory[0xFFFF & (row + increment++)];
                        final int ddd = 0xFFFF & Shorts.fromBytes(msb, lsb);
                        dstString = dstMode.toString(ddd, rrr_2);
                    }
                    else {
                        dstString = dstMode.toString(rrr_2);
                    }

                    if (dstMode.isPostIncremented() && rrr_2 == Cpu.PC) {
                        increment += 2;
                    }

                    mnemonic = String.format(format, srcString, dstString);
                    break;
                }

                default:
                    mnemonic = Instruction.NOP.getFormatString();
            }

            if (refreshAll) {
                mnemonics[row] = mnemonic;
                for (int j = 1; j < increment; ++j) {
                    mnemonics[0xFFFF & (row + j)] = "";
                }
            }
            else {
                if (mnemonics[row] == null || !mnemonics[row].equals(mnemonic)) {
                    mnemonics[row] = mnemonic;
                    for (int j = 1; j < increment; ++j) {
                        mnemonics[0xFFFF & (row + j)] = "";
                    }
                }
                else {
                    break;
                }
            }
            row += increment;
        }
        return row;
    }
}
