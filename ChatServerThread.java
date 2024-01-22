import java.net.*;
import java.io.*;

public class ChatServerThread extends Thread {
    private Socket socket = null;
    private ChatServer server = null;
    private int ID = -1;
    private DataInputStream streamIn = null;
    private volatile boolean running = true;

    public ChatServerThread(ChatServer _server, Socket _socket) {
        server = _server;
        socket = _socket;
        ID = socket.getPort();
    }

    public void run() {
        System.out.println("Server Thread " + ID + " running.");
        while (running) {
            try {
                System.out.println(streamIn.readUTF());
            } catch (IOException ioe) {
                System.out.println("Error: " + ioe.getMessage());
                running = false;
            }
        }
        // Close resources after running
        try {
            close();
        } catch (IOException ioe) {
            System.out.println("Error closing resources: " + ioe.getMessage());
        }
    }

    public void open() throws IOException {
        streamIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
    }

    public void close() throws IOException {
        if (socket != null) socket.close();
        if (streamIn != null) streamIn.close();
    }

    public void stopThread() {
        running = false;
    }
}
