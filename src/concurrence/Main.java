package concurrence;


// Ogier Bouvier & Tristan Overney

public class Main {

	public static void main(String[] args) {
		TCPAcceptor server = TCPAcceptor.getServer();
		Thread main = new Thread(server);
		main.start();
		try {
			main.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
