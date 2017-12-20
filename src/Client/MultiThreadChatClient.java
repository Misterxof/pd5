package Client;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class MultiThreadChatClient
 */
public class MultiThreadChatClient implements Runnable{

   private static Socket clientSocket = null;
   private static PrintStream os = null;
   private static DataInputStream is = null;
   private static BufferedReader inputLine = null;
   private static boolean closed = false;

    private static Logger LOGGER = LogManager.getLogger(MultiThreadChatClient.class);

    /**
     * The method start Client
     * @param args - arguments
     */
   public static void main(String[] args){
       int portNumber = 5000;
       String host = "localhost";

       if(args.length < 2){
           System.out.println("Please enter host and portNumber.");
       }
       else {
           host = args[0];
           portNumber = Integer.valueOf(args[1]).intValue();
       }

       try {
           LOGGER.debug("Initializing client socket and input and output streams.");
           clientSocket = new Socket(host, portNumber);
           inputLine = new BufferedReader(new InputStreamReader(System.in));
           os = new PrintStream(clientSocket.getOutputStream());
           is = new DataInputStream(clientSocket.getInputStream());

       }catch (UnknownHostException e){
           System.err.println("Don't know about host " + host);
       }catch (IOException e){
           e.printStackTrace();
       }

       if (clientSocket != null && os != null && is != null){
           try {
               System.out.println("Use following command to print message: sendmsg to [id] [msg]" );
               System.out.println("Use following command to see list of clients : list to [id]" );

               LOGGER.debug("Start client thread.");
               new Thread(new MultiThreadChatClient()).start();
               while (!closed) os.println(inputLine.readLine());

               os.close();
               is.close();
               clientSocket.close();
           }catch (IOException e){
               e.printStackTrace();
           }
       }
   }

    /**
     *Overriding the method run().
     */
    @Override
    public void run() {
       String responseLine;
       try {

           while ((responseLine = is.readLine()) != null) {
               System.out.println(responseLine);
           }
           closed = true;
       }catch (IOException e){
           e.printStackTrace();
       }
    }
}