

import java.util.logging.*;

public class GoBackFtp {
	// global logger	
	private static final Logger logger = Logger.getLogger("GoBackFtp");

	/**
	 * Constructor to initialize the program 
	 * 
	 * @param windowSize	Size of the window for Go-Back_N in units of segments
	 * @param rtoTimer		The time-out interval for the retransmission timer
	 */
	public GoBackFtp(int windowSize, int rtoTimer);


	/**
	 * Send the specified file to the specified remote server
	 * 
	 * @param serverName	Name of the remote server
	 * @param serverPort	Port number of the remote server
	 * @param fileName		Name of the file to be trasferred to the rmeote server
	 */
	public void send(String serverName, int serverPort, String fileName);
	
} // end of class