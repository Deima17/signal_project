package com.data_management;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * WebSocket client that connects to a WebSocket server and receives
 * real-time patient data, parsing and storing it into DataStorage.
 * Implements DataReader for integration with the existing system.
 */
public class WebSocketClientImpl extends WebSocketClient implements DataReader {

    private DataStorage dataStorage;

    /**
     * Constructs a WebSocketClientImpl that connects to the given URI.
     *
     * @param serverUri the URI of the WebSocket server to connect to
     * @param dataStorage the storage where received data will be stored
     */
    public WebSocketClientImpl(URI serverUri, DataStorage dataStorage) {
        super(serverUri);
        this.dataStorage = dataStorage;
    }

    /**
     * Called when the connection to the WebSocket server is established.
     *
     * @param handshake the server handshake information
     */
    @Override
    public void onOpen(ServerHandshake handshake) {
        System.out.println("Connected to WebSocket server");
    }

    /**
     * Called when a message is received from the server.
     * Parses the CSV message and stores the data in DataStorage.
     * Expected format: "patientId,timestamp,label,data"
     *
     * @param message the message received from the server
     */
    @Override
    public void onMessage(String message) {
        try {
            String[] parts = message.split(",");
            if (parts.length != 4) {
                System.err.println("Skipping malformed message: " + message);
                return;
            }
            int patientId = Integer.parseInt(parts[0].trim());
            long timestamp = Long.parseLong(parts[1].trim());
            String label = parts[2].trim();
            double data = Double.parseDouble(parts[3].trim());
            dataStorage.addPatientData(patientId, data, label, timestamp);
        } catch (Exception e) {
            System.err.println("Error parsing message: " + message + " - " + e.getMessage());
        }
    }

    /**
     * Called when the WebSocket connection is closed.
     *
     * @param code the close code
     * @param reason the reason for closure
     * @param remote true if closed by the server
     */
    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Disconnected from WebSocket server. Reason: " + reason);
    }

    /**
     * Called when an error occurs on the connection.
     *
     * @param ex the exception that was thrown
     */
    @Override
    public void onError(Exception ex) {
        System.err.println("WebSocket error: " + ex.getMessage());
    }

    /**
     * Not used for WebSocket-based reading.
     * Use readData(DataStorage, String) instead.
     *
     * @param dataStorage the storage where data will be stored
     * @throws UnsupportedOperationException always
     */
    @Override
    public void readData(DataStorage dataStorage) throws IOException {
        throw new UnsupportedOperationException("Use readData(DataStorage, String uri) for WebSocket reading");
    }

    /**
     * Connects to the WebSocket server at the given URI and starts
     * receiving real-time data into DataStorage.
     *
     * @param dataStorage the storage where data will be stored
     * @param uri the WebSocket server URI e.g. "ws://localhost:8080"
     * @throws IOException if the connection cannot be established
     */
    @Override
    public void readData(DataStorage dataStorage, String uri) throws IOException {
        this.dataStorage = dataStorage;
        try {
            connectBlocking();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("WebSocket connection interrupted", e);
        }
    }

    /**
     * Factory method to create and return a connected WebSocketClientImpl.
     *
     * @param uri the WebSocket server URI string e.g. "ws://localhost:8080"
     * @param dataStorage the storage to use
     * @return a connected WebSocketClientImpl
     * @throws IOException if connection fails
     * @throws URISyntaxException if the URI string is invalid
     */
    public static WebSocketClientImpl connect(String uri, DataStorage dataStorage)
            throws IOException, URISyntaxException {
        WebSocketClientImpl client = new WebSocketClientImpl(new URI(uri), dataStorage);
        client.readData(dataStorage, uri);
        return client;
    }
}