/*
 * TimeoutHandler class
 * 
 * CPSC 441
 * Assignment 4
 * 
 * @author Christina Truong
 */

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.TimerTask;

public class TimeoutHandler extends TimerTask {
    private DatagramPacket packet; 
    private FtpSegment segment;
    private DatagramSocket udpDatagramSocket;

    /**
     * Constructor to initialize the timeouthandler
     * 
     * @param udpSocket client UDP socket to re transmit the packet
     * @param segment   segment to be re transmitted
     * @param IPAddress ip address of the server
     * @param port      UDP port number of server
     */
    public TimeoutHandler(DatagramSocket udpSocket, FtpSegment segment, InetAddress IPAddress, int port) {
        packet = FtpSegment.makePacket(segment, IPAddress, port);
        this.udpDatagramSocket = udpSocket;
        this.segment = segment;
    }

    /*
     * method run() to run the timer task timeouthandler
     * retransmit the segment encapsulated in a packet to the server
     */
    public void run() {
        // send the packet
        try {
            System.out.println("timeout");
            udpDatagramSocket.send(packet);
            System.out.println("retx\t" + segment.getSeqNum());
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }
}