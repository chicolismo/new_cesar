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
    public static final int KEYBOARD_STATE_ADDRESS = 65498;
    public static final int LAST_CHAR_ADDRESS = 65499;

    static final int PC = 7;

    /**
     * Contém valores dos registradores da Cpu.
     */
    private final short[] registers;

    /**
     * Os bytes da memória da Cpu, onde estão as instruções e os dados.
     */
    private final byte[] memory;

    private final String[] mnemonics;

    /**
     * A <i>ALU</i> ou (Unidade lógica e aritmética) da Cpu. Responsável pelas
     * operações complexas que envolvem aplicar operações sobre operandos e
     * atualizar o registrador de condições.
     */
    final Alu alu;

    /**
     * O registrador de condições contém informações sobre certos resultados da
     * última instrução executada, como por exemplo:
     * 
     * <ul>
     * <li>O resultado é zero?</li>
     * <li>É negativo?</li>
     * <li>A operação causou <i>overflow</i> na representação de algum
     * operando?</li>
     * <li>Ocorreu o bit vai-um ou vem-um?</li>
     * </ul>
     */
    private final ConditionRegister conditionRegister;

    /**
     * Dependendo da instrução executada, este campo conterá uma mensagem relevante.
     */
    private String message;

    /**
     * Indica se a Cpu executou a instrução <code>HLT</code>. Isso é útil para
     * terminar alguma <i>thread</i> que esteja executando diversas instruções.
     */
    boolean halted;

    public Cpu() {
        registers = new short[8];
        memory = new byte[MEMORY_SIZE];
        mnemonics = new String[MEMORY_SIZE];
        Mnemonic.updateMnemonics(memory, mnemonics, 0);
        conditionRegister = new ConditionRegister();
        conditionRegister.setZero(true); // Cesar inicializa com Z = 1
        alu = new Alu(this);
        halted = true;
        message = "Bem-vindos";
    }

    public String getMessage() {
        return message;
    }

    public short getRegisterValue(int registerNumber) {
        return registers[registerNumber];
    }

    public void setRegisterValue(int registerNumber, short newValue) {
        registers[registerNumber] = newValue;
    }

    public boolean isHalted() {
        return halted;
    }

    public void setHalted(boolean halted) {
        this.halted = halted;
    }

    // 65499
    public void setLastTypedChar(char c) {
        memory[KEYBOARD_STATE_ADDRESS] = (byte) 0x80;
        memory[LAST_CHAR_ADDRESS] = (byte) c;
    }

    short[] getRegisters() {
        return registers;
    }

    public ConditionRegister getConditionRegister() {
        return conditionRegister;
    }

    public byte[] getMemory() {
        return memory;
    }

    public String[] getMnemonics() {
        return mnemonics;
    }

    public void setMemory(final byte[] bytes) {
        final int offset = bytes.length > MEMORY_SIZE ? bytes.length - MEMORY_SIZE : 0;
        final int max = Math.max(bytes.length, MEMORY_SIZE);
        for (int i = offset; i < max; ++i) {
            memory[i - offset] = bytes[i];
        }
    }

    public static boolean isDisplayAddress(final int address) {
        return (address >= BEGIN_DISPLAY_ADDRESS && address <= END_DISPLAY_ADDRESS);
    }

    private byte fetchNextByte() {
        // TODO: Incrementar acessos de memória?
        byte nextByte = memory[Shorts.toUnsignedInt(registers[PC])];
        ++registers[PC];
        return nextByte;
    }

    short getWord(int address) {
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

    void setWord(final int address, final short value) {
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

    int getAddress(final AddressMode mode, final int registerNumber) {
        int address = 0;

        switch (mode) {
            case Register:
                /*
                 * O registrador contém o operando, portanto o endereço é o índice do
                 * registrador
                 */

                address = (short) registerNumber;
                break;

            case RegisterPostIncremented:
                /*
                 * O registrador indicado contém o endereço do operando. Após o uso, seu
                 * conteúdo é incrementado de duas unidades, para apontar para o operando
                 * seguinte.
                 */

                address = Shorts.toUnsignedInt(registers[registerNumber]);
                registers[registerNumber] += 2;
                break;

            case RegisterPreDecremented:
                /*
                 * O registrador indicado é decrementado de duas unidades, contendo, então, o
                 * endereço do operando.
                 */

                registers[registerNumber] -= 2;
                address = Shorts.toUnsignedInt(registers[registerNumber]);
                break;

            case Indexed: {
                /*
                 * O conteúdo do registrador indicado é somado à palavra que segue o código da
                 * instrução, para, desta maneira, formar o endereço do operando. Note que o R7
                 * (PC) é atualizado antes de a soma ser realizada.
                 */

                // Palavra que segue o código da instrução
                final short nextWord = getWord(registers[PC]);

                // Incrementa o PC
                registers[PC] += 2;

                // Soma o valor do registrador indicado com a palavra que segue a instrução
                address = Shorts.toUnsignedInt((short) (registers[registerNumber] + nextWord));
                break;
            }

            case RegisterIndirect: {
                /*
                 * O registrador indicado contém o endereço do operando.
                 */
                address = Shorts.toUnsignedInt(registers[registerNumber]);
                break;
            }

            case PostIncrementedIndirect: {
                /*
                 * O registrador indicado contém o "endereço do endereço" do operando e, após, é
                 * incrementado de duas unidades.
                 */

                // Lê o endereço do endereço.
                final int firstAddress = Shorts.toUnsignedInt(registers[registerNumber]);

                // Incrementa de duas unidades
                registers[registerNumber] += 2;

                // Obtém o endereço final do operando.
                address = getWord(firstAddress);
                break;
            }

            case PreDrecrementedIndirect: {
                /*
                 * O regisrador indicado é decrementado de duas unidades e, após, contém o
                 * "endereço do endereço" do operando.
                 */

                // Decrementa o registrador
                registers[registerNumber] -= 2;

                // Obtém o endereço do endereço
                final int firstAddress = Shorts.toUnsignedInt(registers[registerNumber]);

                // Obtém o endereço do operando, usando o endereço anterior
                address = getWord(firstAddress);
                break;
            }

            case IndexedIndirect: {
                /*
                 * O conteúdo do registrador indicado é somado à palavra que segue o código da
                 * instrução, para, desta maneira, formar o endereço do endereço do operando. O
                 * R7 (PC) é atualizado antes de a soma ser realizada.
                 */

                // Palavra que segue o código da instrução.
                final short nextWord = getWord(registers[PC]);

                // Atualiza o PC
                registers[PC] += 2;

                // Soma a palavra seguinte ao valor do registrador para obter o endereço do
                // endereço.
                final int firstAddress = Shorts.toUnsignedInt((short) (nextWord + registers[registerNumber]));

                // Obtém o endereço do operando.
                address = getWord(firstAddress);
                break;
            }
        }

        return address;
    }

    short getValueFromAddress(final AddressMode mode, final int address) {
        if (mode == AddressMode.Register) {
            return registers[address];
        }
        else {
            return getWord(address);
        }
    }

    void setValueToAddress(final AddressMode mode, final int address, final short value) {
        if (mode == AddressMode.Register) {
            registers[address] = value;
        }
        else {
            setWord(address, value);
        }
    }

    public static final int END_OF_MEMORY = -1;
    public static final int NOOP = -2;
    public static final int HALTED = -3;
    public static final int INVALID_INSTRUCTION = -4;
    public static final int REGISTER_ADDRESS = -5;
    public static final int CMP_INSTRUCTION = -6;

    /**
     * Executa a instrução atualmente apontada pelo PC.
     * 
     * @return Um <code>int</code> contendo o endereço do byte que foi alterado na
     *         memória. Se nenhum byte foi alterado, um valor negativo é retornado,
     *         explicando qual o motivo da memória não ser escrita no términdo da
     *         instrução.
     * 
     *         Os possíveis motivos para não alterar a memória são:
     * 
     *         <dl>
     *         <dt>END_OF_MEMORY</dt>
     *         <dd>O PC chegou ao fim da memória e não executou nenhuma
     *         instrução</dd>
     *         <dt>NOOP</dt>
     *         <dd>O programa executou um <code>NOP</code>.
     *         <dt>HALTED</dt>
     *         <dd>O programa executou um <code>HLT</code>.
     *         <dt>INVALID_INSTRUCTION</dt>
     *         <dd>O programa encontrou uma instrução inválida</dd>
     *         <dt>REGISTER_ADDRESS</dt>
     *         <dd>A valor alterado pertence a um registrador, e portanto a memória
     *         não foi modificada</dd>
     *         <dt>CMP_INSTRUCTION</dt>
     *         <dd>A instrução executada foi <code>CMP</code>, que não escreve nada
     *         no operador destino.</dd>
     *         </dl>
     */
    public int executeNextInstruction() {
        if (isHalted()) {
            return HALTED;
        }

        if (registers[PC] == -1) {
            setHalted(true);
            message = "PC chegou ao final da memória";
            return END_OF_MEMORY;
        }

        final byte firstByte = fetchNextByte();

        Instruction instruction = Instruction.getInstruction(firstByte);

        if (instruction == Instruction.NOP) {
            return NOOP;
        }

        if (instruction == Instruction.HLT) {
            setHalted(true);
            return HALTED;
        }

        if (instruction.isConditionalBranch()) {
            byte offset = fetchNextByte();
            alu.conditionalBranch(instruction, offset);
            return REGISTER_ADDRESS;
        }
        else if (instruction.isOneOperandInstruction()) {
            final byte nextByte = fetchNextByte();
            final int mmm = (nextByte & 0b0011_1000) >> 3;
            final int rrr = (nextByte & 0b0000_0111);
            final AddressMode mode = AddressMode.fromInt(mmm);
            final int address = getAddress(mode, rrr);
            final short value = getValueFromAddress(mode, address);
            final short result = alu.oneOperandInstruction(instruction, value);

            setValueToAddress(mode, address, result);
            if (mode != AddressMode.Register) {
                return address;
            }
            else {
                return REGISTER_ADDRESS;
            }
        }
        else {
            switch (instruction) {
                case CCC:
                    alu.ccc(firstByte);
                    return REGISTER_ADDRESS;
                case SCC:
                    alu.scc(firstByte);
                    return REGISTER_ADDRESS;
                case JMP: {
                    short nextByte = fetchNextByte();
                    final int mmm = (nextByte & 0b0011_1000) >> 3;
                    final int rrr = (nextByte & 0b0000_0111);
                    final AddressMode mode = AddressMode.fromInt(mmm);
                    final int address = getAddress(mode, rrr);
                    alu.jmp(mode, address);
                    return REGISTER_ADDRESS;
                }
                case SOB: {
                    final int registerNumber = (firstByte & 0b0000_0111);
                    final byte offset = fetchNextByte();
                    alu.sob(registerNumber, offset);
                    return REGISTER_ADDRESS;
                }
                case JSR: {
                    final byte nextByte = fetchNextByte();
                    final short word = Shorts.fromBytes(firstByte, nextByte);
                    final int reg = (word & 0b0000_0111_0000_0000) >> 8;
                    final int mmm = (word & 0b0000_0000_0011_1000) >> 3;
                    final int rrr = (word & 0b0000_0000_0000_0111);
                    final AddressMode mode = AddressMode.fromInt(mmm);
                    final int address = getAddress(mode, rrr);
                    alu.jsr(mode, address, reg);
                    return REGISTER_ADDRESS;
                }
                case RTS:
                    alu.rts(firstByte);
                    return REGISTER_ADDRESS;
                default: {
                    if (Instruction.isTwoOperandInstruction(instruction)) {
                        final byte nextByte = fetchNextByte();
                        final int word = Shorts.toUnsignedInt(Shorts.fromBytes(firstByte, nextByte));

                        final int mmm_1 = (word & 0b0000_1110_0000_0000) >> 9;
                        final int rrr_1 = (word & 0b0000_0001_1100_0000) >> 6;

                        final int mmm_2 = (word & 0b0000_0000_0011_1000) >> 3;
                        final int rrr_2 = (word & 0b0000_0000_0000_0111);

                        final AddressMode srcMode = AddressMode.fromInt(mmm_1);
                        final int srcAddress = getAddress(srcMode, rrr_1);

                        final AddressMode dstMode = AddressMode.fromInt(mmm_2);
                        final int dstAddress = getAddress(dstMode, rrr_2);

                        final short srcValue = getValueFromAddress(srcMode, srcAddress);
                        final short dstValue = getValueFromAddress(dstMode, dstAddress);
                        final short result = alu.twoOperandInstruction(instruction, srcValue, dstValue);

                        if (instruction != Instruction.CMP) {
                            setValueToAddress(dstMode, dstAddress, result);
                            return dstAddress;
                        }
                        else {
                            return CMP_INSTRUCTION;
                        }
                    }
                }
            }
        }

        return INVALID_INSTRUCTION;
    }


    /**
     * Decrementa o SP em 2 e coloca uma palavra no topo da pilha.
     * 
     * Como a arquitetura do Cesar é Big Endian, o byte mais significativo é
     * colocado no endereço maior, e o meno significativo no endereço imediatamente
     * abaixo.
     * 
     * @param word A palavra a ser colocada no topo da pilha.
     */
    void push(short word) {
        int unsigned = Shorts.toUnsignedInt(word);
        byte msb = (byte) (unsigned >> 8);
        byte lsb = (byte) ((unsigned & 0xFF) >> 8);
        registers[6] -= 2;
        memory[registers[6]] = msb;
        memory[registers[6] + 1] = lsb;
    }

    /**
     * Retorna a palavra que está no topo da pilha e incrementa o SP em 2;
     * 
     * @return A palavra que se encontra no topo da pilha.
     */
    short pop() {
        byte msb = memory[Shorts.toUnsignedInt(registers[6])];
        byte lsb = memory[Shorts.toUnsignedInt((short) (registers[6] + 1))];
        registers[6] += 2;
        return Shorts.fromBytes(msb, lsb);
    }

    /**
     * Dado um nome de arquivo binário, tenta copiar seus dados para a memória do
     * cpu. Se o arquivo for maior que a capacidade da memória, apenas os últimos
     * <i>n</i> bytes são lidos, onde <i>n</i> é o tamanho da memória.
     * 
     * @param filename O nome do arquivo a ser lido.
     * @throws IOException Se por qualquer motivo não for possível ler o arquivo
     *                     cujo nome foi fornecido, essa exceção será lançada.
     */
    public void readBinaryFile(String filename) throws IOException {
        File file = new File(filename);
        FileInputStream fileInputStream = new FileInputStream(file);

        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream)) {
            var buffer = bufferedInputStream.readAllBytes();
            setMemory(buffer);
            Mnemonic.updateMnemonics(memory, mnemonics, 0);
        }
    }
}
