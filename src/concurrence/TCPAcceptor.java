package concurrence;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TCPAcceptor implements Runnable {

	private static TCPAcceptor instance = null;
	
	public static final int PORT = 7676;
	
	private static final int MAX_NB_THREAD = 12;
	
	private ServerSocket socket;
	
	private ExecutorService tPool;
	
	private MessageBuffer buffer = new MessageBuffer();
	
	// private ArrayList<TCPReader> clients = new ArrayList<>();
	// TODO sauf erreur pas besoin de garder une listes des clients dans le "welcoming thread"
	
	private TCPAcceptor() {
		try {
			socket = new ServerSocket(PORT);
			tPool = Executors.newFixedThreadPool(MAX_NB_THREAD);
		} catch(IOException e) {
			System.err.println("Unable to open socket on port " + PORT + ": " + e.getMessage());
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
		// TCPReader chandler;   c.f l.15
		//Thread tclient;
		int cid = 0;
		
		try {
			while(true) {
				client = socket.accept();
				// chandler = new TCPReader(cid++, client, buffer);
				// clients.add(chandler); c.f. l.15
				tPool.execute(new TCPReader(cid++, client, buffer));
			}			
		} catch(IOException e) {
			System.err.println("Error while client connecting: " +  e.getLocalizedMessage());
		}
	}
}
