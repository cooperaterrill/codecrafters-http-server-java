import java.util.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
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
      InputStream in = clientSocket.getInputStream();
      OutputStream out = clientSocket.getOutputStream();
      Scanner scn = new Scanner(in);
      PrintWriter w = new PrintWriter(out);
      
      byte[] input = in.readAllBytes();
      String s = new String(input);
      
      /*
      String s = scn.nextLine();
      while (s != null) {
        s = scn.nextLine();
      }
      */
      //w.write("HTTP/1.1 200 OK\r\n\r\n");
      scn.close();
      w.close();
      in.close();
      out.close();

    }
    catch (IOException e) {
      System.out.println("IOException: " + e.getMessage());
    }
  }
}
