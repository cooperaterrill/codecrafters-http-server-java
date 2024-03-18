import java.util.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
  public static final String OK = "HTTP/1.1 200 OK\r\n\r\n";
  public static final String NOT_FOUND = "HTTP/1.1 404 Not Found\r\n\r\n";
  public static void main(String[] args) {
    // You can use print statements as follows for debugging, they'll be visible when running tests.
    System.out.println("Logs from your program will appear here!");

    ServerSocket serverSocket = null;
    Socket clientSocket = null;
    //System.out.println("OK request looks like: " + OK);
    try {
      serverSocket = new ServerSocket(4221);
      serverSocket.setReuseAddress(true);
      clientSocket = serverSocket.accept();
      System.out.println("accepted new connection");

      InputStream i = clientSocket.getInputStream();
      OutputStream o = clientSocket.getOutputStream();
      
      System.out.println("reading input...");
      String request = readInputStream(i);
      System.out.println("Got request: " + request);
      String path = getPath(request);

      System.out.println("responding...");
      if (path.equals("/")) {
        respond(o, OK);
      }
      else {
        respond(o, NOT_FOUND);
      }

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

  public static void respond(OutputStream out, String msg) {
    PrintWriter w = new PrintWriter(out);
    w.write(msg);
    w.close();
  }

  public static String getPath(String s) {
    String[] lines = s.split("\r\n");
    String reqLine = lines[0];
    String[] reqLineComponents = reqLine.split(" ");
    return reqLineComponents[1];
  }

  public static String readInputStream(InputStream in) throws IOException {
    BufferedReader r = new BufferedReader(new InputStreamReader(in));
    StringBuffer res = new StringBuffer();

    String line = "";
    while((line = r.readLine()) != null && line.isEmpty()) {
      res.append(line);
    }
    
    return res.toString();
  }
}
