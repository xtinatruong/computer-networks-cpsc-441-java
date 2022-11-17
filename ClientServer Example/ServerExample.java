import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerExample {

	private Socket aSocket;
	private ServerSocket serverSocket;
	private PrintWriter socketOut;
	private BufferedReader socketIn;

	public ServerExample(int port) {
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// The logic of the application:
	public void capitalize() {
		String line = null;

		while (true) {
			try {
				line = socketIn.readLine();
				if (line.equals("QUIT")) {
					line = "Good Bye!";
					socketOut.println(line);
					break;
				}
				line = line.toUpperCase();
				socketOut.println(line);
			} catch (IOException e) {
				e.getStackTrace();
			}
		}
	}

	public static void main (String [] args) throws IOException{
		
		try {
		ServerExample myServer = new ServerExample (9898);
		myServer.aSocket = myServer.serverSocket.accept();
		System.out.println("Connection accepted by server!");
		myServer.socketIn = new BufferedReader (new InputStreamReader (myServer.aSocket.getInputStream()));
		myServer.socketOut = new PrintWriter((myServer.aSocket.getOutputStream()), true);
		myServer.capitalize();
		
		myServer.socketIn.close();
		myServer.socketOut.close();
		}catch (IOException e) {
		e.getStackTrace();
	}
		
		
		
	}

}
