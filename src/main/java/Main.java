import java.util.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
  public static final String OK = "HTTP\1.1 200 OK\r\n\r\n";
  public static void main(String[] args) {
    // You can use print statements as follows for debugging, they'll be visible when running tests.
    System.out.println("Logs from your program will appear here!");

    ServerSocket serverSocket = null;
    Socket clientSocket = null;

    try {
      serverSocket = new ServerSocket(4221);
      serverSocket.setReuseAddress(true);
      clientSocket = serverSocket.accept();
      System.out.println("accepted new connection");

      InputStream i = clientSocket.getInputStream();
      OutputStream o = clientSocket.getOutputStream();
      
      System.out.println("reading input...");
      byte[] input = i.readAllBytes();
      String s = new String(input);

      System.out.println("responding...");
      
      o.write(OK.getBytes());
      o.flush();
      System.out.println("wrote response to socket");
      /*
      String s = scn.nextLine();
      while (s != null) {
        s = scn.nextLine();
      }
      */
      //w.write("HTTP/1.1 200 OK\r\n\r\n");

      serverSocket.close();
      clientSocket.close();
      System.out.println("sockets closed");
    }
    catch (IOException e) {
      System.out.println("IOException: " + e.getMessage());
    }
  }
}
