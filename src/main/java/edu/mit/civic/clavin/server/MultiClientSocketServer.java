package edu.mit.civic.clavin.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Listens for clients that want a socket connection to locate text.
 * TODO: restrict allowed connection clients (ie. localhost only or something) 
 * @author rahulb
 */
public class MultiClientSocketServer implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(MultiClientSocketServer.class);

    private int port;
    private ServerSocket serverSocket;
    private ArrayList<SocketClientHandler> clients;
    
    public MultiClientSocketServer(int p){
        clients = new ArrayList<SocketClientHandler>();
        port = p;
    }

    public void run() {
        try{
            serverSocket = new ServerSocket(port);
            logger.info("Listening for socket connections on port "+port);
            while(true){
                Socket client = serverSocket.accept();
                logger.info("Trying a new socket connection to "+client.getInetAddress());                
                Thread t = new Thread(new SocketClientHandler(client,this));
                t.start();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    protected synchronized void addClientHandler(SocketClientHandler client){
        clients.add(client);
    }

    protected synchronized void removeClientHandler(SocketClientHandler client){
        clients.remove(client);
    }

    public synchronized int getClientCount(){
        return clients.size();
    }

    public int getPort() {
        return port;
    }

    public HashMap<String, Integer> getClientRequestInfo(){
        HashMap<String, Integer> info = new HashMap<String, Integer>();
        for(SocketClientHandler client: clients){
            info.put(client.getClientAddress(), new Integer(client.getRequestCount()));
        }
        return info;
    }
    
}
