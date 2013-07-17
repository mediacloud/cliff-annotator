package edu.mit.civic.clavin.server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles a connection with one client that wants to locate text over a socket connection
 * @author rahulb
 */
public class SocketClientHandler implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(SocketClientHandler.class);

    private final BufferedReader reader; 
    private final DataOutputStream output;
    private final Socket socket;
    private final MultiClientSocketServer parent;
    
    private int requestCount = 0;
    
    public SocketClientHandler(Socket incomingSocket, MultiClientSocketServer server) throws IOException{
        parent = server;
        socket = incomingSocket;
        output = new DataOutputStream(incomingSocket.getOutputStream());
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        logger.info("Created a new socket connection to "+getClientAddress());
        parent.addClientHandler(this);
    }

    protected String getClientAddress(){
        return socket.getRemoteSocketAddress().toString();
    }
    
    public void run(){
        try{
            String line = null;
            while((line = reader.readLine()) != null){
                boolean quit = false;
                requestCount++;
                String results = ParseManager.locate(line)+"\n"; 
                output.write(results.getBytes("UTF-8"));
                output.flush();
                if(quit){
                    break;
                }
            }
        }catch(Exception e){
            logger.error(e.toString());
        }finally{
            try{
                socket.close();
            } catch (Exception ee){
            }
            logger.info("Closet socket to "+socket.getRemoteSocketAddress());
            parent.removeClientHandler(this);
        }
    }
    
    protected synchronized int getRequestCount(){
        return requestCount;
    }
        
}
