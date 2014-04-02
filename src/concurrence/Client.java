package concurrence;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.Charset;

public class Client implements Comparable<Client> {

	private String name;
	private Socket sock;

	public Client(Socket sock, String name) {
		this.sock = sock;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	/**
	 * Sends a message to this client
	 * @param topic
	 * @param message
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

	public synchronized void sendACK(String ack_type, String data) {
		try {
			System.err.println(getClass().getName() + ": Sending ACK to " + getName() );
			byte[]  b = (ack_type + "_ack " + data + "\n").getBytes(Charset.forName("UTF-8"));
			sock.getOutputStream().write(b);			
		} catch (IOException e) {
			System.err.println("Client " + name + " failed to receive a message");
		}
	}
	
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
