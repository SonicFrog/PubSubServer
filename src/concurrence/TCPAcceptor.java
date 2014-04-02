package concurrence;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This is the class running the main server process
 * A.k.a. the welcoming thread
 * @version 0.19999b alpha unstable build 0xFFF56
 * @author ars3nic
 *
 */
public class TCPAcceptor implements Runnable {

	private static TCPAcceptor instance = null;
	
	public static final int PORT = 7676;
	
	public static final int MAX_NB_THREAD = 12;
	
	/**
	 * The Socket used to listen for incoming client connections
	 */
	private ServerSocket socket;
	
	private ExecutorService tPool;
	
	/**
	 * The CommandHandler threads
	 */
	private Thread[] handlers = new Thread[MAX_NB_THREAD];
	
	/**
	 * Shared buffer for message passing between TCPReaders and CommandHandler
	 */
	private MessageBuffer buffer = new MessageBuffer();
	
	private TCPAcceptor() {
		try {
			System.err.println(getClass().getName()  + ": Binding socket on localhost:" + PORT + "...");
			socket = new ServerSocket(PORT);
			
			
			System.err.println(getClass().getName() +": Instantiating command handlers...");
			for(int i = 0 ; i < handlers.length ; i++) {
				handlers[i] = new Thread(new CommandHandler(buffer));
				handlers[i].start();
			}
			tPool = Executors.newFixedThreadPool(MAX_NB_THREAD);
		} catch(IOException e) {
			System.err.println(getClass().getName() + ": Unable to open socket on port " + PORT + ": " + e.getMessage());
			System.exit(-1);
		}
	}
	
	public synchronized static TCPAcceptor getServer() {
		if(instance == null) {
			instance = new TCPAcceptor();
		}
		return instance;
	}
	
	/**
	 * Infinite loop waiting for clients connections
	 */
	public void run() {
		Socket client;
		int cid = 0;
		
		try {
			while(true) {
				client = socket.accept();
				System.err.println(getClass().getName() + ": New client connected " + cid);
				tPool.execute(new TCPReader(cid++, client, buffer));
			}			
		} catch(IOException e) {
			System.err.println(getClass().getName() + ": Error while client connecting: " +  e.getLocalizedMessage());
		}
	}
}
