import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Anthony Bosch
 * Tic Tac Toe Client Server
 * Dispatcher
 */

public class Dispatcher {
    static ServerSocket port;

    public static void main(String[] args) throws IOException {
        // use this to create and start up the thread created to handle each user/client's game play
        Socket socket;
        // will be created when a client connects to the ServerSocket and passed along to each server_thread
        ServerThread serverThread;
        ThreadGroup tg1 = new ThreadGroup("Server Thread Group");

        // attach dispatcher object to port 7788
        port = new ServerSocket(7788);

        do {
            // dispatcher block and listen for a connection on port; do this using port.accept()
            // this will block the dispatcher until a client actually makes a connection
            socket = port.accept();
            serverThread = new ServerThread(socket);
            serverThread.start(); // start the server_thread
        } while (true);
    }
}
