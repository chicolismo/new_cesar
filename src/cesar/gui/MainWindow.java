package cesar.gui;

import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

import cesar.gui.panels.ButtonPanel;
import cesar.gui.panels.ConditionPanel;
import cesar.gui.panels.ExecutionPanel;
import cesar.gui.panels.RegisterPanel;
import cesar.gui.tables.DataTableModel;
import cesar.gui.tables.ProgramTableModel;
import cesar.gui.tables.Table;
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

    public MainWindow() {
        super("Cesar");
        // setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        panel = new JPanel();
        setContentPane(panel);

        cpu = new Cpu();
        memory = cpu.getMemory();

        programModel = new ProgramTableModel(cpu.getMemory());
        programWindow = new SideWindow<>(this, "Programa", new Table<ProgramTableModel>(programModel));

        dataModel = new DataTableModel(cpu.getMemory());
        dataWindow = new SideWindow<>(this, "Dados", new Table<DataTableModel>(dataModel));

        initLayout();
        initEvents();
        pack();
    }

    private void initLayout() {
        programWindow.setVisible(true);
        dataWindow.setVisible(true);

        BoxLayout vbox = new BoxLayout(panel, BoxLayout.Y_AXIS);

        RegisterPanel registerPanel = new RegisterPanel();
        ExecutionPanel executionPanel = new ExecutionPanel();
        ConditionPanel conditionPanel = new ConditionPanel();
        ButtonPanel buttonPanel = new ButtonPanel();

        panel.setLayout(vbox);
        panel.add(registerPanel);

        JPanel middleRightPanel = new JPanel();
        BoxLayout middleRightBox = new BoxLayout(middleRightPanel, BoxLayout.Y_AXIS);
        middleRightPanel.setLayout(middleRightBox);
        middleRightPanel.add(conditionPanel);
        middleRightPanel.add(buttonPanel);

        JPanel middlePanel = new JPanel();
        BoxLayout middleBox = new BoxLayout(middlePanel, BoxLayout.X_AXIS);
        middlePanel.setLayout(middleBox);
        middlePanel.add(executionPanel);
        middlePanel.add(middleRightPanel);

        panel.add(middlePanel);
    }

    void initEvents() {
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentMoved(ComponentEvent e) {
                updatePositions();
            }
        });
    }

    void updatePositions() {
        int gap = 10;
        int width = getWidth();
        int height = getHeight();
        Point location = getLocation();
        programWindow.setLocation(location.x - programWindow.getWidth() - gap, location.y);
        dataWindow.setLocation(location.x + width + gap, location.y);
    }

    public void onTextInput(int address, byte value) {
        memory[address] = value;
        programModel.fireTableCellUpdated(address, 2);
        dataModel.fireTableCellUpdated(address, 1);
    }
}
