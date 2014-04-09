package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
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
	
	private Thread w = new Thread(new Runnable() {
		
		@Override
		public void run() {
			b.put(null);				
		}
	});
	
	private Thread r = new Thread(new Runnable() {
		
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
	
	@Test
	public void maximumSizePutTest() {
		long start = System.currentTimeMillis();
		for(int i = 0 ; i < MessageBuffer.BUF_SIZE ; i++) {
			b.put(data[0]); //Filling up the buffer
		}
		Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				b.put(null);
			}
		});
		
		t.start();
		
		while(t.isAlive()) {
			if(start + BLOCK_TIME < System.currentTimeMillis()) {
				t.interrupt();
				return;
			}
		}
		
		assertTrue(false);
	}
	
	@Test(timeout=100)
	public void oneReaderOneWriterTest() throws InterruptedException {
		r.start();
		Thread.sleep(20);
		b.put(null);
		Thread.yield();
		assertFalse(r.isAlive());
	}
	
	@Test
	public void concurrentAccess() {
		//TODO
	}
}
