
/**
 * HttpClient Class
 * 
 * CPSC 441
 * Assignment 2
 * 
 * by: Christina Truong
 * UCID: 30064426
 * 
 */


import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.*;

public class HttpClient {

	private static final Logger logger = Logger.getLogger("HttpClient"); // global logger
    /**
	 * socket that connects to server
	 */
	private Socket socket;

    /**
     * Default no-arg constructor
     */
	public HttpClient() {
		// nothing to do!
	}
	
    /**
     * Downloads the object specified by the parameter url.
	 *
     * @param url	URL of the object to be downloaded. It is a fully qualified URL.
     */
	public void get(String url){
         //1. Parse the input url to extract server address and object path
         String[] urlParts = parseURL(url);
         String host = urlParts[0];
         int port = Integer.parseInt(urlParts[1]);
         String pathname = urlParts[2];
         String filename = urlParts[3];

        //2: Establish a TCP connection with the server
        try {
			// create socket object
			socket = new Socket(host, port);

            //3: Send a GET request for the specified object
            String httpGetRequest = "GET "+ pathname+ " HTTP/1.1\r\nHost: "+host +"\r\n"+"Connection: close\r\n\r\n";
            System.out.println(httpGetRequest);
            OutputStream socketOut = socket.getOutputStream();
            socketOut.write(httpGetRequest.getBytes("US-ASCII"));
            //must close socket output
            socket.shutdownOutput();

            // 4: Read the server response status and header lines
            InputStream socketIn = socket.getInputStream();
            String httpRespHeader = new String();

            //read the inputstream until "\r\n\r\n" meaning the end of the header
            while(!httpRespHeader.contains("\r\n\r\n")){
                //read from input stream 1 byte at a time and adding it to header string
                String byteToString = new String(socketIn.readNBytes(1), 0, 1, "US-ASCII");
                httpRespHeader += byteToString;
            }

            System.out.println(httpRespHeader);
            
            // 5: if response status is OK
            if(httpRespHeader.contains("HTTP/1.1 200 OK\r\n")){
                //grabs content length to buffer read
                int contentLength = Integer.parseInt(httpRespHeader.split("Content-Length: ")[1].split("\r\n", 2)[0].strip());

                // 6: Create the local file with the object name
                BufferedOutputStream contentFromServerOutputStream = new BufferedOutputStream(new FileOutputStream(filename));
                int numBytes;
                byte[] buff = new byte[contentLength];

                // 7: while not end of input stream 
                while ((numBytes = socketIn.read(buff)) != -1) {
                    // 8: Read from the socket and write to the local file
                    contentFromServerOutputStream.write(buff, 0, numBytes);
                    contentFromServerOutputStream.flush();
                }

                //8: close all streams after reading from local file finishes
                contentFromServerOutputStream.close();
                socket.shutdownInput();
                socket.close();
            }
		} catch (Exception e) {
            e.printStackTrace();
        }
     }

    /**
     * parses the URL to grab host, port, pathname, and filename
     * @param url
     * @return String array that contains host, port, pathname, and filename
     */
    private String[] parseURL(String url){
        String [] hostandport;
        String host, port, pathname, filename = "index.html";

        //split by "//"" in 2 parts to get rid of http
        String [] parts = url.strip().split("//", 2);

        //split the second part of parts[] by "/" in 2 parts again to keep host and pathname
        parts = parts[1].strip().split("/", 2);

        if(parts[0].contains(":")){ //if the url has a port specified 
            //split by : to get the port and host
            hostandport = parts[0].strip().split(":", 2);
            host = hostandport[0];
            port = hostandport[1];
        }else{ //if the url has not a port specified
            host = parts[0];
            port = "80";
        }

        if(parts[1].isEmpty()){ //if there's no pathname specified
            pathname = "/index.html";
        }else{//if there is pathname specified
            pathname = "/" + parts[1];
            String[] pathnameparts = parts[1].strip().split("/");
            filename = pathnameparts[pathnameparts.length-1].contains(".") ? pathnameparts[pathnameparts.length-1] : "index.html";
        }

        //returns parts of the url as array
        String[] urlParts = {host, port, pathname, filename};
        return urlParts;
     }

}
