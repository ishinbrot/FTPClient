import java.io.Console;

/**
 * Created by ianshinbro on 10/4/2015.
 */
public class credentials {

    public credentials() {
        host = new String();
        username = new String();
        pass = new String();
    }
    public void credentialPrompt() {
        Console console = System.console();
        host = console.readLine("Please enter the host name of the server");

        username = console.readLine("Please enter the password");

        pass = new String(console.readPassword("Please enter the password"));

        fileName = console.readLine("Please enter the file name");
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
