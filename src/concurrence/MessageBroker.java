package concurrence;

import java.util.ArrayList;
import java.util.HashMap;

public class MessageBroker implements Runnable {

	private MessageBuffer buffer;
	private HashMap<String, ArrayList<Client>> subscriptions = new HashMap<>();
	
	public MessageBroker (MessageBuffer buffer) {
		this.buffer = buffer;
	}
	
	public void run() {
		while(true) {
			Message m = buffer.get();
			
			switch (m.getCmdid()) {
			case SUBSCRIBE:
				
				break;
				
			case ENDOFCLIENT:
				break;
			
			case UNSUBSCRIBE:
				break;
			
			case PUBLISH:
				break;
			}
		}
	}
}
