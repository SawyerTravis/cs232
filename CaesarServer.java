//
//  Caesar Server 
//  CS232 Client-Server Computing
//
//  Created by Sawyer Travis on 4/17/22.
//  
//  Resources:
//      https://docs.oracle.com/javase/tutorial/networking/sockets/clientServer.html
//      https://www.javatpoint.com/java-get-current-date
//      https://mkyong.com/java/how-to-get-ip-address-in-java/#:~:text=In%20Java%2C%20you%20can%20use,Server%20running%20the%20Java%20app.
//      https://crunchify.com/how-to-get-server-ip-address-and-hostname-in-java/
//      https://stackoverflow.com/questions/19108737/java-how-to-implement-a-shift-cipher-caesar-cipher
//

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;  
import java.util.Date;  
import java.net.InetAddress;
import java.net.UnknownHostException;


public class CaesarServer extends Thread {
    private ServerSocket serverSocket;
    private int port;
    private boolean running = false;
    
    public CaesarServer( int port ) {
        this.port = port;
    }

    public void startServer() {
        try {
            serverSocket = new ServerSocket( port );
            this.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopServer() {
        running = false;
        this.interrupt();
    }

    @Override
    public void run() {
        running = true;
        while( running ) {
            try {
                // accept() to receive the next connection 
                Socket socket = serverSocket.accept();

                // Pass the socket to RequestHandler thread 
                RequestHandler requestHandler = new RequestHandler(socket);
                requestHandler.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main( String[] args ) {
        if (args.length == 0) {
            System.out.println("Usage: CaesarServer <port>");
            System.exit(0);
        }
        int port = Integer.parseInt(args[0]);
        System.out.println("Start server on port: " + port);

        // display server address
        // https://mkyong.com/java/how-to-get-ip-address-in-java/#:~:text=In%20Java%2C%20you%20can%20use,Server%20running%20the%20Java%20app.
        try {
            InetAddress ip = InetAddress.getLocalHost();
            System.out.println("Current Server IP address : " + ip.getHostAddress());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        CaesarServer server = new CaesarServer(port);
        server.startServer();
        System.out.println("\nServer started successfully\n");

        server.stopServer();
    }
}

class RequestHandler extends Thread {

    // method to intialize socket 
    private Socket socket;
    RequestHandler (Socket socket) {
        this.socket = socket;

    }

    // method to handle rotation 
    // Resource: https://stackoverflow.com/questions/19108737/java-how-to-implement-a-shift-cipher-caesar-cipher
    private static String cipher(String str, int shift) {
        StringBuilder strBuilder = new StringBuilder();
        char c;
        int length = str.length();
        for (int i = 0; i < length; i++) {
            c = str.charAt(i);
            // if c is letter ONLY then shift them, else directly add it
            if (Character.isLetter(c)) {
                c = (char) (str.charAt(i) + shift);
                // checking case or range check is important, just if (c > 'z'
                // || c > 'Z')
                // will not work
                if ((Character.isLowerCase(str.charAt(i)) && c > 'z') || (Character.isUpperCase(str.charAt(i)) && c > 'Z')) {

                    c = (char) (str.charAt(i) - (26 - shift));
                }
            }
            strBuilder.append(c);
        }
        return strBuilder.toString();
    }

    @Override
    public void run() {
        try {
            System.out.println("Received a connection");

            // get streams from client 
            BufferedReader in = new BufferedReader( new InputStreamReader( socket.getInputStream() ) );
            PrintWriter out = new PrintWriter( socket.getOutputStream() );

            // output date
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");  
            Date date = new Date();  
            System.out.println("Connection time: " + formatter.format(date));  

            // output IP address 
            // Reference: https://crunchify.com/how-to-get-server-ip-address-and-hostname-in-java/
            InetAddress ip;
            String hostname;
            try {
                ip = InetAddress.getLocalHost();
                hostname = ip.getHostName();
                System.out.println("Client current IP address : " + ip);
                System.out.println("Client current Hostname : " + hostname);
     
            } catch (UnknownHostException e) {
     
                e.printStackTrace();
            }

            String userIn = in.readLine();
            int rotation = 0;
            try {
                rotation = Integer.parseInt(userIn);

                // make sure the user only enters an integer within the correct range
                if (rotation >= 1)  { 
                    if (rotation <= 25) {
                        System.out.println("\nClient: Rotation = " + rotation);
                        out.println(rotation);
                        out.flush();
                    }
                } else {
                    System.out.println("Invalid rotation: connection terminated by server\n");
                    System.out.flush();
                    out.println("invalid");
                    out.flush();

                    // close streams and socket
                    out.close();
                    in.close();
                    socket.close();
                }
            } catch (NumberFormatException e) {     // handle not_an_integer exception
                System.out.println("Invalid rotation: connection terminated by server\n");

                // close streams and socket
                out.close();
                in.close();
                socket.close();
            } 

            // read in line from the user to be rotated  
            String line = in.readLine(); 
            String rotatedLine;

            while (line != null && line.length() > 0) {

                rotatedLine = cipher(line, rotation);   // rotate line

                out.println(rotatedLine);               // output the rotated line to client
                out.flush();
                line = in.readLine();                   // prompt again 
            }

            // close connection 
            in.close();
            out.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}