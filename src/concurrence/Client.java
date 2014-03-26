package concurrence;

import java.net.Socket;

public class Client {

	private String name;
	private Socket sock;
	
	public Client(Socket sock, String name) {
		this.sock = sock;
		this.name = name;
	}
	
	public Socket getSocket() {
		return sock;
	}
	
	public String getName() {
		return name;
	}
}
