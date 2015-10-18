/**
 * Created by ianshinbrot on 10/1/15.
 */
import java.net.*;
import java.util.*;
import java.io.*;
import java.text.*;
public class FTPClient extends Thread{

    public FTPClient()  {
    }




    /**
     * this changes the working directory
     * @throws Exception
     */
    public void CWD()  throws Exception{
        System.out.println("What directory would you like to change to?");
        Scanner in = new Scanner(System.in);
        String directory = in.next();
        this.send(("CWD " + directory));

        String line = this.read(socket);

        if (line.startsWith("250 ")) {
            //command successful
            System.out.println("Directory successfully changed");
        }
        else if (line.startsWith("550 ")) {
            //directory doesn't exist
            System.out.println("Directory doesn't exist. Returning to the main menu.");
            return;
        }
        else System.out.println("Command not understood. Returning to the main menu.");
        return;
    }
    /**
     * this prints the working directory with the PWD command
     * @throws Exception
     */
    public void PWD() throws Exception{
        this.send("PWD");
        String dir="";

        String line = this.read(socket);
        if (line.startsWith("257 ")) {
            int firstQuote = line.indexOf('\"');
            int secondQuote = line.indexOf('\"', firstQuote + 1);
            if (secondQuote > 0) {
                dir = line.substring(firstQuote + 1, secondQuote);
            }
            System.out.println("The current directory is " + dir);
        }
        else {
            System.out.println("Current command is not supported.");
        }

    }

    /**
     * This function goes up a parent directory
     * @throws Exception
     */
    public void CDUP() throws Exception {
        this.send("CDUP");
        String line = this.read(socket);

        if (line.startsWith("250")) {
            // command successful
        }
        System.out.println(line);
    }
    public void HELP() throws Exception {

        System.out.println("What command would you like to have help with");
        Scanner in =new Scanner(System.in);
        String command = in.next();
        this.send("HELP " + command);
        //like man command in bash
        // 211 or 214
        String line = this.read(socket);
        if (line.startsWith("502")) {
            // command not understood
            System.out.println("Command " + command + " not understood.");
        }
        else if (line.startsWith("214")) {
            // command definition has been returned
            System.out.println(line);
        }
    }

    public void LIST() throws  Exception {
        this.send("LIST");
        String line = this.read(dataSocket);

        if (line.startsWith("425 ")) {
            // can't build data connection
            this.error();

        }
        System.out.println(line);
    }

    /**
     *
     * @param nameofHost
     * @param port
     * @throws Exception
     */

    public void PASV(String nameofHost, int port) throws Exception {
       // port=22;
        this.send("PASV");


        String line = this.read(socket);
        String[] responselist= line.split("\\(|\\)|,");
        if (line.startsWith("227 ")) {
            // successfully entered passive mode
            int upperbit = Integer.parseInt(responselist[5]);
            int lowerbit = Integer.parseInt(responselist[6]);
            port = upperbit * 256 + lowerbit;
     dataSocket = new Socket(nameofHost, port);
        }
        if (line.startsWith("500 ")) {
            //invalid port command
            this.commanderror=true;

        }
        if (line.startsWith("501 ")) {
            //cannot accept server argument
            this.commanderror=true;
        }
        if (line.startsWith("502 ")) {
            // active ftp not allowed
            this.commanderror=true;
        }
        if (line.startsWith("421 ")) {
            //service not available
            this.commanderror=true;
        }

        if (this.commanderror) {
            this.error();
        }
        System.out.println(line);


    }

    public void error () {
        System.out.println("There was an error. Returning to the main menu.");
        return;
    }

    public void PORT(String nameofHost, int port) throws Exception {
        this.send("PORT " + socket.getLocalAddress());
        //change localhost to ip address

        String line = this.read(dataSocket);
        String[] responselist = line.split("\\(|\\)|,");
        if (!line.startsWith("227 ")) {
            throw new IOException(
                    "FTPClient received an unknown response after switching to passive: "
                            + line);
        }
        else {
            // successfully entered passive mode
            int upperbit = Integer.parseInt(responselist[5]);
            int lowerbit = Integer.parseInt(responselist[6]);
             port = upperbit * 256 + lowerbit;
            dataSocket = new Socket(nameofHost, port);
            //TODO Error checking
        }

        }

    /**
     * This retrieves a file from the ftp server
     * @throws Exception
     */
    public void RETR() throws Exception {

        if (dataSocket==null) {
            System.out.println("Passive mode isn't enabled. Please enable that before continuing");
            return;
        }
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        System.out.println("Please input the path directory of the file you would like to download");
        String directory="";
        Scanner in = new Scanner(System.in);
        directory = in.next();
        String fileName="";
        this.send("RETR " + directory);
        if (directory.contains("/")) {
             fileName = directory.substring(directory.lastIndexOf("/"), directory.length());
        }
        else fileName=directory;

        try {
            // receive file'
            int bytesRead;
            int current = 0;
            File file = new File(fileName);
            byte[] mybytearray = new byte[dataSocket.getReceiveBufferSize()];
            InputStream is = dataSocket.getInputStream();

            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bytesRead = is.read(mybytearray, 0, mybytearray.length);
            current = bytesRead;

            do {
                bytesRead =
                        is.read(mybytearray, current, (mybytearray.length - current));
                if (bytesRead >= 0) current += bytesRead;
            } while (bytesRead > -1);

            bos.write(mybytearray, 0, current);
            bos.flush();
            String line = reader.readLine();
            log("READ: ", "Received: " + line+ "\n");
        }
    finally {
        if (fos != null) fos.close();
        if (bos != null) bos.close();
        if (dataSocket != null) dataSocket.close();
    }

        System.out.println("File successfully downloaded");


    }
    /**
     * Logins into the server with credentials
     * @param cr - the credentials for the user in the ftp client
     */
    public void login(credentials cr) throws Exception {


        cr.userNamePrompt();

        send("USER " + cr.getUsername());
        String response2 = this.read(socket);

        if (!response2.startsWith("331 ")) {
            throw new IOException(
                    "FTPClient received an unknown response after sending the user: "
                            + response2);
        }
        cr.passwordPrompt();
        this.send("PASS " + cr.getPass());

        String response3 = this.read(this.socket);

        if (!response3.startsWith("230 ")) {
            throw new IOException(
                    "FTPClient was unable to log in with the supplied password: "
                            + response3);
        }

        System.out.println(response3);
    }
    public void EPSV() throws Exception {
        this.send("EPSV");

        String line = this.read(this.socket);
        System.out.println(this.read(this.socket));

        if (line.startsWith("229")){
            //command successful

            System.out.println("Now in extended passive mode");
        }
        else{
            // error occured
            this.error();
        }


    }

