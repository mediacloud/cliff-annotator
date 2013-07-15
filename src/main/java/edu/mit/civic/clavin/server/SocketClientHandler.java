package edu.mit.civic.clavin.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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
    private final PrintWriter output;
    private final Socket socket;
    private final MultiClientSocketServer parent;
    
    public SocketClientHandler(Socket incomingSocket, MultiClientSocketServer server) throws IOException{
        parent = server;
        socket = incomingSocket;
        output = new PrintWriter(incomingSocket.getOutputStream());
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        logger.info("Created a new socket connection to "+socket.getRemoteSocketAddress());
    }

    public void run(){
        try{
            String line = null;
            while((line = reader.readLine()) != null){
                boolean quit = false;
                String results = ParseManager.locate(line); 
                output.write(results+"\n");
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
            parent.decrementClientCount();
        }
    }
}
