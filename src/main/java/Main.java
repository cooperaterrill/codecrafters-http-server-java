import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
  public static final String OK = "HTTP/1.1 200 OK";
  public static final String NOT_FOUND = "HTTP/1.1 404 Not Found";
  public static final String EOF = "\r\n\r\n";
  public static void main(String[] args) {
    // You can use print statements as follows for debugging, they'll be visible when running tests.
    System.out.println("Logs from your program will appear here!");

    /*
    for (int i = 0; i < 3; i++) {
      //ClientHandler client = new ClientHandler(4221);
      new Thread(new ClientHandler(4221)).start();
    }
    */
    
    try {
      ServerSocket serverSocket = new ServerSocket(4221);
      serverSocket.setReuseAddress(true);

      while (true) {
        Socket clientSocket = serverSocket.accept();
        System.out.println("Accepted new connection -main");
        new Thread(new ClientHandler(clientSocket)).start();
      }
    }
    catch (IOException e) {
      System.out.println("IOException: " + e.getMessage());
    }
    

  }

  
}
