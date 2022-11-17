import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientExample {
	
	private Socket aSocket;
	private PrintWriter socketOut;
	private BufferedReader socketIn;
	private BufferedReader stdIn;
	
	public ClientExample (String serverName, int portNumber) {
		try {
			aSocket = new Socket (serverName, portNumber);
			//keyboard input stream
			stdIn = new BufferedReader (new InputStreamReader (System.in));
			//Socket input stream
			socketIn = new BufferedReader (new InputStreamReader (aSocket.getInputStream()));
			socketOut = new PrintWriter((aSocket.getOutputStream()), true);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void commmunicate () {
		String line = "";
		String response = "";
		
		while (!line.contentEquals("QUIT")) {
			
			try {
				System.out.println("Please enter a word: ");
				line = stdIn.readLine();//read line from the user (i.e. from the keyboard)
				socketOut.println(line);
				response = socketIn.readLine(); // read response from the socket
				System.out.println("Response is: " + response);
				
				
			}catch(IOException e) {
				e.getStackTrace();
			}
			
		}
		try {
			stdIn.close();
			socketIn.close();
			socketOut.close();
		}catch(IOException e) {
			e.getStackTrace();
		}
	}
	
	public static void main (String [] args) throws IOException{
		ClientExample myClient = new ClientExample ("localhost", 9898);
		myClient.commmunicate();
	}
	
	
	

}
