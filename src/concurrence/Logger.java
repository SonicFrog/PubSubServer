package concurrence;

import java.io.PrintStream;

/**
 * Debug printer to allow for easy toggling of console flood
 * @author ars3nic
 *
 */
public class Logger {

	public static Logger instance = null;
	
	private PrintStream out; 
	private boolean output = false;
	
	private Logger(PrintStream out) {
		this.out = out;
	}
	
	public synchronized static Logger getLogger() {
		if(instance == null) {
			instance = new Logger(System.err);
		}
		return instance;
	}
	
	public void print(String message) {
		if(output)
			out.println(message);
	}
	
	public void setDebug(boolean status) {
		output = status;
	}
}
