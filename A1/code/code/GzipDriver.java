
/**
 * A simple driver for GzipClient class
 * 
 * CPSC 441
 * Assignment 1
 * 
 * 
 * The following command line arguments can be passed to the driver:
 * 
 * -i	to specify the name of the input file. this argument is REQUIRED.
 * -o	to specify the name of the output file.
 * -b 	to specify the read/write buffer size.
 * -p 	to specify the server port number.
 * -s	to specify the server name
 * -v	to specify the log level
  * 
 * 
 * @author 	Majid Ghaderi
 * @version 2022
 *
 */

import java.io.*;
import java.util.*;
import java.util.logging.*;


public class GzipDriver {
	
	private static final Logger logger = Logger.getLogger("GzipClient"); // global logger

	public static void main(String[] args) {
		

		// input file name is required
		if (args.length == 0) {
			System.out.println("incorrect usage, input file name is required");
			System.out.println("try again");
			System.exit(0);
		}
			
		// parse command line args
		HashMap<String, String> params = parseCommandLine(args);
		
		// set the parameters
		String inName = params.getOrDefault("-i", args[0]); // name of the file to be compressed
		String outName = params.getOrDefault("-o", inName + ".gz"); // name of the compressed file
		String serverName = params.getOrDefault("-s", "localhost"); // server name
		int serverPort = Integer.parseInt( params.getOrDefault("-p", "2025") ); // server port number
		int bufferSize = Integer.parseInt( params.getOrDefault("-b", "10000") ); // buffer size in bytes
        Level logLevel = Level.parse( params.getOrDefault("-v", "all").toUpperCase() ); // log levels: all, info, off

        // standard output
		setLogLevel(logLevel);

		GzipClient client = new GzipClient(serverName, serverPort, bufferSize);
		System.out.printf("sending file \'%s\' to the server for compression...\n", inName);
		client.gzip(inName, outName);
		System.out.println("gzip completed.");

		// get rid of any lingering threads/timers
		System.exit(0);
	}

	
	// parse command line arguments
	private static HashMap<String, String> parseCommandLine(String[] args) {
		HashMap<String, String> params = new HashMap<String, String>();

		int i = 0;
		while ((i + 1) < args.length) {
			params.put(args[i], args[i+1]);
			i += 2;
		}
		
		return params;
	}

	// set the global log level and format
	private static void setLogLevel(Level level) {
		System.setProperty("java.util.logging.SimpleFormatter.format", "%5$s %n");
		
		ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(level);
        logger.addHandler(handler);		
		logger.setLevel(level);
        logger.setUseParentHandlers(false);
	}
}
