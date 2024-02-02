import javax.swing.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class App {

    public static void main(String[] args) {
        try {
            // Establecer el Look and Feel del sistema
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        System.out.println("Selecciona el archivo txt o Excel con los nombres a buscar:");
        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            System.out.println("Selecciona el directorio donde buscar:");
            File searchDirectory = null;
            if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                searchDirectory = fileChooser.getSelectedFile();
            }

            System.out.println("Selecciona el directorio donde copiar los ficheros encontrados:");
            File targetDirectory = null;
            if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                targetDirectory = fileChooser.getSelectedFile();
            }

            if (selectedFile != null && searchDirectory != null && targetDirectory != null) {
                List<String> fileNames = readFile(selectedFile);
                processFiles(fileNames, searchDirectory, targetDirectory);
            } else {
                System.out.println("Operación cancelada o directorios no seleccionados correctamente.");
            }
        }
    }

    private static List<String> readFile(File file) {
        List<String> fileNames = new ArrayList<>();
        try {
            String fileName = file.getName();
            if (fileName.endsWith(".txt")) {
                fileNames = Files.readAllLines(file.toPath());
            } else if (fileName.endsWith(".xlsx")) {
                FileInputStream fis = new FileInputStream(file);
                Workbook workbook = new XSSFWorkbook(fis);
                Sheet sheet = workbook.getSheetAt(0);
                for (Row row : sheet) {
                    Cell cell = row.getCell(0);
                    if (cell != null) {
                        fileNames.add(cell.getStringCellValue());
                    }
                }
                workbook.close();
                fis.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileNames;
    }


    private static void processFiles(List<String> fileNames, File searchDirectory, File targetDirectory) {
        try {
            Map<String, List<File>> foundFiles = new HashMap<>();
            searchFiles(searchDirectory, fileNames, foundFiles);

            if (!targetDirectory.exists()) {
                targetDirectory.mkdirs();
            }

            File logFile = new File(targetDirectory, "log.txt");
            BufferedWriter logWriter = new BufferedWriter(new FileWriter(logFile));

            for (String namePart : fileNames) {
                List<File> files = foundFiles.get(namePart);
                handleFileCopying(namePart, files, targetDirectory, logWriter);
            }

            logWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleFileCopying(String namePart, List<File> files, File targetDirectory, BufferedWriter logWriter) throws IOException {
        if (files == null || files.isEmpty()) {
            logWriter.write("ERROR || ERROR || ERROR -- No se encontró el archivo con el fragmento: " + namePart + " ERROR || ERROR || ERROR" + "\n");
            System.out.println("No se encontró el archivo con el fragmento: " + namePart);
            return;
        }

        if (files.size() > 1) {
            File coincidentesDir = new File(targetDirectory, "Ficheros coincidentes");
            coincidentesDir.mkdirs();
            for (File file : files) {
                Files.copy(file.toPath(), Paths.get(coincidentesDir.getPath(), file.getName()), StandardCopyOption.REPLACE_EXISTING);
            }
            logWriter.write("ERROR || ERROR || ERROR -- Múltiples archivos encontrados para el fragmento: " + namePart + ". Copiados a 'Ficheros coincidentes'. " + "ERROR || ERROR || ERROR" + "\n");
            System.out.println("Múltiples archivos encontrados para el fragmento: " + namePart + ". Copiados a 'Ficheros coincidentes'.");
        } else {
            Files.copy(files.get(0).toPath(), Paths.get(targetDirectory.getPath(), files.get(0).getName()), StandardCopyOption.REPLACE_EXISTING);
            logWriter.write("Archivo encontrado y copiado para el fragmento: " + namePart + "\n");
            System.out.println("Archivo encontrado y copiado para el fragmento: " + namePart);
        }
    }

    private static void searchFiles(File directory, List<String> fileNames, Map<String, List<File>> foundFiles) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    searchFiles(file, fileNames, foundFiles);
                } else {
                    for (String namePart : fileNames) {
                        if (file.getName().contains(namePart)) {
                            foundFiles.computeIfAbsent(namePart, k -> new ArrayList<>()).add(file);
                        }
                    }
                }
            }
        }
    }
}
