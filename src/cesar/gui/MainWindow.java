package cesar.gui;

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
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
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
import cesar.gui.tables.DataTableModel;
import cesar.gui.tables.ProgramTableModel;
import cesar.gui.tables.Table;
import cesar.hardware.Base;
import cesar.hardware.Cpu;

public class MainWindow extends JFrame {
    private static final long serialVersionUID = 8690285431269859830L;

    private Cpu cpu;
    private byte[] memory;
    private final JPanel panel;
    private final ProgramTableModel programModel;
    private final DataTableModel dataModel;
    private final SideWindow<ProgramTableModel> programWindow;
    private final SideWindow<DataTableModel> dataWindow;
    private final TextWindow textWindow;
    private final RegisterPanel registerPanel;
    private final ExecutionPanel executionPanel;
    private final InstructionPanel instructionPanel;
    private final ConditionPanel conditionPanel;
    private final ButtonPanel buttonPanel;
    private final JFileChooser fileChooser;
    private final StatusBar statusBar;
    private Base currentBase;

    public MainWindow() {
        super("Cesar");
        setResizable(false);
        setAutoRequestFocus(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);

        currentBase = Base.Decimal;

        panel = new JPanel();
//        setContentPane(panel);

        cpu = new Cpu();
        memory = cpu.getMemory();

        programModel = new ProgramTableModel(cpu.getMemory());
        programWindow = new SideWindow<>(this, "Programa", new Table<ProgramTableModel>(programModel));

        dataModel = new DataTableModel(cpu.getMemory());
        dataWindow = new SideWindow<>(this, "Dados", new Table<DataTableModel>(dataModel));

        textWindow = new TextWindow(this, cpu.getMemory());

        registerPanel = new RegisterPanel();
        executionPanel = new ExecutionPanel();
        conditionPanel = new ConditionPanel();
        buttonPanel = new ButtonPanel();
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
        setMinimumSize(getSize());
    }

    private void initLayout() {
        programWindow.setVisible(true);
        dataWindow.setVisible(true);
        textWindow.setVisible(true);

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
        c_1.fill = GridBagConstraints.BOTH;
        c_1.anchor = GridBagConstraints.NORTHEAST;
        middlePanel.add(conditionPanel, c_1);
        GridBagConstraints c_2 = new GridBagConstraints();
        c_2.gridx = 1;
        c_2.gridy = 1;
        c_2.fill = GridBagConstraints.BOTH;
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

        add(panel, BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);
    }

    void initMenu() {
        int commandKey = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("Arquivo");

        JMenuItem fileOpen = new JMenuItem("Abrir", KeyEvent.VK_A);
        fileOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, commandKey));
        fileOpen.addActionListener((e) -> {
            onFileOpen();
        });
        fileMenu.add(fileOpen);

        JMenuItem fileSave = new JMenuItem("Salvar", KeyEvent.VK_S);
        fileSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, commandKey));
        fileSave.addActionListener((e) -> {
            onFileSave();
        });
        fileMenu.add(fileSave);

        JMenu editMenu = new JMenu("Editar");

        JMenu viewMenu = new JMenu("Visualizar");

        JMenu runMenu = new JMenu("Executar");

        JMenu aboutMenu = new JMenu("?");

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(viewMenu);
        menuBar.add(runMenu);
        menuBar.add(aboutMenu);
        setJMenuBar(menuBar);
    }

    void initEvents() {
        final Component parent = this;

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentMoved(ComponentEvent e) {
                updatePositions();
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
    }

    void updatePositions() {
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

    public void onTextInput(int address, byte value) {
        memory[address] = value;
        programModel.fireTableCellUpdated(address, 2);
        dataModel.fireTableCellUpdated(address, 1);
        if (cpu.isDisplayAddress((short) address)) {
            textWindow.repaint();
        }
    }

    public void onRegisterDisplayDoubleClick(RegisterDisplay display) {
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
                // Do nothing.
            }
        }
    }

    public void onFileOpen() {
        // TODO: Verificar se o arquivo atual foi modificado.
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            JOptionPane.showMessageDialog(this, fileChooser.getSelectedFile().getAbsolutePath());
        }
    }

    public void onFileSave() {

    }
}
