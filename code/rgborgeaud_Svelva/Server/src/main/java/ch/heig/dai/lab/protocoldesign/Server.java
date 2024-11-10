package ch.heig.dai.lab.protocoldesign;

import java.io.*;
import java.net.*;

import static java.nio.charset.StandardCharsets.*;

public class Server {
    private static final int SERVER_PORT = 1234;

    // Possible requests
    private final String COMPUTATION_REQUEST = "ASK";
    private final String DISCONNECT_REQUEST = "BYE";

    // Possible responses
    private final String ERROR_RESPONSE = "ERR";
    private final String SUCCESS_RESPONSE = "RES";

    private final String SUPPORTED_OPERATIONS = "+-*/";

    private final String WELCOME_MESSAGE = """
                        Welcome ! Please use parenthesis around all expressions (e.g. (1*(2+3))
                        Supported operations are : +, -, *, /
                        STOP
                        """;
    private static final String UNKNOWN_REQUEST_MSG = "Unknown request: %s";

    public static void main(String[] args) {
        // Create a new server and run it
        Server server = new Server();
        System.out.println("Starting server...");
        server.run();
        System.out.println("Shutting down server.");
    }

    private void run() {
        // Attempt to create the socket
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            System.out.println("Socket is up. Waiting for connections...");
            // On success, let's loop on the main routine
            // Instantiation of the socket
            final Socket socket = serverSocket.accept();
            while (!socket.isClosed()) {
                try (final BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), UTF_8));   // Try to instantiate input buffer
                     final BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), UTF_8)) // Try to instantiate output buffer
                ){
                    System.out.println("Accepted connection from " + socket.getRemoteSocketAddress());
                    // Welcome message
                    this.writeMessageAndLog(out, WELCOME_MESSAGE);

                    String line;
                    // On input
                    while ((line = in.readLine()) != null) {
                        // Check if this is a valid request
                        final String REQUEST = line.split(" ")[0];
                        switch(REQUEST) {
                            case COMPUTATION_REQUEST: {
                                // Expression evaluation routine
                                System.out.println("Received request: " + line);
                                try {
                                    this.evaluateExpression(out, line);
                                } catch (Exception e) {
                                    // Log error
                                    this.replyError(out, e.getMessage());
                                }
                                break;
                            }
                            case DISCONNECT_REQUEST: {
                                // Disconnect server
                                System.out.println("Client disconnected.");
                                break;
                            }
                            default:
                                // Log unknown query
                                this.replyError(out, String.format(UNKNOWN_REQUEST_MSG, REQUEST));
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Server: socket ex.: " + e);
                } finally {
                    System.out.println("Connection closed.");
                }
            }
        } catch (IOException e) {
            System.out.println("Server: server socket ex.: " + e);
        } finally {
            System.out.println("Socket shut down.");
        }
    }

    private void evaluateExpression(BufferedWriter out, String line) throws Exception {
        // Let's instantiate the request processing class
        final ComputeExpression computeExpression;
        computeExpression = new ComputeExpression(
            line.substring(COMPUTATION_REQUEST.length() + 1),
            SUPPORTED_OPERATIONS);
        // Evaluate
        final String result = computeExpression.compute();
        // Reply success
        this.replySuccess(out, result);
    }

    private void replySuccess(BufferedWriter out, String result) throws IOException {
        this.reply(out, SUCCESS_RESPONSE, result);
    }

    private void replyError(BufferedWriter out, String result) throws IOException {
        this.reply(out, ERROR_RESPONSE, result);
    }

    private void reply(BufferedWriter out, String response, String result) throws IOException {
        this.writeMessageAndLog(out, response + " " + result);
    }

    private void writeMessageAndLog(BufferedWriter out, String message) throws IOException {
        out.write(message + "\n");
        out.flush();
        System.out.println(message);
    }
}
