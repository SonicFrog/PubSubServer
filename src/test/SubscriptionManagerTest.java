package test;

import static org.junit.Assert.assertEquals;

import java.net.Socket;

import org.junit.Test;

import concurrence.Client;
import concurrence.SubscriptionManager;

public class SubscriptionManagerTest {
	private SubscriptionManager mgr = new SubscriptionManager();

	private String[] topics = { "epfl" , "unil", "others", "faggots" };
	private Client[] clients = {
			new Client(new Socket(), "client"),
			new Client(new Socket(), "client2")
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
}
