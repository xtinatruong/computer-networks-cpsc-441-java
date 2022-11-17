import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;


//Note: The Client is the same as the one for the previous example (i.e. client/server no threads) 
public class ServerWithTwoThreads {

	private Socket aSocket;
	private ServerSocket serverSocket;
	private PrintWriter socketOut;
	private BufferedReader socketIn;

	public ServerWithTwoThreads(int port) {
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException {

		try {
			ServerWithTwoThreads myServer = new ServerWithTwoThreads(9898);

			myServer.aSocket = myServer.serverSocket.accept();
			System.out.println("Connection accepted by server!");
			myServer.socketIn = new BufferedReader(new InputStreamReader(myServer.aSocket.getInputStream()));
			myServer.socketOut = new PrintWriter((myServer.aSocket.getOutputStream()), true);
			// myServer.capitalize();
			Capitalizer cap1 = new Capitalizer(myServer.socketIn, myServer.socketOut);
			Thread t1 = new Thread(cap1);
			t1.start();

			myServer.aSocket = myServer.serverSocket.accept();
			System.out.println("Connection accepted by server!");
			myServer.socketIn = new BufferedReader(new InputStreamReader(myServer.aSocket.getInputStream()));
			myServer.socketOut = new PrintWriter((myServer.aSocket.getOutputStream()), true);
			// myServer.capitalize();
			Capitalizer cap2 = new Capitalizer(myServer.socketIn, myServer.socketOut);
			Thread t2 = new Thread(cap2);
			t2.start();

			try {
				t1.join();
				t2.join();
			} catch (InterruptedException e) {
				e.getStackTrace();
			}

			myServer.socketIn.close();
			myServer.socketOut.close();
		} catch (IOException e) {
			e.getStackTrace();
		}

	}

}
