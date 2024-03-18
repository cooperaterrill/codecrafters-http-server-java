import java.util.*;
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
      String path = getPath(request);
      String body = getBodyFromPath(path);
      System.out.println("Got body " + body);

      System.out.println("responding...");
      respond(o, body);
      
      System.out.println("wrote response to socket");
      

      serverSocket.close();
      clientSocket.close();
      System.out.println("sockets closed");
    }
    catch (IOException e) {
      System.out.println("IOException: " + e.getMessage());
    }
  }

  public static String getBodyFromPath(String path) {
    String body = path.substring(6);
    return body;
  }

  public static void respond(OutputStream out, String msg) {
    PrintWriter w = new PrintWriter(out);
    w.write(OK + "\r\n");
    w.write("Content-Type: text/plain\r\n");
    w.write("Content-Length: " + msg.length() + "\r\n\r\n");
    w.write(msg + EOF);
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
    int i = 0;
    while((line = r.readLine()) != null && !line.isEmpty()) {
      res.append(line.strip() + "\r\n");
      i++;
    }
    System.out.println("Read " + i + " lines");
    
    return res.toString();
  }
}
