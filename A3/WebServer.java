
/**
 * WebServer Class
 * 
 * Implements a multi-threaded web server
 * supporting non-persistent connections.
 * 
 */

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.*;

public class WebServer extends Thread {

    // global logger object, configures in the driver class
    private static final Logger logger = Logger.getLogger("WebServer");
    // private Socket socket;
    private ServerSocket serverSocket;
    private boolean shutdown = false;
    private ExecutorService pool;
    private int timeout;

    /**
     * Constructor to initialize the web server
     * 
     * @param port    The server port at which the web server listens > 1024
     * @param timeout The timeout value for detecting non-resposive clients, in
     *                milli-second units
     * 
     */
    public WebServer(int port, int timeout) {
        if ((port > 1024) && (port < 65536)) {
            try {
                this.timeout = timeout;
                // open the server socket
                serverSocket = new ServerSocket(port);
                // set socket timeout option using ServerSocket.setSoTimeout(100)
                serverSocket.setSoTimeout(100);
                pool = Executors.newFixedThreadPool(10);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            System.out.println("Enter port number from 1024 to 65536 only");
            System.exit(0);
        }
    }

    /**
     * Main web server method.
     * The web server remains in listening mode
     * and accepts connection requests from clients
     * until the shutdown method is called.
     *
     */
    public void run() {
        try {

            while (!shutdown) {
                try {
                    // Listen for connection requests from clients
                    // Accept a new connection request
                    Socket socket = serverSocket.accept();

                    // Spawn a worker thread to handle the new connection
                    WorkerThread thread = new WorkerThread(socket, timeout);
                    pool.execute(thread);
                } catch (SocketTimeoutException e) {
                    
                }
            }

            // shutdown the executor
            try {
                // do not accept any new tasks
                pool.shutdown();
                // wait 5 seconds for existing tasks to terminate
                if (!pool.awaitTermination(5, TimeUnit.SECONDS)) {
                    pool.shutdownNow(); // cancel currently executing tasks
                }
            } catch (InterruptedException e) {
                // cancel currently executing tasks
                pool.shutdownNow();
            }

            serverSocket.close();
        } catch (SocketTimeoutException e) {

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Signals the web server to shutdown.
     *
     */
    public void shutdown() {
        shutdown = true;
    }
}
