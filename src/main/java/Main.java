import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Main {
  public static final String OK = "HTTP/1.1 200 OK";
  public static final String NOT_FOUND = "HTTP/1.1 404 Not Found";
  public static final String EOF = "\r\n\r\n";
  public static void main(String[] args) {
    HashMap<String, String> arguments = new HashMap<>();
    for (int i = 0; i < args.length-1; i+=2) {
      arguments.put(args[i].substring(2), args[i+1]);
    }
    System.out.println("Got directory: " + arguments.get("directory"));

    // You can use print statements as follows for debugging, they'll be visible when running tests.
    System.out.println("Logs from your program will appear here!");

    /*
    for (int i = 0; i < 3; i++) {
      //ClientHandler client = new ClientHandler(4221);
      new Thread(new ClientHandler(4221)).start();
    }
    */
    ServerSocket serverSocket = null;
    try {
      serverSocket = new ServerSocket(4221);
      serverSocket.setReuseAddress(true);

      while (true) {
        Socket clientSocket = serverSocket.accept();
        System.out.println("\n\nACCEPTED NEW CONNECTION");
        new Thread(new ClientHandler(clientSocket, arguments.get("directory"))).start();
      }
    }


    catch (IOException e) {
      System.out.println("IOException: " + e.getMessage());
    }


    finally {
      try {
        serverSocket.close();
      }
      catch (Exception e) {
        System.out.println("Failed to close server socket: " + e.getMessage());
      }
    }
    

  }

  
}
