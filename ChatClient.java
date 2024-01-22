import java.net.*;
import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ChatClient extends JFrame {
    private Socket socket = null;
    private DataInputStream console = null;
    private DataOutputStream streamOut = null;
    private ChatClientThread client = null;
    private JTextArea display = new JTextArea();
    private JTextField input = new JTextField();
    private JButton send = new JButton("Send"), connect = new JButton("Connect"), quit = new JButton("Bye");
    private String serverName = "localhost";
    private int serverPort = 4444;

    public ChatClient() {
        JPanel keys = new JPanel();
        keys.setLayout(new GridLayout(1, 2));
        keys.add(quit);
        keys.add(connect);
        JPanel south = new JPanel();
        south.setLayout(new BorderLayout());
        south.add("West", keys);
        south.add("Center", input);
        south.add("East", send);
        JLabel title = new JLabel("Simple Chat Client Applet", JLabel.CENTER);
        title.setFont(new Font("Helvetica", Font.BOLD, 14));
        setLayout(new BorderLayout());
        add("North", title);
        add("Center", display);
        add("South", south);
        quit.setEnabled(false);
        send.setEnabled(false);

        quit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                input.setText(".bye");
                send();
                quit.setEnabled(false);
                send.setEnabled(false);
                connect.setEnabled(true);
            }
        });

        connect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                connect(serverName, serverPort);
            }
        });

        send.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                send();
                input.requestFocus();
            }
        });
    }

    public void connect(String serverName, int serverPort) {
        println("Establishing connection. Please wait ...");
        try {
            socket = new Socket(serverName, serverPort);
            println("Connected: " + socket);
            open();
            send.setEnabled(true);
            connect.setEnabled(false);
            quit.setEnabled(true);
        } catch (UnknownHostException uhe) {
            println("Host unknown: " + uhe.getMessage());
        } catch (IOException ioe) {
            println("Unexpected exception: " + ioe.getMessage());
        }
    }

    private void send() {
        try {
            streamOut.writeUTF(input.getText());
            streamOut.flush();
            input.setText("");
        } catch (IOException ioe) {
            println("Sending error: " + ioe.getMessage());
            close();
        }
    }

    public void handle(String msg) {
        if (msg.equals(".bye")) {
            println("Good bye. Press RETURN to exit ...");
            close();
        } else println(msg);
    }

    public void open() {
        try {
            streamOut = new DataOutputStream(socket.getOutputStream());
            client = new ChatClientThread(this, socket);
        } catch (IOException ioe) {
            println("Error opening output stream: " + ioe);
        }
    }

    public void close() {
        try {
            if (streamOut != null) streamOut.close();
            if (socket != null) socket.close();
        } catch (IOException ioe) {
            println("Error closing ...");
        }
        client.close();
        client.stopThread();
    }


    private void println(String msg) {
        display.append(msg + "\n");
    }

    public void stopThread() {
        if (client != null) {
            client.stopThread();
        }
    }

    public static void main(String[] args) {
        // You could add code here to get serverName and serverPort from command-line arguments if you want
        ChatClient chatClient = new ChatClient();
        chatClient.setSize(400, 400);
        chatClient.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        chatClient.setVisible(true);
    }
}
