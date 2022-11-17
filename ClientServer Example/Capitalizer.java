import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class Capitalizer implements Runnable{
	
	private PrintWriter socketOut;
	private BufferedReader socketIn;
	
	public Capitalizer (BufferedReader in, PrintWriter out) {
		socketIn = in;
		socketOut = out;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		capitalize();
	}
	
	// The logic of the application:
	private void capitalize() {
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

}
