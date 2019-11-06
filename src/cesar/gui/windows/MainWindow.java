package cesar.gui.windows;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog.ModalExclusionType;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import cesar.gui.displays.RegisterDisplay;
import cesar.gui.panels.ButtonPanel;
import cesar.gui.panels.ConditionPanel;
import cesar.gui.panels.ExecutionPanel;
import cesar.gui.panels.InstructionPanel;
import cesar.gui.panels.RegisterPanel;
import cesar.gui.panels.StatusBar;
import cesar.gui.tables.DataTable;
import cesar.gui.tables.DataTableModel;
import cesar.gui.tables.ProgramTable;
import cesar.gui.tables.ProgramTableModel;
import cesar.hardware.Base;
import cesar.hardware.ConditionRegister;
import cesar.hardware.Cpu;
import cesar.utils.Shorts;

public class MainWindow extends JFrame {
    private static final long serialVersionUID = 8690285431269859830L;

    private final Cpu cpu;
    private final byte[] memory;
    private final ConditionRegister conditionRegister;
    private final JPanel panel;
    private final ProgramTableModel programModel;
    private final DataTableModel dataModel;
    private final SideWindow<ProgramTableModel> programWindow;
    private final ProgramTable programTable;
    private final DataTable dataTable;
    private final SideWindow<DataTableModel> dataWindow;
    private final TextWindow textWindow;
    private final RegisterPanel registerPanel;
    private final RegisterDisplay[] registerDisplays;
    private final ExecutionPanel executionPanel;
    private final InstructionPanel instructionPanel;
    private final ConditionPanel conditionPanel;
    private final ButtonPanel buttonPanel;
    private final JFileChooser fileChooser;
    private final StatusBar statusBar;
    private final JToggleButton runButton;
    private final JButton nextButton;
    private final HashMap<String, Component> components;
    private boolean programIsRunning;
    private Base currentBase;

    public MainWindow() {
        super("Cesar");
        setResizable(false);
        setFocusable(true);
        setAutoRequestFocus(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);

        programIsRunning = false;

        components = new HashMap<String, Component>();
        currentBase = Base.Decimal;
        panel = new JPanel();

        cpu = new Cpu();
        memory = cpu.getMemory();
        conditionRegister = cpu.getConditionRegister();
        programModel = new ProgramTableModel(cpu.getMemory(), cpu.getMnemonics());
        programWindow = new SideWindow<>(this, "Programa", new ProgramTable(programModel));
        programTable = (ProgramTable) programWindow.getTable();
        programModel.setParent(programTable);
        programWindow.setFocusable(true);

        dataModel = new DataTableModel(cpu.getMemory());
        dataWindow = new SideWindow<>(this, "Dados", new DataTable(dataModel));
        dataTable = (DataTable) dataWindow.getTable();
        dataWindow.setFocusable(true);

        textWindow = new TextWindow(this, cpu.getMemory());

        registerPanel = new RegisterPanel();
        registerDisplays = registerPanel.getDisplays();
        executionPanel = new ExecutionPanel();
        conditionPanel = new ConditionPanel();
        buttonPanel = new ButtonPanel();
        runButton = buttonPanel.getRunButton();
        nextButton = buttonPanel.getNextButton();
        instructionPanel = new InstructionPanel();

        statusBar = new StatusBar();
        statusBar.setText("Bem-vindos");

        fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(false);
        FileNameExtensionFilter fileFilter = new FileNameExtensionFilter("Arquivos do Cesar", "mem");
        fileChooser.setFileFilter(fileFilter);

        initLayout();
        initMenu();
        initEvents();
        pack();
        updateDisplays();
    }

