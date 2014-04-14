package concurrence;

/**
 * Concurrent message buffer yay
 * 
 * @author ars3nic
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
	 */
	public synchronized void put(Message m) {
		try {
			while (counter == BUF_SIZE) {
				System.err.println(getClass().getName() + ": Buffer is full");
				wait();
			}
			buf[input] = m;
			counter++;
			input = (input + 1) % BUF_SIZE;
			System.err.println(getClass().getName() + ": Added message to buffer");
			notifyAll();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets a message from the queue
	 * Blocks if there is no message to get
	 * @return
	 */
	public synchronized Message get() {
		try {
			while(counter == 0) {
				System.err.println(getClass().getName() + ": No message to get in buffer! Waiting...");
				wait();
			}
			Message m = buf[output];
			output = (output +  1) % BUF_SIZE;
			counter--;
			System.err.println(getClass().getName() + ": Got a message from buffer");
			notifyAll();
			return m;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public int getCurrentSize() {
		return counter;
	}
}
