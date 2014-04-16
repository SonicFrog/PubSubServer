package concurrence;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.Charset;

/**
 * Used to hold the client's socket along with its name to allow easy manipulation 
 * and message sending from the subscriptions
 * @author Ogier & Tristan
 * @version π
 * @see Comparable
 *
 */
public class Client implements Comparable<Client> {

	private String name;
	private Socket sock;

	/**
	 * Default Constructor for client
	 * 
	 * @param sock
	 * 	The socket used to communicate with this client
	 * @param name
	 * 	The name of this client
	 */
	public Client(Socket sock, String name) {
		this.sock = sock;
		this.name = name;
	}

	/**
	 * Gets the name given by the server to this client useful for debug purposes
	 * @return
	 * 	A string containing the name of this client
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sends a message to this client
	 * @param topic
	 * 	The topic in which this message was posted
	 * @param message
	 * 	The message's content
	 */
	public synchronized void sendMessage(String topic, String message) throws IOException {
		Logger.getLogger().print(getClass().getName() + ": Sending message to " + getName() );
		byte[] b = (topic + " " + message + "\n" ).getBytes(Charset.forName("UTF-8"));
		sock.getOutputStream().write(b);
		sock.getOutputStream().flush();
	}


	/**
	 * Sends an ack to this client
	 * This is thread-safe
	 * @param ack_type
	 * 	The type of ack *_ack
	 * @param data
	 * 	The additionnal information about the ack (for (un)subscribe this is the subject,
	 * and for connection this is the local client name)
	 */
	public synchronized void sendACK(String ack_type, String data) throws IOException {
		Logger.getLogger().print(getClass().getName() + ": Sending ACK to " + getName() );
		byte[]  b = (ack_type + "_ack " + data + "\n").getBytes(Charset.forName("UTF-8"));
		sock.getOutputStream().write(b);			
	}

	/**
	 * Closes this client's socket
	 * @throws IOException
	 */
	public void close() throws IOException {
		sock.close();
	}

	@Override
	public int compareTo(Client arg0) {
		if(arg0.name == name) 
			return 0;
		return -1;
	}
	
	/**
	 * Gets the status of the connection for this client
	 * @return
	 * 	true if this client's socket is connected false otherwise
	 */
	public boolean isConnected() {
		return sock.isConnected();
	}
}
