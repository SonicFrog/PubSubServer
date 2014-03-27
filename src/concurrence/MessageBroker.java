package concurrence;

import java.util.HashMap;
import java.util.Vector;

public class MessageBroker implements Runnable {

	private MessageBuffer buffer;
	private HashMap<String, Vector<Client>> subscriptions = new HashMap<>();
	
	public MessageBroker (MessageBuffer buffer) {
		this.buffer = buffer;
	}
	
	/**
	 * Ajoute un client dans la liste d'abonné pour le sujet donné
	 * @param subject
	 * @param client
	 * @return
	 * 	true si le client n'était pas déjà abonné et false sinon
	 */
	private boolean subscribeClient(String subject, Client client) {
		Vector<Client> destination = subscriptions.get(subject);
		if(destination == null) {
			destination = subscriptions.put(subject, new Vector<Client>());
		}
		if(!destination.contains(client)) {
			destination.add(client);
			return true;
		}
		return false;
	}
	
	/**
	 * Fonctionne de la même manière que subscribeClient sauf que c'est 
	 * pour le désabonnement ;)
	 * @param subject
	 * @param client
	 * @return
	 */
	private boolean unSubscribeClient(String subject, Client client) {
		Vector<Client> subscribers = subscriptions.get(subject);
		if(subscribers == null || !subscribers.contains(client)) {
			return false;
		}
		subscribers.remove(client);
		return true;
	}
	
	/**
	 * Enlève un client de toutes les listes d'abonnés
	 * @param client
	 */
	private void removeClient(Client client) {
		for(Vector<Client> v : subscriptions.values()) {
			v.remove(client);
		}
	}
	
	/**
	 * Publie un message dans le sujet donné
	 * @param topic
	 * @param message
	 */
	private void sendMessage(String topic, String message) {
		
	}
	
	public void run() {
		while(true) {
			Message m = buffer.get();
			
			switch (m.getCmdid()) {
			case SUBSCRIBE:
				subscribeClient(m.getTopic(), m.getClient());
				break;
				
			case ENDOFCLIENT:
				removeClient(m.getClient());
				break;
			
			case UNSUBSCRIBE:
				unSubscribeClient(m.getTopic(), m.getClient());
				break;
			
			case PUBLISH:
				sendMessage(m.getTopic(), m.getMessage());
				break;
			}
		}
	}
}
