import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

/**
 * Class MultiThreadServer
 */
public class MultiThreadServer implements Runnable{
    Socket csocket;

    private static Logger LOGGER = LogManager.getLogger(MultiThreadServer.class);

    static HashMap<Integer,Socket> hm = new HashMap<>(); //store each client socket
    static int k = 1;

    /**
     * Constructor for MultiThreadServer class
     * @param csocket - client socket
     */
    MultiThreadServer(Socket csocket){
        this.csocket = csocket;
    }

    /**
     * The method start Server
     * @param args - arguments
     * @throws Exception
     */
    public static void main(String[] args)throws Exception{
        ServerSocket ssock = new ServerSocket(5000);
        System.out.println("Listening");

        while (true){
            Socket sock = ssock.accept(); // accept the client socket

            LOGGER.debug("Start server thread.");

            MultiThreadServer server = new MultiThreadServer(sock);
            new Thread(server).start();

            PrintStream ps = new PrintStream(sock.getOutputStream());
            ps.println("You are client number " + k + ".");
            ps.println("Enter command.");

            hm.put(k,sock); // we add sock into hashMap when client connected
            LOGGER.debug("Client " + k + " connected to server.");
            System.out.println("Connected to client " + k);
            k++;
            ps.flush();
        }
    }

    /**
     * Overriding the method run().
     * The server read messages from clients socket input streams and do commands
     */
    @Override
    public void run() {
        try {
            PrintWriter out;
            BufferedReader in = new BufferedReader(new InputStreamReader(csocket.getInputStream()));
            String inputLine;
            String j = "sendmsg";  // send message command
            String l = "to";
            String list = "list"; // show list of clients command

            while ((inputLine = in.readLine()) != null){
                String a[] = inputLine.split(" ");

                if(a[0].equals(j) && a[1].equals(l)){
                    int id = Integer.parseInt(a[2]);

                    if(hm.containsKey(id)){
                        Socket server1 = hm.get(id);

                        out = new PrintWriter(server1.getOutputStream(), true);

                        String message = "";
                        boolean flag = false;

                        for (int i = 3; i < a.length; i++){
                            if(flag == false) {
                                message += a[3] + " ";
                                flag = true;
                            }
                            else  message += a[i] + " ";
                        }
                        LOGGER.debug("Send message to Client N" + id + " : " + message);
                        out.println("New message: " + message);
                        out.flush();
                        flag = false;
                    }
                    else {
                        out = new PrintWriter(csocket.getOutputStream(), true);
                        LOGGER.debug("Client offline");
                        out.println("Client offline");
                        out.flush();
                    }
                }
                else if(a[0].equals(list)){
                    int id = Integer.parseInt(a[2]);
                    Socket server1 = hm.get(id);

                    out = new PrintWriter(server1.getOutputStream(), true);
                    out.println("U can send message to: ");
                    LOGGER.debug("Client N" + id + " use list command.");
                    for (int i = 0; i < hm.size(); i++) {

                        out.println("Client N" + (i + 1));
                        out.flush();
                    }
                }
            }
        }catch (IOException e){
            System.out.println("Bue");;
        }
    }
}