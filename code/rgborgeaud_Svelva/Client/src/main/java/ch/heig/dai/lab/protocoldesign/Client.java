package ch.heig.dai.lab.protocoldesign;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Client {
    final String SERVER_ADDRESS = "localhost";
    final int SERVER_PORT = 1234;

    private final String COMPUTATION_REQUEST = "ASK";
    private final String ERROR_RESPONSE = "ERR";
    private final String SUCCESS_RESPONSE = "RES";
    private final String DISCONNECT_SIGNAL = "BYE";

    public static void main(String[] args) {
        // Create a new client and run it
        Client client = new Client();
        System.out.println("Starting client...");
        client.run();
        System.out.println("Shutting down client.");
    }

    private void run() {
        try (final Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);                                                             // Try to create the socket
             final BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));      // Try to create an input buffer
             final BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));   // Try to create an output buffer
             final Scanner sin = new Scanner(System.in)) {                                                                              // Try to create a terminal input scanner

            System.out.println("Connected to " + socket.getRemoteSocketAddress() + "\n");

            String line;

            // Display server's welcome message
            while(!(line = in.readLine()).equals("STOP")) {
                System.out.println(line);
            }

            System.out.println("Please enter expression to compute or 'q' to quit");

            while(!(line = sin.nextLine()).equals("q")) {
                // Send a request for a computation
                this.requestComputation(out, line);

              do {
                line = in.readLine();
              } while (line == null || line.isEmpty());

                // Tokenize the line
                String[] array = {
                    line.substring(0, line.indexOf(" ")),
                    line.substring(line.indexOf(" ") + 1)
                };

                // Process response
                switch(array[0]) {
                    case SUCCESS_RESPONSE : {
                        System.out.println("Result : " + array[1]);
                        break;
                    }
                    case ERROR_RESPONSE : {
                        System.out.println("Error : " + array[1]);
                        break;
                    }

                }
            }
            // Send disconnect signal
            this.sendShutDown(out);

        } catch (IOException e) {
            System.out.println("Client exc: " + e);
        } finally {
            System.out.println("Socket shut down.");
        }
    }

    private void requestComputation(BufferedWriter out, String expression) throws IOException {
        this.sendMessage(out, COMPUTATION_REQUEST + " " + expression);
    }

    private void sendShutDown(BufferedWriter out) throws IOException {
        this.sendMessage(out, DISCONNECT_SIGNAL);
        System.out.println("Closing connection to server.");
    }

    private void sendMessage(BufferedWriter out, String message) throws IOException {
        out.write(message + "\n");
        out.flush();
    }
}
