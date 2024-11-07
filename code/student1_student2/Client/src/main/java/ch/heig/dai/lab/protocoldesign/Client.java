package ch.heig.dai.lab.protocoldesign;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Client {
    final String SERVER_ADDRESS = "localhost";
    final int SERVER_PORT = 1234;

    public static void main(String[] args) {
        // Create a new client and run it
        Client client = new Client();
        client.run();
    }

    private void run() {

        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             var in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             var out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
             var sin = new Scanner(System.in);) {

            String line;

            //Display intro text from server
            while(!(line = in.readLine()).equals("STOP")) {
                System.out.println(line);
            }

            System.out.println("Please enter expression to compute or 'q' to quit");

            while(!(line = sin.nextLine()).equals("q")) {

                out.write("ASK " + line + "\n");
                out.flush();

                line = in.readLine();
                String[] array = line.split(" ");

                switch(array[0]) {
                    case "RES" : {
                        System.out.println("Result : " + array[1]);
                        break;
                    }
                    case "E0" : {
                        System.out.println("Arithmetic exception : division by 0");
                        break;
                    }
                    case "E1" : {
                        System.out.println("Illegal character exception");
                        break;
                    }
                    case "E2" : {
                        System.out.println("Malformed expression");
                    }

                }
            }


        } catch (IOException e) {
            System.out.println("Client exc: " + e);
        }
    }
}