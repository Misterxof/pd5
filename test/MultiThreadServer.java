import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;


public class MultiThreadServer implements Runnable{
    Socket csocket;
    static HashMap<Integer,Socket> hm = new HashMap<>(); //store each client socket
    static int k = 1;

    MultiThreadServer(Socket csocket){
        this.csocket = csocket;
    }

    public static void main(String[] args)throws Exception{
        ServerSocket ssock = new ServerSocket(5000);
        System.out.println("Listening");

        while (true){
            Socket sock = ssock.accept(); // accept the client socket
            MultiThreadServer server = new MultiThreadServer(sock);
            new Thread(server).start();
            hm.put(k,sock); // we add sock into hashMap when client connected
            System.out.println("Conected to client " + k);
            k++;
        }
    }

    @Override
    public void run() {
        try {
            PrintWriter out;
            BufferedReader in = new BufferedReader(new InputStreamReader(csocket.getInputStream()));
            String inputLine;
            String j = "sendmsg";
            String l = "to";
            String t = "sendtoall";

            while ((inputLine = in.readLine()) != null){
                String a[] = inputLine.split(" ");

                if(a[0].equals(j) && a[1].equals(l)){
                    int id = Integer.parseInt(a[2]);

                    if(hm.containsKey(id)){
                        Socket server1 = hm.get(id);

                        out = new PrintWriter(server1.getOutputStream(), true);
                       /* String message = "";
                        for (int i = 0; i < a.length; i++){
                            message +=
                        }*/
                        out.println(a[3]);
                        out.flush();
                    }
                    else {
                        out = new PrintWriter(csocket.getOutputStream(), true);
                        out.println("Client offline");
                        out.flush();
                    }
                }
                else if (a[0].equals(t)){

                    for (int i = 0; i < hm.size(); i++){
                        Socket server1 = hm.get(i);

                        out = new PrintWriter(server1.getOutputStream(), true);
                        out.println(a[3]);
                        out.flush();
                    }
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}