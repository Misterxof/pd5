import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;


public class MultiThreadChatClient implements Runnable{
   private static Socket clientSocket = null;
   private static PrintStream os = null;
   private static DataInputStream is = null;
   private static BufferedReader inputLine = null;
   private static boolean closed = false;

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
               //read from server
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

    @Override
    public void run() {
       //reading from the socket of the MultiThreadServer
       String responseLine;
       try {
           while ((responseLine = is.readLine()) != null) //when receive messages print them
               System.out.println(responseLine);
           closed = true;
       }catch (IOException e){
           e.printStackTrace();
       }
    }
}