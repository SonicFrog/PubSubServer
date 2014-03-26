package concurrence;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import lsr.concurrence.provided.server.InputFormatException;
import lsr.concurrence.provided.server.InputReader;

/**
 * Runnable that handles the communication with one client
 * 
 * @author ars3nic
 * @version 1.0
 *
 */
public class TCPReader implements Runnable {

	private InputReader reader;
	private DataOutputStream out;

	private Client client;

	private MessageBuffer buffer;
	
	/**
	 * Instantiates a new ClientHandler
	 * @param in We need to have only the socket to communicate with the client
	 */
	public TCPReader(int number, Socket sock, MessageBuffer buffer) throws IOException {
		reader = new InputReader(sock.getInputStream());
		client = new Client(sock, "name" + number);
		out = new DataOutputStream(sock.getOutputStream());
		this.buffer = buffer;
	}

	/**
	 * Handles reading messages from one client
	 */
	public void run() {
	
		try {
			while(true) {
				try {
					Message m = Message.fromReader(reader, client);
					reader.readCommand();
					switch(reader.getCommandId()) {
					case NEWCLIENT :
						out.writeChars("connection_ack " + client.getName());
						break;
						
					case ENDOFCLIENT:
						buffer.put(m);
						client.getSocket().close();
						return;
						
						default: 
							buffer.put(m);
					}
				} catch (InputFormatException e) {
					System.err.println("InputFormatException skipping!");
				}
			}
		} catch (IOException e) {
			System.err.println("Error while reading from " + client.getName()  + ": " + e.getLocalizedMessage());
		}
	}
}
