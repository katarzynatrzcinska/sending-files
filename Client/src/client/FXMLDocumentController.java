/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.FileChooser;

/**
 *
 * @author Kasia
 */
public class FXMLDocumentController implements Initializable {
    @FXML 
    private Label statusLabel;
    @FXML 
    private ProgressBar progressBar;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    
    
    @FXML
    private void chooseFile(ActionEvent event) {
        FileChooser fc = new FileChooser();
        File file = fc.showOpenDialog(null);
        Task<Void> sendFileTask = new SendFileTask(file); // klasa zadania
        statusLabel.textProperty().bind(sendFileTask.messageProperty());
        progressBar.progressProperty().bind(sendFileTask.progressProperty());
        executor.submit(sendFileTask); // uruchomienie zadania w tle
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
