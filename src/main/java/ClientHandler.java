import java.io.*;
import java.net.*;
public class ClientHandler {
    public static final String OK = "HTTP/1.1 200 OK";
    public static final String NOT_FOUND = "HTTP/1.1 404 Not Found";
    public static final String EOF = "\r\n\r\n";

    InputStream in;
    OutputStream out;
    String request;

    public ClientHandler(Socket clientSocket) throws IOException {
        in = clientSocket.getInputStream();
        out = clientSocket.getOutputStream();
    }

    public void processRequest() throws IOException {
        BufferedReader r = new BufferedReader(new InputStreamReader(in));
        StringBuffer res = new StringBuffer();

        String line;
        while ((line = r.readLine()) != null && !line.isEmpty()) {
            res.append(line);
        }

        request = res.toString();
        in.close();
    }

    public String getPath() {
        String[] lines = request.split("\r\n");
        return lines[0].split(" ")[1];
    }

    public String getUserAgent() {
        String[] lines = request.split("\r\n");
        for (String line : lines) {
            if (line.length() >= 12 && line.substring(0,12).equals("User-Agent: ")) {
                return line.substring(12).trim();
            }
        }
    
        return null; //no user agent found 
    }

    public void respond() throws IOException {
        String path = this.getPath();
        PrintWriter w = new PrintWriter(out);

        if (path.equals("/")) w.write(OK + EOF);
        else if (path.length() >= 6 && path.substring(0,6).equals("/echo/")) {
            String body = path.substring(6);
            w.write(OK + "\r\n");
            w.write("Content-Type: text/plain\r\n");
            w.write("Content-Length: " + body.length() + "\r\n\r\n");
            w.write(body + EOF);
        }
        else if (path.equals("/user-agent")) {
            String userAgent = this.getUserAgent();
            w.write(OK + "\r\n");
            w.write("Content-Type: text/plain\r\n");
            w.write("Content-Length: " + userAgent.length() + "\r\n\r\n");
            w.write(userAgent + EOF);
        }

        w.close();
        out.close();
    }

}
