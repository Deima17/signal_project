package com.cardio_generator.outputs;

import org.java_websocket.WebSocket;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
/**
* Outputs patient data to WebSocket clients in real-time.
* <p>This implementation of {@link OutputStrategy} creates a WebSocket server that broadcasts patient data to all connected clients. WebSocket provides full-duplex communication over a single TCP connection, making it ideal for real-time web-based monitoring dashboards.
* <p>Data is sent as comma-separated values (CSV) strings to all connected clients simultaneously.
 */
public class WebSocketOutputStrategy implements OutputStrategy {

    private WebSocketServer server;
/**
* Constructs a new WebSocket output strategy that listens on the specified port.
* <p>The WebSocket server is created and started immediately, accepting connections from web browsers or other WebSocket clients.
* @param port the port number to listen on (typically between 1024-65535)
*/
    public WebSocketOutputStrategy(int port) {
        server = new SimpleWebSocketServer(new InetSocketAddress(port));
        System.out.println("WebSocket server created on port: " + port + ", listening for connections...");
        server.start();
    }
/**
* Broadcasts a single data record to all connected WebSocket clients.
* <p>The data is formatted as a CSV string: "patientId,timestamp,label,data" and sent to every client currently connected to the WebSocket server.
* @param patientId  the unique identifier of the patient
* @param timestamp  the time when the data was generated (milliseconds since Unix epoch)
* @param label      the type of measurement (e.g., "ECG", "BloodPressure")
* @param data       the actual measurement value or alert status
*/
    @Override
    public void output(int patientId, long timestamp, String label, String data) {
        String message = String.format("%d,%d,%s,%s", patientId, timestamp, label, data);
        // Broadcast the message to all connected clients
        for (WebSocket conn : server.getConnections()) {
            conn.send(message);
        }
    }
/**
* Simple WebSocket server implementation that handles client connections.
* <p>This inner class extends {@link WebSocketServer} and provides basic logging for connection events (open, close, errors).
*/
    private static class SimpleWebSocketServer extends WebSocketServer {
/**
* Creates a new WebSocket server bound to the specified address.
* @param address the socket address (host and port) to bind to
*/
        public SimpleWebSocketServer(InetSocketAddress address) {
            super(address);
        }
 /**
 * Called when a new WebSocket connection is opened.
 * @param conn the WebSocket connection that was opened
 * @param handshake the handshake information from the client
 */
        @Override
        public void onOpen(WebSocket conn, org.java_websocket.handshake.ClientHandshake handshake) {
            System.out.println("New connection: " + conn.getRemoteSocketAddress());
        }
/**
* Called when a WebSocket connection is closed.
* @param conn the WebSocket connection that was closed
* @param code the close code indicating why the connection was closed
* @param reason a human-readable explanation for the closure
* @param remote true if the closure was initiated by the client, false if by the server
*/
        @Override
        public void onClose(WebSocket conn, int code, String reason, boolean remote) {
            System.out.println("Closed connection: " + conn.getRemoteSocketAddress());
        }

        @Override
        public void onMessage(WebSocket conn, String message) {
            // Not used in this context
        }
/**
* Called when an error occurs on the WebSocket connection.
* @param conn the connection on which the error occurred (may be null)
* @param ex the exception that was thrown
*/
        @Override
        public void onError(WebSocket conn, Exception ex) {
            ex.printStackTrace();
        }
/**
* Called when a message is received from a client.
* This implementation does nothing as this server only sends data, but the method must be defined to satisfy the abstract class.
* @param conn the connection that received the message
* @param message the message received from the client
*/
        @Override
        public void onStart() {
            System.out.println("Server started successfully");
        }
    }
}