    private void initLayout() {
        JPanel middlePanel = new JPanel();
        GridBagLayout middleGrid = new GridBagLayout();
        middleGrid.columnWidths = new int[] { 0, 0 };
        middleGrid.rowHeights = new int[] { 0, 0 };
        middleGrid.columnWeights = new double[] { 1.0, 0.0 };
        middleGrid.rowWeights = new double[] { 1.0, 0.0 };
        middlePanel.setLayout(middleGrid);
        GridBagConstraints c_0 = new GridBagConstraints();
        c_0.gridx = 0;
        c_0.gridy = 0;
        c_0.gridheight = 2;
        c_0.fill = GridBagConstraints.BOTH;
        middlePanel.add(executionPanel, c_0);
        GridBagConstraints c_1 = new GridBagConstraints();
        c_1.gridx = 1;
        c_1.gridy = 0;
        c_1.fill = GridBagConstraints.HORIZONTAL;
        c_1.anchor = GridBagConstraints.NORTHEAST;
        middlePanel.add(conditionPanel, c_1);
        GridBagConstraints c_2 = new GridBagConstraints();
        c_2.gridx = 1;
        c_2.gridy = 1;
        c_2.fill = GridBagConstraints.HORIZONTAL;
        c_2.anchor = GridBagConstraints.SOUTHEAST;
        middlePanel.add(buttonPanel, c_2);

        BoxLayout vbox = new BoxLayout(panel, BoxLayout.Y_AXIS);
        panel.setLayout(vbox);
        panel.add(registerPanel);
        panel.add(middlePanel);
        panel.add(instructionPanel);

        Border border = new CompoundBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED),
            BorderFactory.createEmptyBorder(4, 4, 4, 4));
        panel.setBorder(border);

        getContentPane().add(panel, BorderLayout.CENTER);
        getContentPane().add(statusBar, BorderLayout.SOUTH);
    }

    void initMenu() {
        int commandKey = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();

        JMenuBar menuBar = new JMenuBar();
        menuBar.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F10, 0), "none");

        JMenu fileMenu = new JMenu("Arquivo");

        JMenuItem fileOpen = new JMenuItem("Abrir", KeyEvent.VK_A);
        fileOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, commandKey));
        fileOpen.addActionListener((e) -> onFileOpen());
        fileMenu.add(fileOpen);

        JMenuItem fileSave = new JMenuItem("Salvar", KeyEvent.VK_S);
        fileSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, commandKey));
        fileSave.addActionListener((e) -> onFileSave());
        fileMenu.add(fileSave);

        JMenu editMenu = new JMenu("Editar");

        JMenu viewMenu = new JMenu("Visualizar");

        JMenu executionMenu = new JMenu("Executar");
        JMenuItem executionRun = new JMenuItem("Rodar");
        components.put("executionRun", executionRun);
        executionRun.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0));
        executionRun.addActionListener((event) -> runButton.doClick());
        executionMenu.add(executionRun);

        JMenuItem executionStep = new JMenuItem("Passo");
        executionStep.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F10, 0));
        executionStep.addActionListener((event) -> nextButton.doClick());
        executionMenu.add(executionStep);
        executionMenu.addSeparator();

        JMenu aboutMenu = new JMenu("?");

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(viewMenu);
        menuBar.add(executionMenu);
        menuBar.add(aboutMenu);
        setJMenuBar(menuBar);
    }

    void initEvents() {
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentMoved(ComponentEvent e) {
                updatePositions();
            }
        });

        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                event.getComponent().requestFocus();
            }
        };

        addMouseListener(mouseAdapter);
        programWindow.addMouseListener(mouseAdapter);
        dataWindow.addMouseListener(mouseAdapter);
        textWindow.addMouseListener(mouseAdapter);

        KeyAdapter keyAdapter = new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                onKeyTyped(e.getKeyChar());
            }
        };

        addKeyListener(keyAdapter);
        programWindow.addKeyListener(keyAdapter);
        dataWindow.addKeyListener(keyAdapter);
        textWindow.addKeyListener(keyAdapter);

        programWindow.getInput().addActionListener((event) -> {
            try {
                final int address = Integer.parseInt(programWindow.getLabelText(), Base.toInt(currentBase));
                final byte value = (byte) (0xFF
                    & Integer.parseInt(programWindow.getInputText(), Base.toInt(currentBase)));
                onTextInput(address, value);
            }
            catch (NumberFormatException e) {
            }
        });

        dataWindow.getInput().addActionListener((event) -> {
            try {
                final int address = Integer.parseInt(dataWindow.getLabelText(), Base.toInt(currentBase));
                final byte value = (byte) (0xFF & Integer.parseInt(dataWindow.getInputText(), Base.toInt(currentBase)));
                onTextInput(address, value);
            }
            catch (NumberFormatException e) {
            }
        });

        for (final RegisterDisplay display : registerPanel.getDisplays()) {
            display.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        onRegisterDisplayDoubleClick(display);
                    }
                }
            });
        }

        final JToggleButton btnDec = buttonPanel.getDecButton();
        final JToggleButton btnHex = buttonPanel.getHexButton();
        final JButton btnNext = buttonPanel.getNextButton();
        final JToggleButton btnRun = buttonPanel.getRunButton();

        btnDec.addActionListener((event) -> onSetBase(Base.Decimal));
        btnHex.addActionListener((event) -> onSetBase(Base.Hexadecimal));
        btnRun.addActionListener((event) -> onRun());
        btnNext.addActionListener((event) -> onNext());

        btnDec.doClick();
    }

    public void updatePositions() {
        int gap = 10;
        int width = getWidth();
        int height = getHeight();
        Point location = getLocation();
        Dimension programDim = programWindow.getSize();
        programWindow.setLocation(location.x - programDim.width - gap, location.y);
        dataWindow.setLocation(location.x + width + gap, location.y);
        textWindow.setLocation(location.x - programDim.width - gap, location.y + height + gap);
        programWindow.setSize(programDim.width, height);
        dataWindow.setSize(dataWindow.getWidth(), height);
    }

    private void updateDisplays() {
        updateRegisterDiplays();
        updateConditionDisplays();
    }

    private void updateRegisterDiplays() {
        for (int i = 0; i < 8; ++i) {
            registerDisplays[i].setValue(cpu.getRegisterValue(i));
        }
    }

    private void updateConditionDisplays() {
        conditionPanel.setNegative(conditionRegister.isNegative());
        conditionPanel.setZero(conditionRegister.isZero());
        conditionPanel.setOverflow(conditionRegister.isOverflow());
        conditionPanel.setCarry(conditionRegister.isCarry());
    }

    public void repaintAll() {
        repaint();
        programWindow.repaint();
        dataWindow.repaint();
        textWindow.repaint();
    }

    public int executeNextInstruction() {
        final int changedAddress = cpu.executeNextInstruction();
        if (changedAddress >= 0) {
            if (Cpu.isDisplayAddress((short) (changedAddress))) {
                programModel.fireTableRowsUpdated(changedAddress, changedAddress);
                dataModel.fireTableRowsUpdated(changedAddress, changedAddress);
            }
            else {
                programModel.fireTableRowsUpdated(changedAddress, changedAddress + 1);
                dataModel.fireTableRowsUpdated(changedAddress, changedAddress + 1);
            }
        }
        updateDisplays();
        return changedAddress;
    }

    private synchronized void setIsRunning(boolean isRunning) {
        programIsRunning = isRunning;
    }

    private synchronized boolean isRunning() {
        return programIsRunning;
    }

    private void onRun() {
        JMenuItem executionRun = (JMenuItem) components.get("executionRun");

        if (runButton.isSelected()) {
            runButton.setToolTipText("Parar (F9)");
            executionRun.setText("Parar");
        }
        else {
            runButton.setToolTipText("Rodar (F9)");
            executionRun.setText("Rodar");
        }

        // Se foi clicado
        if (runButton.isSelected()) {
            statusBar.clear();
            cpu.setHalted(false);
            setIsRunning(true);
            Thread runThread = new Thread() {
                @Override
                public void run() {
                    while (isRunning()) {
                        if (!cpu.isHalted()) {
                            int result = executeNextInstruction();
                            if (result == Cpu.END_OF_MEMORY || result == Cpu.HALTED) {
                                statusBar.setText(cpu.getMessage());
                                runButton.doClick();
                                break;
                            }
                        }
                    }
                    repaintAll();
                    programModel.setCurrentPcRow(Shorts.toUnsignedInt(cpu.getRegisterValue(7)));
                }
            };
            runThread.start();
        }
        else {
            setIsRunning(false);
            cpu.setHalted(true);
        }
    }

    private void onNext() {
        cpu.setHalted(false);
        executeNextInstruction();
        updateRegisterDiplays();
        updateConditionDisplays();
        int pcRow = Shorts.toUnsignedInt(cpu.getRegisterValue(7));
        programModel.setCurrentPcRow(pcRow);
    }

    private void onTextInput(int address, byte value) {
        memory[address] = value;
        programModel.fireTableCellUpdated(address, 2);
        dataModel.fireTableCellUpdated(address, 1);
        if (Cpu.isDisplayAddress((short) address)) {
            textWindow.repaint();
        }
    }

    private void onRegisterDisplayDoubleClick(RegisterDisplay display) {
        int registerNumber = display.getNumber();
        short regValue = cpu.getRegisterValue(registerNumber);
        String message = String.format("Digite novo valor do R%d", registerNumber);
        String stringValue = Integer.toString(regValue, Base.toInt(currentBase));
        String stringInput = JOptionPane.showInputDialog(message, stringValue);
        if (stringInput != null && stringInput.length() > 0) {
            try {
                final short value = (short) (0xFFFF & Integer.parseInt(stringInput, Base.toInt(currentBase)));
                cpu.setRegisterValue(registerNumber, value);
                display.setValue(value);
            }
            catch (NumberFormatException e) {
            }
        }
    }

    private void onSetBase(Base base) {
        if (currentBase != base) {
            currentBase = base;
            programWindow.setBase(base);
            dataWindow.setBase(base);
            registerPanel.setBase(base);
        }
    }

    private void onKeyTyped(char key) {
        // TODO: Colocar o valor no endereço do teclado e indicar que
        // o teclado foi lido.
        cpu.setLastTypedChar(key);
        programModel.fireTableRowsUpdated(Cpu.KEYBOARD_STATE_ADDRESS, Cpu.LAST_CHAR_ADDRESS);
        dataModel.fireTableRowsUpdated(Cpu.KEYBOARD_STATE_ADDRESS, Cpu.LAST_CHAR_ADDRESS);
    }

    public void onFileOpen() {
        // TODO: Verificar se o arquivo atual foi modificado.
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            String fileName = fileChooser.getSelectedFile().getAbsolutePath();
            try {
                cpu.readBinaryFile(fileName);
                // Atualiza a interface
                programModel.fireTableRowsUpdated(-1, -1);
                dataModel.fireTableRowsUpdated(-1, -1);
                repaintAll();
            }
            catch (IOException e) {
                JOptionPane.showMessageDialog(this, String.format("Erro ao ler o arquivo %s", fileName),
                    "Erro de leitura", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void onFileSave() {

    }

    public void onExit() {
        // TODO: Verifica se houve mudanças no arquivo atual e oferece salvar.
        System.exit(0);
    }

    private void centerOnScreen() {
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(dim.width / 2 - getSize().width / 2, dim.height / 2 - getSize().height / 2);
    }

    public void initializePositions() {
        centerOnScreen();
        updatePositions();
        programWindow.setVisible(true);
        dataWindow.setVisible(true);
        textWindow.setVisible(true);
        dataTable.scrollToRow(1024, true);
    }
}
