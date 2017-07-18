/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.nio.file.Files;
import static java.util.logging.Level.SEVERE;
import java.util.logging.Logger;
import javafx.concurrent.Task;

/**
 *
 * @author Kasia
 */
public class SendFileTask extends Task<Void> {
    private static Logger log = Logger.getLogger(SendFileTask.class.getCanonicalName());
    
    // plik do wysłania
    File file;
    
    public SendFileTask(File file) { 
        this.file = file; 
    }
    
    @Override
    protected Void call() throws Exception {
        updateMessage("Initiating...");
        
        try (Socket socket = new Socket("127.0.0.1", 1337);
             ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
             BufferedInputStream in = new BufferedInputStream(Files.newInputStream(file.toPath()))) { // pobranie pliku z komputera
            
            updateMessage("Sending...");
            
            out.writeUTF(file.getName());
            
            byte[] buffer = new byte[4096];
            int readBytes;
            int sentBytes = 0;
            
            while ((readBytes = in.read(buffer)) != -1) {
                out.write(buffer, 0, readBytes); // zapisanie pliku na wyjście
                sentBytes += readBytes;
                updateProgress(sentBytes, file.length());
            }
            
            updateMessage("Done!");
            
        } catch (ConnectException e) {
            updateMessage("Connection refused");
        } catch (IOException e) {
            log.log(SEVERE, e.getMessage(), e);
        }
    
        return null;
    }
}
