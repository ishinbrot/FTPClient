import java.io.Console;
import java.io.IOException;
import java.util.*;
import java.io.*;

/**
 * Created by ianshinbro on 10/4/2015.
 */
public class credentials {

    public credentials() {
        host = new String();
        username = new String();
        pass = new String();
    }
    public void credentialPrompt() throws IOException {
        // Scanner in = new Scanner(System.in);
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        System.out.print("Please enter the username: ");
        username = in.readLine();
       // EraserThread et = new EraserThread("Please enter the password: ");
      //  Thread mask = new Thread(et);
      //  mask.start();

            pass = new String(System.console().readPassword());

    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }
    public void setfileName(String fileName) {
        this.fileName = fileName;
    }
    public String getfileName() {
        return fileName;
    }

    private String host;
    private String username;
    private String pass;
    private String fileName;
}
