//
//  Caesar Client 
//  CS232 Client-Server Computing
//
//  Created by Sawyer Travis on 4/17/22.
//      
//  Resources:
//      https://www.codejava.net/java-se/networking/java-socket-client-examples-tcp-ip
//      https://www.geeksforgeeks.org/socket-programming-in-java/
//      https://docs.oracle.com/javase/tutorial/networking/sockets/readingWriting.html
//      https://www.geeksforgeeks.org/compare-two-strings-in-java/#:~:text=Using%20String.,match%2C%20then%20it%20returns%20false.
//      https://www.tutorialspoint.com/java/number_parseint.htm
//      https://www.codegrepper.com/code-examples/java/java+try+catch+integer.parseint
//      
//

import java.net.*;
import java.io.*;

public class CaesarClient {
    public static void main(String[] args) {

        // initial message
        System.out.println("\n****SJT Caesar Cipher Server****\n");

        // get server name
        String server = args[0];
        
        // get port number (must be converted to int)
        int port = Integer.parseInt(args[1]);

        try {
            // https://docs.oracle.com/javase/tutorial/networking/sockets/readingWriting.html
            Socket socket = new Socket(server, port);                    // 1st statement
            System.out.println("Connected");
            PrintWriter out =                                            // 2nd statement
                new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in =                                          // 3rd statement 
                new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            BufferedReader stdIn =                                       // 4th statement 
                new BufferedReader(new InputStreamReader(System.in));

            String userIn;
            
            // get rotation 
            System.out.println("Enter rotation amount: ");
            System.out.flush();
            userIn = stdIn.readLine();
            
            // output to server 
            out.println(userIn); // server will terminate connection if incorrect value

            // read in server response and print to user
            String response = in.readLine();
            if (response == null) {
                System.out.println("Caesar Server: Invalid rotation, connection terminated");

                // close streams and socket
                out.close();
                in.close();
                stdIn.close();
                socket.close();

                // exit 
                System.exit(0);

            } else {
                System.out.println("Caesar Server: " + response + "\n");    // print response if correct value for rotation
            }

            // begin reading string inputs from user
            while ((userIn) != null) {
                // prompt user for text in next while loop
                System.out.println("Enter text for rotation: ");
                System.out.flush();
                userIn = stdIn.readLine();

                // exit program if "quit" is entered
                // Reference: https://www.geeksforgeeks.org/compare-two-strings-in-java/#:~:text=Using%20String.,match%2C%20then%20it%20returns%20false.
                if (userIn.equals("quit")) {
                    System.out.println("****Quitting SJT Server****");
                    break;
                }

                // output to server 
                out.println(userIn);

                // read in server response and print to user
                System.out.println("Caesar Server: " + in.readLine() + "\n");
            }

            // close streams and socket
            out.close();
            in.close();
            stdIn.close();
            socket.close();

            // exit 
            System.exit(0);

        } catch (IOException exc) {     // handle error exception 
            System.out.println("I/O error: " + exc.getMessage());
        }
    }
}
