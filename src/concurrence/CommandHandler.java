package concurrence;


public class CommandHandler implements Runnable {

	private MessageBuffer buffer;
	
	private static SubscriptionManager subs = new SubscriptionManager();
	
	public CommandHandler (MessageBuffer buffer) {
		System.err.println(getClass().getName() + ": Created!");
		this.buffer = buffer;
	}
	
	public void run() {
		while(true) {
			Message m = buffer.get();
			
			System.err.println(getClass().getName() + ": Handling message from " + m.getClient().getName()); 
	
			switch (m.getCmdid()) {
			case SUBSCRIBE:
				subs.addSubscriber(m.getTopic(), m.getClient());
				break;
				
			case ENDOFCLIENT:
				subs.removeFromAll(m.getClient());
				break;
			
			case UNSUBSCRIBE:
				subs.removeSubscriber(m.getTopic(), m.getClient());
				break;
			
			case PUBLISH:
				subs.publish(m.getTopic() , m.getMessage());
				break;				
			
			default:
				System.err.println(getClass().getName() + ": Fatal error reading message(" + m.getCmdid() +") from " + m.getClient().getName());
			}
			
			System.err.println(getClass().getName() + ": Finished handling message from " + m.getClient().getName());
		}
	}
}
