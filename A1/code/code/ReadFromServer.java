import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;

/** Represents a readfromserver thread
 * @author Christina Truong
 * @author 30064426
*/
public class ReadFromServer extends Thread {
    /**
     * writes gzip file from server to local machine
     */
    private BufferedOutputStream gzipFromServerOutputStream = null;
    /**
     * connects to server 
     */
    private Socket socket;
    /**
     * reads data with specified buffer size into buffer array from client
     */
    private BufferedInputStream socketIn;
    /**
     *  buffer array to read from file
     */
    private byte[] buff;

    /**
     * Creates ReadFromServer thread with fileoutput name, socket, and buffersize
     * @param outName local filepath name to receive gzipfile from server and write to local machine
     * @param s socket that connects to server
     * @param bSize buffer size of buffer array to read from server into 
     * @throws IOException
     */
    public ReadFromServer(String outName, Socket s, int bSize) throws IOException {
        try {
            gzipFromServerOutputStream = new BufferedOutputStream(new FileOutputStream(outName));
            socket = s;
            buff = new byte[bSize];
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Thread#run()
     * runs the read()
     */
    @Override
    public void run() {
        read();
    }

    /**
     * reads from what server sends back so in this case it would auto create the
     * gzipfile
     */
    private void read() {
        try {
            int readBytes = 0;
            // receiving gzipfile from server
            socketIn = new BufferedInputStream(socket.getInputStream(), buff.length);
            while ((readBytes = socketIn.read(buff)) != -1) {
                System.out.println("R " + readBytes);
                gzipFromServerOutputStream.write(buff, 0, readBytes);
                gzipFromServerOutputStream.flush();
            }
            // closing outputstream after reading from local file finishes
            gzipFromServerOutputStream.close();
            //must close socket Input for readFromserver thread to work
            socket.shutdownInput();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
