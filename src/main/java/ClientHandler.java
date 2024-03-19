import java.io.*;
import java.net.*;
public class ClientHandler extends Thread {
    public static final String OK = "HTTP/1.1 200 OK";
    public static final String NOT_FOUND = "HTTP/1.1 404 Not Found";
    public static final String EOF = "\r\n\r\n";

    int port;
    InputStream in;
    OutputStream out;
    String request;

    public ClientHandler(int port) {
        this.port = port;
    }
    /*
    public ClientHandler(Socket clientSocket) throws IOException {
        in = clientSocket.getInputStream();
        out = clientSocket.getOutputStream();
    }
    */

    public void run() {
        try {
            System.out.println("Thread " + Thread.currentThread().getName() + " has begun");
            ServerSocket serverSocket = new ServerSocket(port);
            serverSocket.setReuseAddress(true);
            Socket clientSocket = serverSocket.accept();
            in = clientSocket.getInputStream();
            out = clientSocket.getOutputStream();   
            System.out.println("accepted new connection");

            this.processRequest();
            System.out.println("Request processed");
            System.out.println("Got request\n---------\n" + this.request);

            this.respond();
            System.out.println("Response sent\n");
            serverSocket.close();
        }
        catch (IOException e) {
            System.out.println("IOExcpetion: " + e.getMessage());
        }
    }
    public void processRequest() throws IOException {
        BufferedReader r = new BufferedReader(new InputStreamReader(in));
        StringBuffer res = new StringBuffer();

        String line;
        int i = 0;
        while ((line = r.readLine()) != null && !line.isEmpty()) {
            res.append(line.strip() + "\r\n");
            i++;
        }
        System.out.println("Processed " + i + " lines");
        request = res.toString();
    }

    public String getPath() {
        String[] lines = request.split("\r\n");
        return lines[0].split(" ")[1].strip();
    }

    public String getUserAgent() {
        String[] lines = request.split("\r\n");
        for (String line : lines) {
            if (line.length() >= 12 && line.substring(0,12).equals("User-Agent: ")) {
                return line.substring(12).strip();
            }
        }
    
        return null; //no user agent found 
    }

    public void respond() throws IOException {
        String path = this.getPath();
        System.out.println("Got path: " + path);
        PrintWriter w = new PrintWriter(out);

        if (path.equals("/")) {
            System.out.println("Got path /, responding OK");
            w.write(OK + EOF);
        }
        else if (path.length() >= 6 && path.substring(0,6).equals("/echo/")) {
            System.out.println("Got path /echo/, responding with inner path");
            String body = path.substring(6);
            w.write(OK + "\r\n");
            w.write("Content-Type: text/plain\r\n");
            w.write("Content-Length: " + body.length() + "\r\n\r\n");
            w.write(body + EOF);
        }
        else if (path.equals("/user-agent")) {
            System.out.println("Got path /user-agent, responding with user agent");
            String userAgent = this.getUserAgent();
            w.write(OK + "\r\n");
            w.write("Content-Type: text/plain\r\n");
            w.write("Content-Length: " + userAgent.length() + "\r\n\r\n");
            w.write(userAgent + EOF);
        }
        else {
            System.out.println("Got invalid path");
            w.write(NOT_FOUND + EOF);
        }

        w.close();
    }

}
