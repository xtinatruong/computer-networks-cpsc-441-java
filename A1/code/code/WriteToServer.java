import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.Socket;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
/** Represents a WriteToServer thread
 * @author Christina Truong
 * @author 30064426
*/
public class WriteToServer extends Thread {
    /**
     * reads from local file path specified
     */
    private BufferedInputStream localFileInputStream = null;
    /**
     * client socket that connects to server socket
     */
    private Socket socket;
    /**
     * writes to server
     */
    private BufferedOutputStream socketOut;
    /**
     * buffer array to read from file
     */
    private byte[] buff;

    /**
     * @param inName local filepath name to write to server
     * @param s socket that connects to server
     * @param bSize buffer size of buffer array to read from
     */
    public WriteToServer(String inName, Socket s, int bSize) {
        try {
            localFileInputStream = new BufferedInputStream(new FileInputStream(inName));
            socket = s;
            buff = new byte[bSize];
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /* (non-Javadoc)
     * @see java.lang.Thread#run()
     * executes the write();
     */
    @Override
    public void run() {
        write();
    }

    /**
     * buffer writes by specified bytes from GzipClient's infilename to the server
     */
    private void write() {
        try {
            int readBytes = 0;
            // sending local file to server for gzip
            socketOut = new BufferedOutputStream(socket.getOutputStream(), buff.length);
            while ((readBytes = localFileInputStream.read(buff)) != -1) {
                System.out.println("W " + readBytes);
                socketOut.write(buff, 0, readBytes);
                socketOut.flush();
            }
            //closing inputstream after reading from local file finishes
            localFileInputStream.close();
            //must close socket output for readFromserver thread to work
            socket.shutdownOutput();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}