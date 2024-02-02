import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

public class DocumentSearchController {

    @FXML
    private TextArea previewArea;

    @FXML
    private TextArea logArea;

    @FXML
    private Label lblSelectedFile;

    @FXML
    private Label lblSearchDirectory;

    @FXML
    private Label lblTargetDirectory;

    private File selectedFile;
    private File searchDirectory;
    private File targetDirectory;
    private File lastOpenedLocation;

    @FXML
    private void selectFile() {
        FileChooser fileChooser = new FileChooser();
        if (lastOpenedLocation != null) {
            fileChooser.setInitialDirectory(lastOpenedLocation);
        }
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Text Files", "*.txt"),
            new FileChooser.ExtensionFilter("Excel Files", "*.xlsx")
        );
        File file = fileChooser.showOpenDialog(new Stage());
        if (file != null) {
            selectedFile = file;
            updateLabelWithPath(lblSelectedFile, file);
            previewFiles();
            lastOpenedLocation = file.getParentFile(); // Después de seleccionar un archivo
        }
    }

    @FXML
    private void selectSearchDirectory() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        if (lastOpenedLocation != null) {
            directoryChooser.setInitialDirectory(lastOpenedLocation);
        }
        File directory = directoryChooser.showDialog(new Stage());
        if (directory != null) {
            searchDirectory = directory;
            updateLabelWithPath(lblSearchDirectory, directory);
            lastOpenedLocation = directory; // Después de seleccionar un directorio
        }
    }

    @FXML
    private void selectTargetDirectory() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        if (lastOpenedLocation != null) {
            directoryChooser.setInitialDirectory(lastOpenedLocation);
        }
        File directory = directoryChooser.showDialog(new Stage());
        if (directory != null) {
            targetDirectory = directory;
            updateLabelWithPath(lblTargetDirectory, directory);
            lastOpenedLocation = directory; // Después de seleccionar un directorio
        }
    }

    @FXML
    private void startSearch() {
        if (selectedFile == null || searchDirectory == null || targetDirectory == null) {
            logArea.appendText("ERROR: Debes seleccionar todos los directorios y el archivo de lista\n");
            return;
        }

        List<String> fileNames = readFile(selectedFile);
        processFiles(fileNames, searchDirectory, targetDirectory);

        logArea.appendText("Búsqueda completada.\n");
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

    private void updateLabelWithPath(Label label, File file) {
        String fullPath = file.getAbsolutePath();
        int maxCharacters = 65; // Ajusta este valor según el ancho de tus labels
        String displayedPath;
    
        if (fullPath.length() > maxCharacters) {
            displayedPath = "..." + fullPath.substring(fullPath.length() - maxCharacters);
        } else {
            displayedPath = fullPath;
        }
    
        label.setText(displayedPath);
    }

    private void previewFiles() {
        try {
            List<String> fileNames = Files.readAllLines(selectedFile.toPath());
            previewArea.clear();
            fileNames.forEach(name -> previewArea.appendText(name + "\n"));
        } catch (IOException e) {
            logArea.appendText("ERROR: No se pudo leer el archivo seleccionado\n");
        }
    }
}
