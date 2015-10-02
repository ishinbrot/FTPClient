/**
 * Created by ianshinbrot on 10/1/15.
 */
import java.net.*;
import java.util.*;
import java.io.*;
public class FTP {


    public FTP() extends Thread {
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
        socket = new Socket(nameofHost, port);      // it's connected
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
    }

    private Socket socket = null;

    private BufferedReader reader = null;

}