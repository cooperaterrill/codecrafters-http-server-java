import java.io.*;
import java.net.*;
import java.util.HashMap;
public class ClientHandler implements Runnable {
    public static final String OK = "HTTP/1.1 200 OK";
    public static final String NOT_FOUND = "HTTP/1.1 404 Not Found";
    public static final String EOF = "\r\n\r\n";
    public static final String POST = "HTTP/1.1 201 Created";

    int port;
    InputStream in;
    OutputStream out;
    String request;
    String directory;
    HashMap<String, String> headers;
    String path;
    String method;
    String version;
    String body;
    /*
    public ClientHandler(int port) {
        this.port = port;
    }
    */

    public ClientHandler(Socket clientSocket, String directory) throws IOException {
        in = clientSocket.getInputStream();
        out = clientSocket.getOutputStream();
        this.directory = directory;
        headers = new HashMap<>();
    }
    

    public void run() {
        try {
            System.out.println("Thread " + Thread.currentThread().getName() + " has begun");
            /*
            ServerSocket serverSocket = new ServerSocket(port);
            serverSocket.setReuseAddress(true);
            Socket clientSocket = serverSocket.accept();
            in = clientSocket.getInputStream();
            out = clientSocket.getOutputStream();
            */
            //System.out.println("accepted new connection");

            this.readRequest();
            System.out.println("Request read");
            System.out.println("Got request\n---------\n" + this.request + "\n---------");

            this.processRequest();
            System.out.println("Request fields processed");

            this.respond();
            System.out.println("Response sent\n");
            //serverSocket.close();
        }
        catch (IOException e) {
            System.out.println("IOExcpetion: " + e.getMessage());
        }
    }

    public void processRequest() {
        String[] lines = request.split("\r\n");
        String[] reqLine = lines[0].split(" ");
        method = reqLine[0];
        path = reqLine[1];
        version = reqLine[2];
        
        for (int i = 1; i < lines.length; i++) {
            int separator = lines[i].indexOf(':');
            String header = lines[i].substring(0, separator);
            String val = lines[i].substring(separator+2); //dont count colon or space
            headers.put(header, val);
            System.out.println("Mapped header " + header + " to value " + val);
        }
    }

    public void readRequest() throws IOException {
        BufferedReader r = new BufferedReader(new InputStreamReader(in));
        StringBuffer res = new StringBuffer();
        StringBuffer body = new StringBuffer();
        int contentLength = -1;

        String line = r.readLine();
        int i = 0;
        while (!line.isEmpty() && line != null) {
            if (line.length() >= 16 && line.substring(0, 16).equals("Content-Length: ")) {
                contentLength = Integer.parseInt(line.substring(16));
            }
            res.append(line + "\r\n");
            line = r.readLine();
            i++;
        }
        System.out.println("Processed " + i + " lines of headers");
        System.out.println("Got content length of " + contentLength);
        if (contentLength != -1) {
            for (int j = 0; j < contentLength; j++) {
                //System.out.println("j= " + j);
                body.append((char)(r.read()));
            }
        }
        System.out.println("Got body: " + body);
        this.body = body.toString();
        request = res.toString();
    }

    public void respond() throws IOException {
        System.out.println("Got path: " + path);
        PrintWriter w = new PrintWriter(out);

        if (method.equals("POST")) {
            String filePath = directory + path.substring(7);
            File file = new File(filePath);
            FileWriter fileWriter = new FileWriter(file);

            System.out.println(file.createNewFile() ? "New file created" : "File already exists");

            System.out.println("Writing body: " + body);
            fileWriter.write(body);
            fileWriter.close();

            System.out.println("POST complete");
            w.write(POST + EOF);

        }
        else { //get request
            if (path.equals("/")) { //status request
                System.out.println("Got path /, responding OK");
                w.write(OK + EOF);
            }
            else if (path.length() >= 6 && path.substring(0,6).equals("/echo/")) { //echo request
                System.out.println("Got path /echo/, responding with inner path");
                String body = path.substring(6);
                w.write(OK + "\r\n");
                w.write("Content-Type: text/plain\r\n");
                w.write("Content-Length: " + body.length() + "\r\n\r\n");
                w.write(body + EOF);
            }
            else if (path.equals("/user-agent")) { //user agent reqeust
                System.out.println("Got path /user-agent, responding with user agent");
                String userAgent = headers.get("User-Agent");
                w.write(OK + "\r\n");
                w.write("Content-Type: text/plain\r\n");
                w.write("Content-Length: " + userAgent.length() + "\r\n\r\n");
                w.write(userAgent + EOF);
            }
            else if (path.length() >= 7 && path.substring(0, 7).equals("/files/")) { //file request
                System.out.println("Got request for file");
                String filePath = directory + path.substring(7);
                if (new File(filePath).exists()) {
                    File file = new File(filePath);
                    System.out.println("File exists, responding with data");
                    /*
                    w.write(OK + "\r\n");
                    w.write("Content-Type: application/octet-stream\r\n");
                    w.write("Content-Length: " + file.toString().length() + "\r\n\r\n");
                    */
    
                    FileInputStream r = new FileInputStream(file);
                    byte[] contents = r.readAllBytes();
                    r.close();
    
                    //we will use an output stream to write bytes
                    out.write((OK+"\r\n"+"Content-Type: application/octet-stream\r\nContent-Length: " + contents.length + "\r\n\r\n" + new String(contents)).getBytes());
    
                    out.write((EOF).getBytes());
                    
                }
                else { //file not found
                    System.out.println("File does not exist, responding 404");
                    w.write(NOT_FOUND + EOF);
                }
            }
            
            else { //bad path request
                System.out.println("Got invalid path (404)");
                w.write(NOT_FOUND + EOF);
            }
        }
        
        w.close();
    }

}
