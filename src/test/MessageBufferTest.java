package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import lsr.concurrence.provided.server.CommandID;

import org.junit.Before;
import org.junit.Test;

import concurrence.Client;
import concurrence.Message;
import concurrence.MessageBuffer;

/**
 * These are some basic test for the ConcurrentMessageBuffer
 * @author ars3nic
 *
 */
public class MessageBufferTest {

	public static final int BLOCK_TIME = 10000;
	
	private MessageBuffer b = new MessageBuffer();
	
	private Message[] data = new Message[3];
	
	private Thread writerThread = new Thread(new Runnable() {
		
		@Override
		public void run() {
			b.put(null);				
		}
	});
	
	private Thread readerThread = new Thread(new Runnable() {
		
		@Override
		public void run() {
			b.get();
		}
	});
	
	@Before
	public void setUp() {
		data[0] = new Message(CommandID.ENDOFCLIENT, new Client(null, null), "Blabla", "Blabla");
		data[1] = new Message(CommandID.PUBLISH, new Client(null, null), "Blibli", "Blabla");
		data[2] = new Message(CommandID.SUBSCRIBE, new Client(null, null), "Blabla", "Blibli");
	}
	
	@Test
	public void blockingWhenEmptyTest() {
		long start = System.currentTimeMillis();
		
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				b.get();		
			}
		});
		t.start();
		
		while(t.isAlive()) {
			if(System.currentTimeMillis() > start + BLOCK_TIME) {
				t.interrupt();
				return;
			}
		}
		assertEquals("The thread terminated by itself!!", true, false);
	}
	
	@Test
	public void putThenGetTest() {
		for(Message m : data) {		
			b.put(m);
			assertEquals(1, b.getCurrentSize());
			assertEquals(m, b.get());
			assertEquals(0, b.getCurrentSize());
		}
	}
	
	@Test
	public void multiplePutThenGetTest() {
		int i;
		for (i = 0 ; i < data.length ; i++) {
			b.put(data[i]);
		}
		assertEquals(data.length, b.getCurrentSize());
		for(i = 0 ; i < data.length ; i++) {
			assertEquals(data[i], b.get());
		}
	}
	
	@Test(timeout=1000)
	public void maximumSizePutTest() throws InterruptedException {
		for(int i = 0 ; i < MessageBuffer.BUF_SIZE ; i++) {
			b.put(data[0]); //Filling up the buffer
		}
		
		writerThread.start();
		
		Thread.sleep(200);
		
		readerThread.start();
		
		Thread.sleep(100);
		assertFalse(writerThread.isAlive());
		assertFalse(readerThread.isAlive());
		assertEquals(MessageBuffer.BUF_SIZE, b.getCurrentSize());
	}
	
	@Test(timeout=100)
	public void oneReaderOneWriterTest() throws InterruptedException {
		readerThread.start();
		Thread.sleep(20);
		b.put(null);
		Thread.sleep(20);
		assertFalse(readerThread.isAlive());
	}
	
	@Test
	public void concurrentAccess() {
		//TODO
	}
}
