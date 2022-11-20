/*
 * StopWaitFtp class
 * 
 * CPSC 441
 * Assignment 4
 * 
 * @author Christina Truong
 * 
 */

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Timer;
import java.util.logging.*;

public class StopWaitFtp {
	// global logger
	private static final Logger logger = Logger.getLogger("StopWaitFtp");
	// timeout in milli-seconds
	private int timeout, serverUdpPort, sequenceNumber;
	/**
	 * tcp socket server for udp
	 */
	private Socket socket;

	/**
	 * Datagram socket for udp sending
	 */
	private DatagramSocket udpSocket;


	/**
	 * timer for timeout
	 */
	private Timer t;


	/**
	 * input stream for file sending
	 */
	private BufferedInputStream fIstream;

	/**
	 * Constructor to initialize the program
	 * 
	 * @param timeout The time-out interval for the retransmission timer, in
	 *                milli-seconds
	 */
	public StopWaitFtp(int timeout) {
		//initialize timer
		t = new Timer();

		//set timeout
		setTimeout(timeout);
	}

	/**
	 * Send the specified file to the remote server
	 * 
	 * @param serverName Name of the remote server
	 * @param serverPort Port number of the remote server
	 * @param fileName   Name of the file to be trasferred to the rmeote server
	 */
	public void send(String serverName, int serverPort, String fileName) {
		try {
			// Open a TCP connection to the server
			socket = new Socket(serverName, serverPort);
			// Open a UDP socket
			udpSocket = new DatagramSocket();

			// Complete the handshake over TCP - Handshake Message Sequence
			tcpHandshake(socket, fileName, udpSocket);

			// get server ip
			InetAddress inetAddress = InetAddress.getByName(serverName);

			// start bufferedfileinputstream
			fIstream = new BufferedInputStream(new FileInputStream(parsedFile(fileName)));

			// set array to read into at max payload
			byte[] fileByteArray = new byte[FtpSegment.MAX_PAYLOAD_SIZE];
			int byteReadNum;

			// while not end of file do
			while ((byteReadNum = fIstream.read(fileByteArray)) != -1) {
				// Read from the file and create a segment
				FtpSegment segment = new FtpSegment(sequenceNumber, fileByteArray, byteReadNum);
				
				// Send the segment as packet and start the timer
				udpSocket.send(FtpSegment.makePacket(segment, inetAddress, serverUdpPort));
				System.out.println("send\t" + sequenceNumber);

				//store a copy of the segment (to be transmitted when a timeout happens) in the timer task object
				FtpSegment segment_copy = new FtpSegment(sequenceNumber, fileByteArray, byteReadNum);
				
				// start timer right after send packet 
				TimeoutHandler toHandler = new TimeoutHandler(udpSocket, segment_copy, inetAddress, serverUdpPort);
				//create a recurring timer task with no delay and timeout specified
				t.scheduleAtFixedRate(toHandler, timeout, timeout);
				
				// create an empty datagram packet to place ack packet recieved from server
				DatagramPacket packetRecieved = new DatagramPacket(new byte[FtpSegment.MAX_PAYLOAD_SIZE], FtpSegment.MAX_PAYLOAD_SIZE);
				int nextSequence = sequenceNumber + 1;

				while (sequenceNumber != nextSequence) {
					udpSocket.receive(packetRecieved);
					// extract the segment from the packet recieved
					FtpSegment segmentRecieved = new FtpSegment(packetRecieved);
					System.out.println("ack\t" + segmentRecieved.getSeqNum());

					// if the ack recieved is the correct ack with the correct sequence number
					if (segmentRecieved.getSeqNum() == nextSequence) {
						// stop the timer
						toHandler.cancel();
						// increase the sequence number
						sequenceNumber++;
					}
				}

			}
			// cancel the timer object
			cancelTimer();

			// close all streams
			close();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	/**
	 * completes tcp handshake per instructed by lab handout
	 * @param socket
	 * @param fileName
	 * @param udpSocket
	 */
	private void tcpHandshake(Socket socket, String fileName, DatagramSocket udpSocket) {
		try {
			// connect tcp socket i/o stream
			DataInputStream socketIn = new DataInputStream(socket.getInputStream());
			DataOutputStream socketOut = new DataOutputStream(socket.getOutputStream());

			// 1. Send the local UDP port number used for file transfer as an int value
			socketOut.writeInt(udpSocket.getLocalPort());

			// if file exist then send data other wise quit
			File file = parsedFile(fileName);
			if (file != null && file.exists()) {
				// 2. send the filename to the server as a UTF encoded string
				socketOut.writeUTF(file.getName());
				// 3. Send the length (in bytes) of the file as a long value
				socketOut.writeLong(file.length());
			} else {
				System.exit(0);
			}
			// flush() output for TCP to send data to server
			socketOut.flush();

			// 4. Receive the server UDP port number used for file transfer as an int
			serverUdpPort = socketIn.readInt();
			// 5. Receive the initial sequence number used by the server as an int
			sequenceNumber = socketIn.readInt();

			//close socket since everything after is being communicated by udp
			socketIn.close();
			socketOut.close();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

	}

	/**
	 * parse file to get rid of / in file path 
	 * @param fileName
	 * @return a file with name parsed
	 */
	private File parsedFile(String fileName) {
		//if file starts with / then remove it
		if (fileName.charAt(0) == '/') {
			return new File(fileName.substring(1));
		} else {
			return new File(fileName);
		}
	}

	/**
	 * cancle and purge timer after done file transmission
	 */
	private void cancelTimer() {
		t.cancel();
		t.purge();
	}

	/**
	 * close all streams
	 */
	private void close() {
		try {
			socket.close();
			udpSocket.close();
			fIstream.close();
		} catch (IOException e) {
			return;
		}
	}


	/**
	 * auto generated setters below by vscode 
	 */
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public void setServerUdpPort(int serverUdpPort) {
		this.serverUdpPort = serverUdpPort;
	}

	public void setSequenceNumber(int sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public void setUdpSocket(DatagramSocket udpSocket) {
		this.udpSocket = udpSocket;
	}

	public void setT(Timer t) {
		this.t = t;
	}

	public void setfIstream(BufferedInputStream fIstream) {
		this.fIstream = fIstream;
	}
}