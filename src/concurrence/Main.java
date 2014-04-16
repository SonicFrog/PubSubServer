package concurrence;


// Ogier Bouvier & Tristan Overney

public class Main {

	public static void main(String[] args) {
		Logger.getLogger().setDebug(true); // replace true by false if you want to get rid of all sterr logs we've implemented
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
