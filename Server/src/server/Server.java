/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import static java.lang.String.format;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import static java.util.logging.Level.SEVERE;
import java.util.logging.Logger;

/**
 *
 * @author Kasia
 */
public class Server {
    private static Logger log = Logger.getLogger(Server.class.getCanonicalName());
    
    ExecutorService executor = Executors.newFixedThreadPool(4); // pula wątków o stałym rozmiarze
    
    private void listen(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) { // nasłuchiwanie na połączenia na wybranym porcie
            log.info(format("Listening on port %d", port));
            
            while (true) {
                final Socket socket = serverSocket.accept(); // przyjęcie połączenia od klienta
                executor.submit(() -> handleClient(socket)); // wysłanie zadania do puli wątków
            }
        } catch (IOException e) {
            log.log(SEVERE, e.getMessage(), e);
        }
    }
    
    private void handleClient(Socket socket) {
        log.info(format("Connection from: %s", socket.getInetAddress().getHostAddress()));
        
        try (ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()))) { //  tworzy obiekty typu InputStream, który czyta bajt po bajcie z socketu, czyli z tego co przysyła klient
            String fileName = in.readUTF();
            log.info(format("Receiving file: %s", fileName));
            
            try (BufferedOutputStream out = new BufferedOutputStream(Files.newOutputStream(Paths.get(fileName)))) { // utworzenie nowego pliku na komputerze
                byte[] buffer = new byte[4096]; // bufor 4KB
                int readSize;
                
                while((readSize = in.read(buffer)) != -1) { // czytanie kawałka o wielkości bufora i wpisywanie do pliku na komputerze
                    out.write(buffer, 0, readSize);
                }
            }
            
        } catch (IOException e) {
            log.log(SEVERE, e.getMessage(), e);
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.listen(1337);
    }
}
