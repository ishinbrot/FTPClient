/**
 * Created by ianshinbrot on 10/1/15.
 */
import java.net.*;
import java.util.*;
import java.io.*;
public class FTP extends Thread{


    public FTP()  {
    }

    public void connect(String nameofHost) {

    }

    /**
     *
     * @param port -  the port that will be used for the ftp address, if not specified default is 22
     * @param nameofHost - name of the ftp server host
     * @param fileName - the file name consisting of where the log files go
     *                 this uses anonomyous user and password to login
     */
    public void initiateConnection(int port, String nameofHost, String fileName) throws Exception{      // if port isn't specified 22 will be used
        if (socket!=null){
            throw new IOException("Already connected. Must disconnect first");
        }
        initiateConnection(port, nameofHost,fileName,"anonymous","anonymous");      // if no username is needed anonymous should be used
    }

    /**
     *
     * @param port - port that will be used for the ftp address, if not specific port 22 will be used
     * @param nameofHost
     * @param fileName - filename where the log files go
     * @param user - username to connect to the ftp client
     * @param password - password to connect to the ftp client
     */
    public void initiateConnection(int port, String nameofHost, String fileName, String user, String password) throws Exception {

        if (user.isEmpty() && password.isEmpty()) {
            this.initiateConnection(port, nameofHost, fileName);
        }
        if (socket!=null){
            throw new IOException("Already connected. Must disconnect first");
        }
        socket = new Socket(nameofHost, port);      // it's connected

        login(socket, user, password);
    }
    /**
     * @param socket - socket that connects to the ftp client
     * @param username - username to connect to the ftp client
     * @param password - password to connect to the ftp client
     */
    public void login(Socket socket, String username, String password) throws Exception {

        send("User " + username);
        send("Pass" + password);
    }
    /**
     *
     * @throws IOException
     */
    public void terminate() throws IOException {
        try {
            send("Quit");
        }
        finally {
            socket = null;
        }
    }

    /**
     *
     * @param text - sent to the server
     * @throws IOException
     */
    private void send(String text) throws IOException {
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        if (socket == null) {
            throw new IOException("Not connected");

        }
        try {
            writer.write(text);
            writer.flush();
            //TODO add debug statement
        }
        catch (IOException e) {
            socket = null;
            throw e;
        }
    }

    /**
     *
     * @return - the line that is read from the server
     * @throws IOException
     */
    private String read() throws IOException {
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String line = reader.readLine();
        //TODO add debug statement

        return line;
    }

    /**
     *
     * @param port - the port that is used to connect
     * @param cr - credentials that are used to connect to the server
     * @throws Exception
     */
    private void initiateConnection(int port, credentials cr) throws Exception {
       // credential.credentialPrompt();
        cr.credentialPrompt();
        String user = cr.getUsername();
        String pass = cr.getPass();
        String host = cr.getHost();
        String fileName = cr.getfileName();

        this.initiateConnection(port,  host, fileName, user, pass);
    }

    private Socket socket = null;
    private credentials credential = new credentials();
    private BufferedReader reader = null;
    private BufferedWriter writer = null;

    public  static void main(String[] args) throws Exception {
        FTP client = new FTP();
        credentials cr = new credentials();
        cr.setHost(args[0]);
        cr.setfileName(args[1]);
        int port = 22;
        if (args.length == 3) {
             port = Integer.parseInt(args[2]);
        }

        client.initiateConnection(port, cr);


    }

}