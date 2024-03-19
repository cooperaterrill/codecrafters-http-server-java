import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
  public static final String OK = "HTTP/1.1 200 OK";
  public static final String NOT_FOUND = "HTTP/1.1 404 Not Found";
  public static final String EOF = "\r\n\r\n";
  public static void main(String[] args) {
    // You can use print statements as follows for debugging, they'll be visible when running tests.
    System.out.println("Logs from your program will appear here!");



    while (true) {
      try {
        ServerSocket serverSocket = new ServerSocket(4221);
        serverSocket.setReuseAddress(true);
        Socket clientSocket = serverSocket.accept();
        System.out.println("accepted new connection");

        ClientHandler client = new ClientHandler(clientSocket);
        System.out.println("Initiated new client handler");

        client.processRequest();
        System.out.println("Request processed");

        client.respond();
        System.out.println("Response sent");
      }
      catch (IOException e) {
        System.out.println("IOException: " + e.getMessage());
      }
    }

  }

  
}
