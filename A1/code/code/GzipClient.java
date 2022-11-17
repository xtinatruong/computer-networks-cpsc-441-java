
/**
 * GzipClient Class
 * 
 * CPSC 441
 * Assignment 1
 * UCID: 30064426
 * Student Name: Hoang Truong 
 *
 */
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.*;

public class GzipClient {
	private static final Logger logger = Logger.getLogger("GzipClient"); // global logger
	/**
	 * socket that connects to server
	 */
	private Socket socket;
	/**
	 * beffer size for i/o streams
	 */
	private int bufferSize;

	/**
	 * Constructor to initialize the class.
	 * 
	 * To Do: you should implement this method.
	 * 
	 * @param serverName remote server name
	 * @param serverPort remote server port number
	 * @param bufferSize buffer size used for read/write
	 */
	public GzipClient(String serverName, int serverPort, int bufferSize) {
		try {
			// create socket object
			socket = new Socket(serverName, serverPort);
			// set local buffer size as specified size
			this.bufferSize = bufferSize;
		} catch (NumberFormatException e) {
			System.out.println("Bad port format. Please enter a number.\n");
			e.printStackTrace();
			System.exit(2);
		} catch (UnknownHostException e) {
			System.out.println("Cannot find the specified host. Please check host name...\n");
			e.printStackTrace();
			System.exit(3);
		} catch (IOException e) {
			System.out.println(" >Cannot connect with server...");
			System.out.println(" >Please check if port number corresponds to port number in server...");
			System.out.println(" >Possibly server is not operating. Please try again later...\n");
			e.printStackTrace();
			System.exit(4);
		} catch (IllegalArgumentException e) {
			System.out.println("Please enter a number between 0 and 65535 for the port.\n");
			e.printStackTrace();
			System.exit(2);
		}
	}

	/**
	 * Compress the specified file using the remote server.
	 * 
	 * To Do: you should implement this method.
	 * 
	 * @param inName  name of the input file to be compressed
	 * @param outName name of the output compressed file
	 */
	public void gzip(String inName, String outName) {
		try {
			//create writing and reading threads
			WriteToServer w = new WriteToServer(inName, socket, bufferSize);
			ReadFromServer r = new ReadFromServer(outName, socket, bufferSize);
			
			//start the threads
			w.start();
			r.start();

			//must join threads to sync with main threads
			w.join();
			r.join();

			//close socket after writing
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
