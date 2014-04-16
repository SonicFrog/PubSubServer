package concurrence;

import java.io.IOException;
import java.net.Socket;

import lsr.concurrence.provided.server.InputFormatException;
import lsr.concurrence.provided.server.InputReader;

/**
 * Runnable that handles receiving messages from one client
 * 
 * @author Ogier & Tristan
 * @version 1.0
 *
 */
public class TCPReader implements Runnable {

	private InputReader reader;

	/**
	 * The Client instance this reader receives messages from
	 */
	private Client client;

	/**
	 * Reference to the shared message buffers
	 */
	private MessageBuffer buffer;

	/**
	 * TCPReader constructor
	 * 
	 * @param number
	 * 	The number of this client
	 * @param sock
	 * 	The socket for this client
	 * @param buffer
	 * 	The shared command buffer
	 * @throws IOException
	 */
	public TCPReader(int number, Socket sock, MessageBuffer buffer) throws IOException {
		reader = new InputReader(sock.getInputStream());
		client = new Client(sock, "client" + number);
		this.buffer = buffer;
	}

	/**
	 * Handles reading messages from one client
	 */
	public void run() {
		System.err.println(getClass().getName() + ": "+ client.getName() + " started by ThreadPool");
		
		try {
			client.sendACK("connection" , client.getName());
		} catch (IOException e) {
			System.err.println(getClass().getName() + ": " + client.getName() + " failed to receive ACK");
			if(!client.isConnected())
				return;
		}
		
		while(client.isConnected()) {
			try {
				reader.readCommand();
				Message m = Message.fromReader(reader, client);

				switch(m.getCmdid()) {

				case ENDOFCLIENT:
					buffer.put(m);
					client.close();
					return;

				default: 
					buffer.put(m);
				}
				
			} catch (InputFormatException e) {
				System.err.println("InputFormatException skipping!");
			} catch (IOException e) {
				System.err.println("Error while reading from " + client.getName()  + ": " + e.getLocalizedMessage());
				return;
			}
			
		}

	}
}
