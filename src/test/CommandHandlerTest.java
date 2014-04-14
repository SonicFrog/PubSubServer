package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.Socket;

import lsr.concurrence.provided.server.CommandID;

import org.junit.BeforeClass;
import org.junit.Test;

import concurrence.Client;
import concurrence.CommandHandler;
import concurrence.Message;
import concurrence.MessageBuffer;

public class CommandHandlerTest {
	
	private static MessageBuffer buffer = new MessageBuffer();
	private static CommandHandler handler = new CommandHandler(buffer);
	private static Thread runner = new Thread(handler);
	
	private Client data = new Client(new Socket(), "client");
	
	private Message[] msgs = {
			new Message(CommandID.SUBSCRIBE, data, "epfl", "{ test }"),
			new Message(CommandID.UNSUBSCRIBE, data, "epfl", "{ test } "),
			new Message(CommandID.PUBLISH, data, "epfl", "{ test }")
	};
	
	@BeforeClass
	public static void setUp() {
		runner.start();
	}
	
	@Test
	public void insertRemoveTest() throws InterruptedException {
		assertTrue(runner.isAlive());
		buffer.put(msgs[0]);
		assertEquals(1, buffer.getCurrentSize());
		Thread.sleep(100); //Yield control to the CommandHandler
		assertEquals(0, buffer.getCurrentSize());
		assertTrue(runner.isAlive());
	}
	
	@Test
	public void allInTest() throws InterruptedException {
		assertTrue(runner.isAlive());
		for(Message m : msgs) {
			buffer.put(m);
		}
		Thread.sleep(100);
		assertTrue(runner.isAlive());
	}
}
