package concurrence;

import java.util.Hashtable;
import java.util.Vector;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Concurrently manages the list of subscribers for each subject and allows 
 * for concurrent publishing in different subjects
 * 
 * @see FagLord
 * @version 9.999000
 * @author ars3nic
 *
 */
public class SubscriptionManager {

	private Hashtable <String, Vector<Client>> data = new Hashtable<>();
	
	private Hashtable<String, ReentrantLock> locks = new Hashtable<>();
	
	/**
	 * Adds a new subscriber to the given topic. 
	 * If the topic does not exist it is created.
	 * Thread-safe.
	 * @param topic
	 * @param c
	 */
	public void addSubscriber(String topic, Client c) {
		if(!data.containsKey(topic)) {
			Vector<Client> n = new Vector<Client>();
			n.add(c);
			locks.put(topic, new ReentrantLock());
			data.put(topic, n);
		} else {
			locks.get(topic).lock();
			data.get(topic).add(c);
			locks.get(topic).unlock();
		}
	}
	
	public void removeSubscriber(String topic, Client c) {
		if(data.containsKey(topic)) {
			locks.get(topic).lock();
			Vector<Client> subs = data.get(topic);
			subs.remove(c);
			if(subs.size() == 0) {
				data.remove(topic);
				locks.remove(topic);
			}
			locks.get(topic).lock();
		}
	}
	
	public void publish(String topic, String message) {
		if(data.containsKey(topic)) {
			locks.get(topic).lock();
			for(Client c : data.get(topic)) {
				c.sendMessage(topic, message);
			}
			locks.get(topic).unlock();
		}
	}
}
