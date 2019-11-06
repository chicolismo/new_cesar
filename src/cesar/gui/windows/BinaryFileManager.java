package cesar.gui.windows;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import cesar.hardware.Cpu;

public class BinaryFileManager {
    private final MainWindow window;
    private final Cpu cpu;
    private final JFileChooser fileChooser;
    private byte[] fileBytes;
    private String currentFileName;

    public BinaryFileManager(MainWindow owner) {
        this.window = owner;
        this.cpu = owner.cpu;

        fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(false);
        FileNameExtensionFilter fileFilter = new FileNameExtensionFilter("Arquivos do Cesar", "mem");
        fileChooser.setFileFilter(fileFilter);
    }

    public void openFile() {
        // TODO: Verificar se o arquivo atual foi modificado.
        // TODO: Salar o caminho do último arquivo aberto.
        if (fileChooser.showOpenDialog(window) == JFileChooser.APPROVE_OPTION) {
            String fileName = fileChooser.getSelectedFile().getAbsolutePath();

            try {
                copyFileBytes(fileName);
                cpu.setBytes(fileBytes);
                currentFileName = fileName;

                /* Atualiza a interface */
                window.programModel.fireTableDataChanged();
                window.dataModel.fireTableDataChanged();
                window.repaintAll();
            }
            catch (IOException e) {
                JOptionPane.showMessageDialog(window, String.format("Erro ao ler o arquivo %s", fileName),
                    "Erro de leitura", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void copyFileBytes(final String fileName) throws IOException {
        File file = new File(fileName);
        FileInputStream fileInputStream = new FileInputStream(file);
        try (var bufferedInputStream = new BufferedInputStream(fileInputStream)) {
            fileBytes = bufferedInputStream.readAllBytes();
        }
    }

    public boolean hasMemoryChanged() {
        if (currentFileName != null) {
            final byte[] memory = cpu.getMemory();
            final int fileLength = fileBytes.length;
            final int offset = fileLength > Cpu.MEMORY_SIZE ? fileLength - Cpu.MEMORY_SIZE : 0;
            final int maxSize = Math.min(fileLength, Cpu.MEMORY_SIZE);

            for (int i = 0; i < maxSize; ++i) {
                if (fileBytes[i + offset] != memory[i]) {
                    return true;
                }
            }
        }

        return false;
    }

    public void openFilePartially() {

    }

    public void saveFile() {
        // Exibir diálogo para salvar binário
    }

    public void saveTextFile() {

    }

    public void saveBeforeExit() {
        // Testa se a memória foi modificada e oferece para salvar.
    }
}
