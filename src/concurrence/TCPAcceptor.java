package concurrence;

import java.io.IOException;

import java.net.ServerSocket;
import java.net.Socket;

import java.util.ArrayList;

public class TCPAcceptor implements Runnable {

	private static TCPAcceptor instance = null;
	
	public static final int PORT = 7676;
	
	private ServerSocket socket;
	
	private MessageBuffer buffer = new MessageBuffer();
	
	private ArrayList<TCPReader> clients = new ArrayList<>();
	
	private TCPAcceptor() {
		try {
			socket = new ServerSocket(PORT);
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
		TCPReader chandler;
		Thread tclient;
		int cnum = 0;
		
		try {
			while(true) {
				client = socket.accept();
				chandler = new TCPReader(cnum++, client, buffer);
				clients.add(chandler);
				tclient = new Thread(chandler);
				tclient.start();
			}			
		} catch(IOException e) {
			System.err.println("Error while client connecting: " +  e.getLocalizedMessage());
		}
	}
}
