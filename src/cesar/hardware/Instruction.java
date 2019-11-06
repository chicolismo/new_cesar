package cesar.hardware;

import java.util.HashMap;
import java.util.HashSet;

import cesar.utils.Bytes;

public enum Instruction {
    NOP, CCC, SCC, BR, BNE, BEQ, BPL, BMI, BVC, BVS, BCC, BCS, BGE, BLT, BGT, BLE, BHI, BLS, JMP, SOB, JSR, RTS, CLR,
    NOT, INC, DEC, NEG, TST, ROR, ROL, ASR, ASL, ADC, SBC, MOV, ADD, SUB, CMP, AND, OR, HLT;

    public static final HashMap<Instruction, String> FORMAT;
    public static final HashSet<Instruction> CONDITIONAL_BRANCH_INSTRUCTIONS;
    public static final HashSet<Instruction> ONE_OP_INSTRUCTIONS;
    public static final HashSet<Instruction> TWO_OP_INSTRUCTIONS;

    private static final HashMap<Integer, Instruction> INT_TO_INSTRUCTION;

    static {
        CONDITIONAL_BRANCH_INSTRUCTIONS = new HashSet<>(15);
        CONDITIONAL_BRANCH_INSTRUCTIONS.add(BR);
        CONDITIONAL_BRANCH_INSTRUCTIONS.add(BNE);
        CONDITIONAL_BRANCH_INSTRUCTIONS.add(BEQ);
        CONDITIONAL_BRANCH_INSTRUCTIONS.add(BPL);
        CONDITIONAL_BRANCH_INSTRUCTIONS.add(BMI);
        CONDITIONAL_BRANCH_INSTRUCTIONS.add(BVC);
        CONDITIONAL_BRANCH_INSTRUCTIONS.add(BVS);
        CONDITIONAL_BRANCH_INSTRUCTIONS.add(BCC);
        CONDITIONAL_BRANCH_INSTRUCTIONS.add(BCS);
        CONDITIONAL_BRANCH_INSTRUCTIONS.add(BGE);
        CONDITIONAL_BRANCH_INSTRUCTIONS.add(BLT);
        CONDITIONAL_BRANCH_INSTRUCTIONS.add(BGT);
        CONDITIONAL_BRANCH_INSTRUCTIONS.add(BLE);
        CONDITIONAL_BRANCH_INSTRUCTIONS.add(BHI);
        CONDITIONAL_BRANCH_INSTRUCTIONS.add(BLS);

        ONE_OP_INSTRUCTIONS = new HashSet<>(12);
        ONE_OP_INSTRUCTIONS.add(CLR);
        ONE_OP_INSTRUCTIONS.add(NOT);
        ONE_OP_INSTRUCTIONS.add(INC);
        ONE_OP_INSTRUCTIONS.add(DEC);
        ONE_OP_INSTRUCTIONS.add(NEG);
        ONE_OP_INSTRUCTIONS.add(TST);
        ONE_OP_INSTRUCTIONS.add(ROR);
        ONE_OP_INSTRUCTIONS.add(ROL);
        ONE_OP_INSTRUCTIONS.add(ASR);
        ONE_OP_INSTRUCTIONS.add(ASL);
        ONE_OP_INSTRUCTIONS.add(ADC);
        ONE_OP_INSTRUCTIONS.add(SBC);

        TWO_OP_INSTRUCTIONS = new HashSet<>(6);
        TWO_OP_INSTRUCTIONS.add(MOV);
        TWO_OP_INSTRUCTIONS.add(ADD);
        TWO_OP_INSTRUCTIONS.add(SUB);
        TWO_OP_INSTRUCTIONS.add(CMP);
        TWO_OP_INSTRUCTIONS.add(AND);
        TWO_OP_INSTRUCTIONS.add(OR);

        INT_TO_INSTRUCTION = new HashMap<Integer, Instruction>();
        INT_TO_INSTRUCTION.put(0b0000, NOP);
        INT_TO_INSTRUCTION.put(0b0001, CCC);
        INT_TO_INSTRUCTION.put(0b0010, SCC);

        // Desvio condicional
        INT_TO_INSTRUCTION.put(0b00110000, BR);
        INT_TO_INSTRUCTION.put(0b00110001, BNE);
        INT_TO_INSTRUCTION.put(0b00110010, BEQ);
        INT_TO_INSTRUCTION.put(0b00110011, BPL);
        INT_TO_INSTRUCTION.put(0b00110100, BMI);
        INT_TO_INSTRUCTION.put(0b00110101, BVC);
        INT_TO_INSTRUCTION.put(0b00110110, BVS);
        INT_TO_INSTRUCTION.put(0b00110111, BCC);
        INT_TO_INSTRUCTION.put(0b00111000, BCS);
        INT_TO_INSTRUCTION.put(0b00111001, BGE);
        INT_TO_INSTRUCTION.put(0b00111010, BLT);
        INT_TO_INSTRUCTION.put(0b00111011, BGT);
        INT_TO_INSTRUCTION.put(0b00111100, BLE);
        INT_TO_INSTRUCTION.put(0b00111101, BHI);
        INT_TO_INSTRUCTION.put(0b00111110, BLS);

        // Outras
        INT_TO_INSTRUCTION.put(0b0100, JMP);
        INT_TO_INSTRUCTION.put(0b0101, SOB);
        INT_TO_INSTRUCTION.put(0b0110, JSR);
        INT_TO_INSTRUCTION.put(0b0111, RTS);

        // Instruções de um operando
        INT_TO_INSTRUCTION.put(0b10000000, CLR);
        INT_TO_INSTRUCTION.put(0b10000001, NOT);
        INT_TO_INSTRUCTION.put(0b10000010, INC);
        INT_TO_INSTRUCTION.put(0b10000011, DEC);
        INT_TO_INSTRUCTION.put(0b10000100, NEG);
        INT_TO_INSTRUCTION.put(0b10000101, TST);
        INT_TO_INSTRUCTION.put(0b10000110, ROR);
        INT_TO_INSTRUCTION.put(0b10000111, ROL);
        INT_TO_INSTRUCTION.put(0b10001000, ASR);
        INT_TO_INSTRUCTION.put(0b10001001, ASL);
        INT_TO_INSTRUCTION.put(0b10001010, ADC);
        INT_TO_INSTRUCTION.put(0b10001011, SBC);

        // Instruções de dois operandos
        INT_TO_INSTRUCTION.put(0b1001, MOV);
        INT_TO_INSTRUCTION.put(0b1010, ADD);
        INT_TO_INSTRUCTION.put(0b1011, SUB);
        INT_TO_INSTRUCTION.put(0b1100, CMP);
        INT_TO_INSTRUCTION.put(0b1101, AND);
        INT_TO_INSTRUCTION.put(0b1110, OR);

        // Instrução de parada
        INT_TO_INSTRUCTION.put(0b1111, HLT);


        FORMAT = new HashMap<Instruction, String>();
        FORMAT.put(NOP, "NOP");
        FORMAT.put(HLT, "HLT");
        FORMAT.put(CCC, "CCC %s");
        FORMAT.put(SCC, "SCC %s");
        for (final var instruction : Instruction.CONDITIONAL_BRANCH_INSTRUCTIONS) {
            FORMAT.put(instruction, instruction.toString() + " %d");
        }
        FORMAT.put(JMP, "JMP %s"); // modo
        FORMAT.put(SOB, "SOB R%d, %d"); // registrador, ddd
        FORMAT.put(JSR, "JSR R%d, %s"); // registrador, modo
        FORMAT.put(RTS, "RTS R%d"); // registrador

        for (final var instruction : Instruction.ONE_OP_INSTRUCTIONS) {
            FORMAT.put(instruction, instruction.toString() + " %s"); // modo
        }

        for (final var instruction : Instruction.TWO_OP_INSTRUCTIONS) {
            FORMAT.put(instruction, instruction.toString() + " %s, %s"); // modo, modo
        }
    }

    public boolean isConditionalBranch() {
        return CONDITIONAL_BRANCH_INSTRUCTIONS.contains(this);
    }

    public boolean isOneOperandInstruction() {
        return ONE_OP_INSTRUCTIONS.contains(this);
    }

    public static boolean isTwoOperandInstruction(Instruction instruction) {
        return TWO_OP_INSTRUCTIONS.contains(instruction);
    }

    public static Instruction getInstruction(byte opcodeByte) {
        int opcode = Bytes.toUnsignedInt(opcodeByte);
        int msb = (0xF0 & opcode) >> 4;
        if (INT_TO_INSTRUCTION.containsKey(msb)) {
            return INT_TO_INSTRUCTION.get(msb);
        }
        else if (INT_TO_INSTRUCTION.containsKey(opcode)) {
            return INT_TO_INSTRUCTION.get(opcode);
        }
        else {
            return NOP;
        }
    }

    public String getFormatString() {
        return FORMAT.get(this);
    }
}
