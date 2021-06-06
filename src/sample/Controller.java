package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private TextField pathField;

    @FXML
    private Button browseButton;

    @FXML
    private Pane pane1;

    @FXML
    private Button optimizeButton;

    @FXML
    private CheckBox addXMLCheck;


    final FileChooser fileChooser = new FileChooser();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Choose SVGs files...", "*.svg", "*.xml");

        fileChooser.getExtensionFilters().add(filter);

        pathField.setOnDragOver(event -> {
            Dragboard db = event.getDragboard();
            if (db.hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY);
            } else {
                event.consume();
            }
        });

        pathField.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasFiles()) {
                success = true;
                String filePath = null;
                for (File file : db.getFiles()) {
                    filePath = file.getAbsolutePath();
                    pathField.setText(filePath);
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }

    private void showAlertFileError() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("File error");
        alert.setHeaderText(null);
        alert.setContentText("The file does not exist!");

        alert.showAndWait();
    }

    public void showGUI() {
        File file = fileChooser.showOpenDialog(browseButton.getScene().getWindow());
        pathField.setText(file.getAbsolutePath());
    }

    public void optimize() {
        String pathFile = pathField.getText();
        Boolean addXMLtags = addXMLCheck.isSelected();
        File file = new File(pathFile);

        try {
            String match = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
            if (file.isFile()) {
                if (addXMLtags)
                    match = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" + match;
                String str = match.replaceAll("(\\d+\\.\\d)(?:\\d+)", "$1");
                String baseName = FilenameUtils.getBaseName(file.getName());
                BufferedWriter writer = new BufferedWriter(new FileWriter(file.getParent() + "/" + baseName + "_opt.svg"));
                writer.write(str);
                writer.close();
            } else {
                showAlertFileError();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

