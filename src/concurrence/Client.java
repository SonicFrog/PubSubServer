package concurrence;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.Charset;

/**
 * Used to hold the client's socket along with its name to allow easy manipulation 
 * and message sending from the subscriptions
 * @author ars3nic
 * @see Comparable
 *
 */
public class Client implements Comparable<Client> {

	private String name;
	private Socket sock;

	public Client(Socket sock, String name) {
		this.sock = sock;
		this.name = name;
	}

	/**
	 * Gets the name given by the server to this client useful for debug purposes
	 * @return
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
	public synchronized void sendMessage(String topic, String message) {
		try {
			System.err.println(getClass().getName() + ": Sending message to " + getName() );
			byte[] b = (topic + " " + message + "\n" ).getBytes(Charset.forName("UTF-8"));
			sock.getOutputStream().write(b);
		} catch (IOException e) {
			System.err.println("Client " + name + " failed to receive a message");
		}
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
	public synchronized void sendACK(String ack_type, String data) {
		try {
			System.err.println(getClass().getName() + ": Sending ACK to " + getName() );
			byte[]  b = (ack_type + "_ack " + data + "\n").getBytes(Charset.forName("UTF-8"));
			sock.getOutputStream().write(b);			
		} catch (IOException e) {
			System.err.println("Client " + name + " failed to receive a message");
		}
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
}
