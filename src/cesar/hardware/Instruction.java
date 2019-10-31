package cesar.hardware;

import java.util.HashMap;

public enum Instruction {
    NOP, CCC, SCC, BR, BNE, BEQ, BPL, BMI, BVC, BVS, BCC, BCS, BGE, BLT, BGT, BLE, BHI, BLS, JMP, SOB, JSR, RTS, CLR,
    NOT, INC, DEC, NEG, TST, ROR, ROL, ASR, ASL, ADC, SBC, MOV, ADD, SUB, CMP, AND, OR, HLT, ConditionalBranch,
    OneOperandInstruction;

    private static HashMap<Integer, Instruction> instructionMap;

    static {
        instructionMap = new HashMap<Integer, Instruction>();
        instructionMap.put(0b0000, NOP);
        instructionMap.put(0b0001, CCC);
        instructionMap.put(0b0010, SCC);
        instructionMap.put(0b0011, ConditionalBranch);
        instructionMap.put(0b00110000, BR);
        instructionMap.put(0b00110001, BNE);
        instructionMap.put(0b00110010, BEQ);
        instructionMap.put(0b00110011, BPL);
        instructionMap.put(0b00110100, BMI);
        instructionMap.put(0b00110101, BVC);
        instructionMap.put(0b00110110, BVS);
        instructionMap.put(0b00110111, BCC);
        instructionMap.put(0b00111000, BCS);
        instructionMap.put(0b00111001, BGE);
        instructionMap.put(0b00111010, BLT);
        instructionMap.put(0b00111011, BGT);
        instructionMap.put(0b00111100, BLE);
        instructionMap.put(0b00111101, BHI);
        instructionMap.put(0b00111110, BLS);
        instructionMap.put(0b0100, JMP);
        instructionMap.put(0b0101, SOB);
        instructionMap.put(0b0110, JSR);
        instructionMap.put(0b0111, RTS);
        instructionMap.put(0b1000, OneOperandInstruction);
        instructionMap.put(0b10000000, CLR);
        instructionMap.put(0b10000001, NOT);
        instructionMap.put(0b10000010, INC);
        instructionMap.put(0b10000011, DEC);
        instructionMap.put(0b10000100, NEG);
        instructionMap.put(0b10000101, TST);
        instructionMap.put(0b10000110, ROR);
        instructionMap.put(0b10000111, ROL);
        instructionMap.put(0b10001000, ASR);
        instructionMap.put(0b10001001, ASL);
        instructionMap.put(0b10001010, ADC);
        instructionMap.put(0b10001011, SBC);
        instructionMap.put(0b1001, MOV);
        instructionMap.put(0b1010, ADD);
        instructionMap.put(0b1011, SUB);
        instructionMap.put(0b1100, CMP);
        instructionMap.put(0b1101, AND);
        instructionMap.put(0b1110, OR);
        instructionMap.put(0b1111, HLT);
    }

    public static Instruction getInstruction(int opcode) {
        final int msb = ((0xFF00 & opcode) >> 8);

        if (instructionMap.containsKey(msb)) {
            return instructionMap.get(msb);
        }
        else if (instructionMap.containsKey(opcode)) {
            return instructionMap.get(opcode);
        }
        else {
            return NOP;
        }
    }
}
