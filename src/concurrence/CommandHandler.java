package concurrence;

import java.io.IOException;
import java.util.ConcurrentModificationException;
import java.util.Set;


public class CommandHandler implements Runnable {

	private MessageBuffer buffer;

	private static SubscriptionManager subs = new SubscriptionManager();

	public CommandHandler (MessageBuffer buffer) {
		Logger.getLogger().print(getClass().getName() + ": Created!");
		this.buffer = buffer;
	}

	public void run() {
		while(true) {
			Message m = buffer.get();

			Logger.getLogger().print(getClass().getName() + "[" + Thread.currentThread().getId() + "]: Handling message from " + m.getClient().getName()); 

			try {
				switch (m.getCmdid()) {
				case SUBSCRIBE:
					if(subs.addSubscriber(m.getTopic(), m.getClient())) {
						m.getClient().sendACK("subscribe", m.getTopic());
					}
					break;

				case ENDOFCLIENT:
					subs.removeFromAll(m.getClient());
					break;

				case UNSUBSCRIBE:
					if(subs.removeSubscriber(m.getTopic(), m.getClient())) {
						m.getClient().sendACK("unsubscribe", m.getTopic());
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
					Logger.getLogger().print(getClass().getName() + ": Fatal error reading message(" + m.getCmdid() +") from " + m.getClient().getName());
				}
			} catch (IOException e) {
				Logger.getLogger().print(getClass().getName() + ": Error sending/recving with " + m.getClient().getName());
				subs.removeFromAll(m.getClient());
				try {
					m.getClient().close();
				} catch (IOException e1) {
					Logger.getLogger().print(getClass().getName() + ": Additionnal error while closing socket!");
				}
			} catch (ConcurrentModificationException e) {
				e.printStackTrace();
			}

			Logger.getLogger().print(getClass().getName() + "[" + Thread.currentThread().getId() + "]: Finished handling message from " + m.getClient().getName());
		}
	}
}