    /**
     *
     * @throws IOException
     */
    public void QUIT() throws IOException {
        try {
            send("Quit");
        }
        finally {
            socket = null;
        }
        bufferWriter.close();
    }

    /**
     *
     * @param text - sent to the server
     * @throws IOException
     */
    private void send(String text) throws IOException {

        if (socket == null) {
            throw new IOException("Not connected");
        }
        try {
            writer.write(text + "\r\n");
            writer.flush();
            //TODO add debug statement
        }
        catch (IOException e) {
            socket = null;
            throw e;
        }
        log("WRITE: ", text+ "\n");
    }

    /**
     *
     * @return - the line that is read from the server
     * @throws IOException
     */
    private String read(Socket socket) throws IOException {
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        String line = reader.readLine();

        if (line.startsWith("257 ")) // correct

        //TODO add debug statement
        log("READ: ", "Received: " + line + "\n");

        return line;
    }
    /**
     * this prompts for the next command for the ftp client
     * @throws Exception
     */
    public void prompt(String nameofHost, int port) throws Exception{

        boolean run=true;
        while (run) {
            System.out.println("Please choose a command");

            System.out.println("1. CWD");
            System.out.println("2. CDUP");
            System.out.println("3. QUIT");
            System.out.println("4. PASV");
            System.out.println("5. EPSV");
            System.out.println("6. PORT");
            System.out.println("7. EPRT");
            System.out.println("8. RETR");
            System.out.println("9. PWD");
            System.out.println("10. LIST");
            System.out.println("11. HELP");
            Scanner in = new Scanner(System.in);
            int prompt = in.nextInt();
            switch (prompt) {
                case 1:             // CWD This is working successfully
                    this.CWD();
                    break;
                case 2:             // This is working successfully CDUP
                    this.CDUP();
                    break;
                case 3:             // QUIT This is working successfully
                    this.QUIT();
                    run = false;
                    break;
                case 4:             // PASV This is working correctly for txt files
                    this.PASV(nameofHost, port);
                    break;
                case 5:             //  EPSV
                    this.EPSV();
                    break;
                case 6:             // PORT
                    this.PORT(nameofHost, port);
                    break;
                case 7:             // EPRT
                    //   this.EPRT();
                    break;
                case 8:
                    this.RETR();     // RETR
                    break;
                case 9:              // PWD
                    this.PWD();
                    break;
                case 10:             // LIST
                    this.LIST();
                    break;
                case 11:             // HELP Command completed
                    this.HELP();
                    break;
            }
        }

    }
    private void createLogFile(String fileName) throws IOException {
        File file = new File(fileName);

        if (!file.exists()) {
            file.createNewFile();
        }
        fileWriter = new FileWriter(file.getName(), true);
        bufferWriter = new BufferedWriter(fileWriter);

    }

    /**
     * This creates a log statement with the current date
     * @param status - determines whether the read or write command is
     * @param message - the message that is sent or received from the server
     * @throws IOException
     */
    private void log(String status, String message) throws IOException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        bufferWriter.write(dateFormat.format(cal.getTime()) + status + message + "\n");
    }

    /**
     * This connects the socket to the server, and initially connects without logging in
     * @param port - the port that is used to connect
     * @param cr - credentials that are used to connect to the server
     * @throws Exception
     */
    private void initiateConnection(int port, credentials cr) throws Exception {

        createLogFile(cr.getfileName());
        if (socket!=null){
            throw new IOException("Already connected. Must disconnect first");
        }
        socket = new Socket(cr.getHost(), port);      // it's connected

        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        String response1 = this.read(socket);

        if (!response1.startsWith("220 ")) {
            throw new IOException(
                    "FTPClient received an unknown response when connecting to the FTP server: "
                            + response1);
        }

        System.out.println(response1);

        this.login(cr);

    }

    private Socket socket = null;
    FileWriter fileWriter;
    BufferedWriter bufferWriter;
    private credentials credential = new credentials();
    private BufferedReader reader = null;
    private BufferedWriter writer = null;
    private Socket dataSocket = null;
    private boolean commanderror = false;

    public  static void main(String[] args) throws Exception {
        FTPClient client = new FTPClient();
        credentials cr = new credentials();
        if (args.length<1) {
            cr.setHost("tux.cs.drexel.edu");
            System.out.println("FTP Server not specified. Terminating Program");
            System.exit(0);
        }
        cr.setHost(args[0]);
        if (args.length <2) {
            System.out.println("Log file not specified. Terminating Program");
            System.exit(0);
        }
        cr.setfileName(args[1]);
        int port = 21;
        if (args.length > 2) {
             port = Integer.parseInt(args[2]);
        }

        client.initiateConnection(port, cr);


        client.prompt(cr.getHost(), port);
    }

}