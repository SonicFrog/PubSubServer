package test;

import static org.junit.Assert.assertEquals;

import java.net.Socket;

import org.junit.Test;

import concurrence.Client;
import concurrence.SubscriptionManager;

public class SubscriptionManagerTest {
	
	private static final int ITER_COUNT = 5;
	private static int CLIENT_COUNT = 0;
	private SubscriptionManager mgr = new SubscriptionManager();
	
	private static int[] subCount = { 0, 0 };

	private String[] topics = { "epfl" , "unil", "others", "faggots" };
	private Client[] clients = {
			new Client(new Socket(), "client"),
			new Client(new Socket(), "client2")
	};
	
	private Runnable subR = new Runnable() {		
		@Override
		public void run() {
			int p = 0;
			int c = CLIENT_COUNT;
			CLIENT_COUNT = CLIENT_COUNT + 1;
			System.err.println(c);
			for(int i = 0 ; i < ITER_COUNT ; i++) {
				mgr.addSubscriber(topics[p], clients[c]);
				subCount[c]++;
				mgr.removeSubscriber(topics[p], clients[c]);
				subCount[c]--;
			}
		}
	};

	@Test
	public void subscribeThenRemoveAllTest() {
		for(String topic : topics) {
			mgr.addSubscriber(topic, clients[0]);
		}

		for(String topic : topics) {
			assertEquals(1, mgr.getSubscriberCount(topic));
		}
		mgr.removeFromAll(clients[0]);
		for(String topic : topics) {
			assertEquals(0, mgr.getSubscriberCount(topic));
		}
	}

	@Test
	public void duSubscribeTest() {
		for(String topic : topics) {
			for(Client c : clients) {
				mgr.addSubscriber(topic, c);
			}
		}
		for(String topic : topics) {
			assertEquals(clients.length, mgr.getSubscriberCount(topic));
		}
	}

	@Test
	public void unsubscribeTest() {
		for(String topic : topics) {
			for(int i = 0 ; i < 10 ; i++) {
				mgr.removeSubscriber(topic, clients[0]);
			}
		}
		for(String topic : topics) {
			assertEquals(0, mgr.getSubscriberCount(topic));
		}
	}
	
	@Test
	public void concurrentSubUnsubTest() throws InterruptedException {
		Thread t1 = new Thread(subR);
		Thread t2 = new Thread(subR);
		
		t1.start();
		Thread.sleep(100);
		t2.start();
		Thread.sleep(100);
		
		while(t1.isAlive() || t2.isAlive()) {
			assertEquals(subCount[0] + subCount[1], mgr.getSubscriberCount(topics[0]));
		}
	}
}
