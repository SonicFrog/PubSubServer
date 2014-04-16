package concurrence;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
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
	 * Global used for mutual exclusion on the while editing the hashmaps
	 */
	private ReentrantLock mapModification = new ReentrantLock();
	
	/**
	 * Subscriber list for each subject. Using sets to avoid adding the same client multiple times in the same subject.
	 */
	private Map<String, Set<Client>> data = new HashMap<>();

	/**
	 * The locks used to prevent two CommandHandler from publishing in the same topic at the same time
	 */
	private Map<String, ReentrantLock> locks = new HashMap<>();

	/**
	 * Counts the number of subscriber for the given topic
	 * @param topic
	 * @return
	 * 	The number of subscriber for a given topic
	 */
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
	 * @return
	 * 	A boolean indicating if the client was added to the topic
	 */
	public boolean addSubscriber(String topic, Client c) {
		boolean result = false;
		Logger.getLogger().print(getClass().getName() + ": Adding " + c.getName() + " to " + topic);
		mapModification.lock();
		if(!data.containsKey(topic)) {
			locks.put(topic, new ReentrantLock());
			data.put(topic, new TreeSet<Client>());		
		}
		mapModification.unlock();

		locks.get(topic).lock();
		result = data.get(topic).add(c);
		locks.get(topic).unlock();
		
		return result;
	}

	/**
	 * Removes the given client from every subject.
	 * This method is thread-safe
	 * @param c
	 * 	The client we want to remove from every subscriber list
	 */
	public void removeFromAll(Client c) {
		Logger.getLogger().print(getClass().getName() + ": Disconnecting " + c.getName());
		Iterator<String> it = data.keySet().iterator();
		String topic;
		mapModification.lock();
		while(it.hasNext()) {
			topic = it.next();
			locks.get(topic).lock();
			data.get(topic).remove(c);
			locks.get(topic).unlock();
		}
		mapModification.unlock();
		cleanup();
	}
	
	public void cleanup() {
		Iterator<Set<Client>> it = data.values().iterator();
		
		mapModification.lock();
		while(it.hasNext()) {
			if(it.next().size() == 0) {
				it.remove();
			}
		}
		mapModification.unlock();		
	}

	/**
	 * Removes a client from a subject's client.
	 * This method is thread-safe.
	 * @param topic
	 * 	The topic from which the client unsubscribes
	 * @param c
	 * 	The client wishing to unsubscribe
	 * @return 
	 * 	Whether the client was removed from the given topic
	 */
	public boolean removeSubscriber(String topic, Client c) {
		boolean result = false;
		Set<Client> subs = null;
		Logger.getLogger().print(getClass().getName() + ": Removing " + c.getName() + " from " + topic);
		ReentrantLock topicLock = null;

		mapModification.lock();
		if(data.containsKey(topic)) {
			topicLock = locks.get(topic);
			topicLock.lock();
			subs = data.get(topic);
		}
		mapModification.unlock();
			
		if(subs != null)
			result = subs.remove(c);
		if(topicLock != null)
			topicLock.unlock();
		cleanup();
		
		return result;
	}

	/**
	 * Locks a topic for publishing, if the subscriber list for the given topic was empty
	 * no lock is acquired
	 * This method is thread-safe.
	 * @param topic
	 * 	The topic to publish the message to
	 * @return
	 * 	The list of the subscriber for this topic or
	 * null if there was no subscriber
	 */
	public Set<Client> startPublish(String topic) {
		Logger.getLogger().print(getClass().getName() + ": Publishing to " + topic);
		
		mapModification.lock();
		if(data.containsKey(topic)) {
			locks.get(topic).lock();
		}
		mapModification.unlock();
		return data.get(topic);
	}
	
	/**
	 * Releases a topic
	 * @param topic
	 */
	public void endPublish(String topic) {
		if(locks.get(topic).isHeldByCurrentThread()) {
			locks.get(topic).unlock();
		} else {
			Logger.getLogger().print(getClass().getName() + ": Publishing to " + topic);
		}
	}
}
