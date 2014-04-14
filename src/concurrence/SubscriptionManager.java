package concurrence;

import java.util.Hashtable;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Concurrently manages the list of subscribers for each subject and allows 
 * for concurrent publishing in different subjects
 * 
 * @see Client
 * @version 6.66 (the number of the beast)
 * @author ars3nic
 *
 */
public class SubscriptionManager {

	/**
	 * Subscriber list for each subject. Using sets to avoid adding the same client multiple times in the same subject.
	 */
	private Hashtable <String, Set<Client>> data = new Hashtable<>();

	/**
	 * The locks used to prevent two CommandHandler from publishing in the same topic at the same time
	 */
	private Hashtable<String, ReentrantLock> locks = new Hashtable<>();

	
	public int getSubscriberCount(String topic) {
		Set<Client> c = data.get(topic);
		return (c == null) ? 0 : c.size();
	}
	
	/**
	 * Adds a new subscriber to the given topic. 
	 * If the topic does not exist it is created.
	 * This method is thread-safe.
	 * @param topic
	 * @param c
	 */
	public void addSubscriber(String topic, Client c) {
		System.err.println(getClass().getName() + ": Adding " + c.getName() + " to " + topic);
		if(!data.containsKey(topic)) {
			locks.put(topic, new ReentrantLock());
			data.put(topic, new TreeSet<Client>());		
		}

		locks.get(topic).lock();
		if(data.get(topic).add(c)) {
			c.sendACK("subscribe", topic);
		}
		locks.get(topic).unlock();
	}

	/**
	 * Removes the given client from every subject.
	 * This method is thread-safe
	 * @param c
	 * 	The client we want to remove from every subscriber list
	 */
	public void removeFromAll(Client c) {
		System.err.println(getClass().getName() + ": Disconnecting " + c.getName());
		for(String topic : locks.keySet()) {
			locks.get(topic).lock();
			data.get(topic).remove(c);
			locks.get(topic).unlock();
		}
	}

	/**
	 * Removes a client from a subject's client.
	 * This method is thread-safe.
	 * @param topic
	 * 	The topic from which the client unsubscribes
	 * @param c
	 * 	The client wishing to unsubscribe
	 */
	public void removeSubscriber(String topic, Client c) {
		System.err.println(getClass().getName() + ": Removing " + c.getName() + " from " + topic);
		ReentrantLock topicLock = null;

		if(data.containsKey(topic)) {
			topicLock = locks.get(topic);
			topicLock.lock();
			Set<Client> subs = data.get(topic);
			if(subs.remove(c)) {
				c.sendACK("unsubscribe", topic);
			}
			if(subs.size() == 0) {
				data.remove(topic);
				locks.remove(topic);
			}
			else {
				topicLock.unlock();
			}
		}
	}

	/**
	 * Sends a message to every client that has subscribed to the given topic.
	 * This method is thread-safe.
	 * @param topic
	 * 	The topic to publish the message to
	 * @param message
	 * 	The message content
	 */
	public void publish(String topic, String message) {
		System.err.println(getClass().getName() + ": Publishing to " + topic);
		if(data.containsKey(topic)) {
			locks.get(topic).lock();
			for(Client c : data.get(topic)) {
				c.sendMessage(topic, message);
			}
			locks.get(topic).unlock();
		}
	}
}
