package concurrence;

import lsr.concurrence.provided.server.CommandID;
import lsr.concurrence.provided.server.InputReader;

public class Message {
	private String from;
	private CommandID cmdid;
	private String topic;
	private String message;
	
	public Message(CommandID cmdid, String from, String topic, String message) {
		this.from = from;
		this.cmdid = cmdid;
		this.topic = topic;
		this.message = message;
	}
	
	public static Message fromReader(InputReader reader, String name) {
		return new Message(reader.getCommandId(), name, reader.getTopic(), reader.getMessage());
	}
	
	public String getFrom() {
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
