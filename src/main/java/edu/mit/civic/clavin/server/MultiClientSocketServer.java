package edu.mit.civic.clavin.server;

import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Listens for clients that want a socket connection to locate text. 
 * @author rahulb
 */
public class MultiClientSocketServer implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(MultiClientSocketServer.class);

    private int port;
    private ServerSocket serverSocket;
    private int clientCount = 0;    // WARNING: wrap access to this in synchronized methods

    public MultiClientSocketServer(int p){
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
                clientCount++;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    protected synchronized void decrementClientCount(){
        clientCount--;
    }

    public synchronized int getClientCount(){
        return clientCount;
    }

    public int getPort() {
        return port;
    }

}
