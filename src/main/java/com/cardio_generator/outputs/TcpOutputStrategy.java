package com.cardio_generator.outputs;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;

/**
* Outputs patient data over TCP network connections.
* <p>This implementation of {@link OutputStrategy} creates a TCP server socket that listens for client connections. 
* Once a client connects, data is sent as comma-separated values (CSV) strings over the socket connection.
* <p>This is useful for streaming real-time patient data to remote monitoring systems or dashboards.
*/
public class TcpOutputStrategy implements OutputStrategy {

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
/**
* Constructs a new TCP output strategy that listens on the specified port.
* <p>The server socket is created immediately, and client connections are accepted asynchronously in a separate thread to avoid blocking.
* @param port the TCP port number to listen on (must be available and typically between 1024-65535)
* @throws RuntimeException if the server socket cannot be created (wraps the original IOException)
*/
    public TcpOutputStrategy(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("TCP Server started on port " + port);

            // Accept clients in a new thread to not block the main thread
            Executors.newSingleThreadExecutor().submit(() -> {
                try {
                    clientSocket = serverSocket.accept();
                    out = new PrintWriter(clientSocket.getOutputStream(), true);
                    System.out.println("Client connected: " + clientSocket.getInetAddress());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
/**
* Sends a single data record to the connected TCP client.
* <p>The data is formatted as a CSV string: "patientId,timestamp,label,data"
* If no client is currently connected, the data is silently discarded.
* @param patientId  the unique identifier of the patient
* @param timestamp  the time when the data was generated (milliseconds since Unix epoch)
* @param label      the type of measurement (e.g., "ECG", "BloodPressure")
* @param data       the actual measurement value or alert status
*/
    @Override
    public void output(int patientId, long timestamp, String label, String data) {
        if (out != null) {
            String message = String.format("%d,%d,%s,%s", patientId, timestamp, label, data);
            out.println(message);
        }
    }
}
