package concurrence;

/**
 * Concurrent message buffer yay
 * 
 * @author Ogier & Tristan
 * @version 3 - 2i
 *
 */
public class MessageBuffer {

	public static final int BUF_SIZE = 10;

	private int input = 0, output = 0;

	private int counter = 0;

	private Message[] buf = new Message[BUF_SIZE];

	/**
	 * Puts a message into the queue 
	 * Blocking if there is no place into the queue
	 * 
	 * @param m
	 * 	The message to be queued in this buffer
	 */
	public synchronized void put(Message m) {
		try {
			while (counter == BUF_SIZE) {
				Logger.getLogger().print(getClass().getName() + ": Buffer is full");
				wait();
			}
			buf[input] = m;
			counter++;
			input = (input + 1) % BUF_SIZE;
			Logger.getLogger().print(getClass().getName() + ": Added message to buffer");
			notifyAll();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets a message from the queue
	 * Blocks if there is no message to get
	 * @return
	 * 	The first message that was queued or null if the thread was interrupted
	 */
	public synchronized Message get() {
		try {
			while(counter == 0) {
				Logger.getLogger().print(getClass().getName() + ": No message to get in buffer! Waiting...");
				wait();
			}
			Message m = buf[output];
			output = (output +  1) % BUF_SIZE;
			counter--;
			Logger.getLogger().print(getClass().getName() + ": Got a message from buffer");
			notifyAll();
			return m;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Gets the current number of messages waiting in the buffer
	 * @return
	 * 	An integer representing the number of messages currently in the buffer
	 */
	public int getCurrentSize() {
		return counter;
	}
}
