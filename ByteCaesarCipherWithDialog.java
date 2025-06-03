import javax.swing.*;
import java.io.*;

public class ByteCaesarCipherWithDialog {

    public static void main(String[] args) {
        // Prompt for encryption or decryption
        String[] options = {"Encrypt", "Decrypt"};
        
        int modeChoice = JOptionPane.showOptionDialog(null, "Select mode:", "Encryption/Decryption",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if (modeChoice == -1) {
            JOptionPane.showMessageDialog(null, "Operation cancelled.");
            return;
        }

        String mode = (modeChoice == 0) ? "encrypt" : "decrypt";

        // Prompt for key
        String keyInput = JOptionPane.showInputDialog("Enter numeric key (integer):");
        if (keyInput == null) {
            JOptionPane.showMessageDialog(null, "Operation cancelled.");
            return;
        }

        int key;
        try {
            key = Integer.parseInt(keyInput.trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid key. Must be an integer.");
            return;
        }

        key = key % 256;
        if (key < 0) key += 256;

        // File chooser dialog
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Choose file to " + mode);

        int result = fileChooser.showOpenDialog(null);
        if (result != JFileChooser.APPROVE_OPTION) {
            JOptionPane.showMessageDialog(null, "No file selected.");
            return;
        }

        File inputFile = fileChooser.getSelectedFile();

        // Build output file name
        String prefix = mode.equals("encrypt") ? "encrypted_" : "decrypted_";
        String fileName = inputFile.getName();
        String baseName = fileName;
        String extension = "";

        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex != -1) {
            baseName = fileName.substring(0, dotIndex);
            extension = fileName.substring(dotIndex);
        }

        String parent = inputFile.getParent();
        File outputFile = new File(parent, prefix + baseName + extension);

        int counter = 1;
        while (outputFile.exists()) {
            outputFile = new File(parent, prefix + baseName + "(" + counter + ")" + extension);
            counter++;
        }

        // Perform encryption/decryption
        int shift = mode.equals("encrypt") ? key : (256 - key) % 256;

        try (FileInputStream fis = new FileInputStream(inputFile);
             FileOutputStream fos = new FileOutputStream(outputFile)) {

            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = fis.read(buffer)) != -1) {
                for (int i = 0; i < bytesRead; i++) {
                    int unsignedByte = buffer[i] & 0xFF;
                    int cipherByte = (unsignedByte + shift) % 256;
                    buffer[i] = (byte) cipherByte;
                }
                fos.write(buffer, 0, bytesRead);
            }

            JOptionPane.showMessageDialog(null, "File successfully " +
                    (mode.equals("encrypt") ? "encrypted" : "decrypted") +
                    ":\n" + outputFile.getAbsolutePath());

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }
}
