public class TCPServer extends Thread {
    int port;
    public TCPServer(int port) {
        this.port = port;
    }
    
    public void run() {
        ClientHandler client = new ClientHandler(port);
        client.start();
    }
}
