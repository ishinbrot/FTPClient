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

    public void login(Socket socket, String username, String password) throws Exception {

        send("User " + username);
        send("Pass" + password);
    }
    public void readfromClient(Socket socket) throws IOException {
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        String response = read();
    }

    public void terminate() throws IOException {
        try {
            send("Quit");
        }
        finally {
            socket = null;
        }
    }

    private void send(String text) throws IOException {
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
    private String read() throws IOException {
        String line = reader.readLine();
        //TODO add debug statement

        return line;
    }
    private void initiateConnection() throws Exception {
        credential.credentialPrompt();
        String user = credential.getUsername();
        String pass = credential.getPass();
        String host = credential.getHost();
        String fileName = credential.getfileName();

        this.initiateConnection(22,  host, fileName, user, pass);
    }

    private Socket socket = null;

    private credentials credential = new credentials();
    private BufferedReader reader = null;
    private BufferedWriter writer = null;

    public static void main(String[] args) throws Exception{
        FTP client= new FTP();

        client.initiateConnection();



    }

}