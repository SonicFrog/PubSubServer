package concurrence;

import java.io.IOException;
import java.util.ConcurrentModificationException;
import java.util.Set;


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

			try {
				switch (m.getCmdid()) {
				case SUBSCRIBE:
					if(subs.addSubscriber(m.getTopic(), m.getClient())) {
						m.getClient().sendACK("subscribe", m.getTopic());
						System.err.println(getClass().getName() + ": " + m.getClient().getName() + " failed to receive a message");
						subs.removeFromAll(m.getClient());
					}
					break;

				case ENDOFCLIENT:
					subs.removeFromAll(m.getClient());
					break;

				case UNSUBSCRIBE:
					if(subs.removeSubscriber(m.getTopic(), m.getClient())) {
						m.getClient().sendACK("unsubscribe", m.getTopic());
						System.err.println(getClass().getName() + ": " + m.getClient().getName() + " failed to receive a message");
						subs.removeFromAll(m.getClient());
					}
					break;

				case PUBLISH:
					Set<Client> dest = subs.startPublish(m.getTopic());
					if(dest == null)
						continue;

					for(Client c : dest) {
						c.sendMessage(m.getTopic(), m.getMessage());
					}
					subs.endPublish(m.getTopic());

					break;				

				default:
					System.err.println(getClass().getName() + ": Fatal error reading message(" + m.getCmdid() +") from " + m.getClient().getName());
				}
			} catch (IOException e) {
				System.err.println(getClass().getName() + ": Error sending/recving with " + m.getClient().getName());
				try {
					m.getClient().close();
				} catch (IOException e1) {
					System.err.println(getClass().getName() + ": Additionnal error while closing socket!");
				}
			} catch (ConcurrentModificationException e) {
				e.printStackTrace();
			}

			System.err.println(getClass().getName() + ": Finished handling message from " + m.getClient().getName());
		}
	}
}
