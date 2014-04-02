package concurrence;

import java.io.IOException;

import lsr.concurrence.provided.server.CommandID;
import lsr.concurrence.provided.server.InputReader;

public class Message {
	private Client from;
	private CommandID cmdid;
	private String topic;
	private String message;
	
	public Message(CommandID cmdid, Client from, String topic, String message) {
		this.from = from;
		this.cmdid = cmdid;
		this.topic = topic;
		this.message = message;
	}
	
	public static Message fromReader(InputReader reader, Client client) throws IOException {
		return new Message(reader.getCommandId(), client, reader.getTopic(), reader.getMessage());
	}
	
	public Client  getClient() {
		return from;
	}

	public CommandID getCmdid() {
		return cmdid;
	}


	public String getTopic() {
		return topic;
	}


	public String getMessage() {
		return message;
	}
}
