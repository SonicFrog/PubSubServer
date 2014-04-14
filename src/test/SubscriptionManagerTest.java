package test;

import org.junit.Test;

import concurrence.Client;
import concurrence.SubscriptionManager;

public class SubscriptionManagerTest {
	private SubscriptionManager mgr = new SubscriptionManager();
	
	private String[] topics = { "epfl" , "unil", "others", "faggots" };
	private Client client = null;
	
	@Test
	public void subscribeTest() {
		for(String topic : topics) {
			mgr.addSubscriber(topic, client);
		}
	}
}
