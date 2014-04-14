package test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Random;
import java.util.Vector;

import concurrence.TCPAcceptor;
import lsr.concurrence.provided.tests.ClientInputReader;

public class MultiTest {

	private static Random rnd = new Random();

	public static final int CLIENT_NUM = 15;

	private static final String[] groups = { "epfl", "unil", "autre" };

	private Vector<Thread> allThem = new Vector<>();
	private Thread server;

	public static void main(String[] args) {
		MultiTest t = new MultiTest();
		t.setUp();
		t.setUpClient();
		t.join();
	}

	//Setting up the server
	public void setUp() {
		TCPAcceptor server = TCPAcceptor.getServer();
		this.server = new Thread(server);
		this.server.start();
	}

	public void setUpClient() {
		for(int i = 0 ; i < CLIENT_NUM ; i++) {
			try {
				allThem.add(new Thread(new TestClient()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		for(Thread t : allThem) {
			t. start();
		}
	}

	public void join() {
		try {
			server.join();
		} catch (InterruptedException e) {

		}
	}

	public static class TestClient implements Runnable {

		private String name;
		private Socket sock;
		private OutputStream out;
		private Reader r;

		public TestClient() throws IOException {
			sock = new Socket(InetAddress.getLocalHost(), 7676);
			out = sock.getOutputStream();
			name = String.valueOf(rnd.nextInt());
			r = new Reader(sock.getInputStream());
			new Thread(r).start();
		}

		public static class Reader implements Runnable {

			private ClientInputReader r;

			public Reader(InputStream in) {
				r = new ClientInputReader(in);
			}

			public void run() {
				while(true) {
					try {
						r.readMessage();
						System.out.println(r.getRawData());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

		public void run() {
			while(true) {
				try {
					Thread.sleep(rnd.nextInt(500));
					int n = rnd.nextInt(3);
					Charset chr = Charset.forName("UTF-8");
					
					switch (n) {
					case 0: //Send a subscribe
						System.out.println("Sending subscribe");
						out.write(("subscribe " + groups[rnd.nextInt(3)] + "\n").getBytes(chr));
						break;

					case 1: //Send an unsubscribe
						System.out.println("Sending unsubscribe");
						out.write(("unsubscribe " + groups[rnd.nextInt(3)] + "\n").getBytes(chr));
						break;

					case 2: //Send a publish
						System.out.println("Publishing");
						out.write(("publish " + groups[rnd.nextInt(3)] + " { " + name + " }\n").getBytes(chr));
						break;

					default:
						throw new AssertionError("The random number generator generated an invalid number!!");
						//This never happens
					}
					out.flush();
				} catch (InterruptedException e) {
					e.printStackTrace(System.out);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
