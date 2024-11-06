package ch.heig.dai.lab.protocoldesign;

import java.io.*;
import java.net.*;
import static java.nio.charset.StandardCharsets.*;

public class Server {
    final int SERVER_PORT = 1234;
    final String COMPUTE_MESSAGE = "ASK";
    static final String SUPPORTED_OPERATIONS = "+-*/";

    public static void main(String[] args) {
        // Create a new server and run it
        Server server = new Server();
        server.run();
    }

    private void run() {
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            while (true) {

                try (Socket socket = serverSocket.accept();
                    var in = new BufferedReader(new InputStreamReader(socket.getInputStream(), UTF_8));
                    var out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), UTF_8))) {

                    out.write("Welcome ! Please use parenthesis around all expressions (e.g. (1*(2+3))\n" +
                            "Supported operations are : +, -, *, /\nSTOP\n");
                    out.flush();

                    String line;

                    while ((line = in.readLine()) != null) {

                        String[] strArray = line.split("\\s+");

                        switch (strArray[0]) {
                            case COMPUTE_MESSAGE :
                                try {
                                    var res = new ComputeExpression(line.substring(4), SUPPORTED_OPERATIONS);
                                    out.write("RES " + res + "\n");
                                    out.flush();
                                } catch (ArithmeticException e) {
                                    out.write("E0\n");
                                    out.flush();
                                } catch (IllegalCharacter e) {
                                    out.write("E1\n");
                                    out.flush();
                                } catch (MalformedExpression e) {
                                    out.write("E2\n");
                                    out.flush();
                                }
                                break;

                            default :
                                break;
                        }
                    }

                } catch (IOException e) {
                    System.out.println("Server: socket ex.: " + e);
                }
            }

        } catch (IOException e) {
            System.out.println("Server: server socker ex.: " + e);
        }
    } 
}