import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class WorkerThread implements Runnable {
    /**
     * local file to send to client
     */
    private File file;
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

    private int timeout;

    public WorkerThread(Socket socket, int timeout) {
        try {
            this.socket = socket;
            this.timeout = timeout;
        } catch (Exception e) {

        }
    }

    @Override
    public void run() {
        // Send response message
        String headerResp;

        try {
            // set timeout
            socket.setSoTimeout(timeout);

            // print client's IP address and port number
            System.out.println(socket.getRemoteSocketAddress().toString());

            // Read and parse the request
            String httpReqHeader = new String();
            // read from socket
            InputStream socketIn;

            socketIn = socket.getInputStream();
            // read the inputstream until "\r\n\r\n" meaning the end of the header
            while (!httpReqHeader.contains("\r\n\r\n")) {
                // read from input stream 1 byte at a time and adding it to header string
                String byteToString;
                byteToString = new String(socketIn.readNBytes(1), 0, 1, "US-ASCII");
                httpReqHeader += byteToString;
            }
            // print HTTP Request from client
            System.out.println(httpReqHeader);

            // split by line
            String[] reqSplit = httpReqHeader.split("\r\n");
            // grab GET request from first line split
            String getReq = reqSplit[0].strip();
            // filepath is the second term after GET separated by spaces
            String filepath = getReq.split(" ")[1].strip();

            // if there's no pathname specified and filepath is just a slash
            if (filepath.equalsIgnoreCase("/"))
                filepath = "/index.html";

            // Java file only works without slash
            if (filepath.charAt(0) == '/') {
                file = new File(filepath.substring(1));
            } else {
                file = new File(filepath);
            }

            // check bad request
            if (!getReq.contains("GET") || !getReq.contains("HTTP/1.1") || !filepath.contains("/")
                    || !filepath.contains(".")) {
                headerResp = sendResp(400);
            } else if (!file.exists()) { // use java class file .exist() to check for 404
                headerResp = sendResp(404);
            } else { // checks OK
                headerResp = sendResp(200);
                // send object content
                buff = new byte[(int) file.length()];
                localFileInputStream = new BufferedInputStream(new FileInputStream(file));
                write();
            }

            // prints out client
            System.out.println(headerResp);

            // send response message to client
            OutputStream sockeOutputStream = socket.getOutputStream();
            sockeOutputStream.write(headerResp.getBytes("US-ASCII"));

            sockeOutputStream.close();

            // close and cleanup
            socket.shutdownInput();
            socket.shutdownOutput();
            socket.close();
        } catch (SocketTimeoutException e) {
            System.out.println(sendResp(408));
            
            try {
                // send response message to client
                OutputStream sockeOutputStream = socket.getOutputStream();
                sockeOutputStream.write(sendResp(408).getBytes("US-ASCII"));
                sockeOutputStream.close();
                socket.shutdownOutput();
                socket.close();
            } catch (UnsupportedEncodingException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (IOException e1) {
                
            }
            
        } catch (IOException e) {
            
        }
    }

    /**
     * Sends Response to client depending on response code entered
     * 
     * @param reqNum response code to respond c
     */
    private String sendResp(int reqNum) {
        // trycatch the whole function for socket timeoutexception and if caught close
        // the socket
        // Send error message
        String headerResp = "HTTP/1.1 ";
        if (reqNum == 408) {
            headerResp += " 408 Request Timeout\r\n";
        } else if (reqNum == 404) {
            headerResp += " 404 Not Found\r\n";
        } else if (reqNum == 200) {
            headerResp += " 200 OK\r\n";
        } else {
            headerResp += " 400 Bad Request\r\n";
        }

        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm:ss zzz");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        try {
            headerResp += "Date: " + formatter.format(date) + "\r\n";
            headerResp += "Server: " + InetAddress.getLocalHost() + "\r\n";
            if (reqNum == 200) {
                headerResp += "Last-Modified: " + formatter.format(new Date(file.lastModified())) + "\r\n";
                headerResp += "Content-Length: " + (int) file.length() + "\r\n";
                headerResp += "Content-Type: " + Files.probeContentType(file.toPath()) + "\r\n";
            }
        } catch (IOException e) {
            System.out.println("Can't get File Type or LocalHost");
        }
        headerResp += "Connection: close\r\n\r\n";

        return headerResp;
    }

    /**
     * buffer writes by specified bytes from server's filename to the client
     * socket
     */
    private void write() {
        try {
            int readBytes = 0;
            // sending local file from server
            socketOut = new BufferedOutputStream(socket.getOutputStream(), buff.length);
            while ((readBytes = localFileInputStream.read(buff)) != -1) {
                socketOut.write(buff, 0, readBytes);
                socketOut.flush();
            }
            localFileInputStream.close();
            socketOut.close();
        } catch (SocketTimeoutException e) {
                   
        } catch (IOException e) {
               
        }
    }
}
