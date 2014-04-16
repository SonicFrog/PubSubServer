package concurrence;

import java.io.PrintStream;

/**
 * Debug printer class
 * 
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
	
	/**
	 * Gets the global Logger instance
	 * @return
	 * 	A ready to log Logger
	 */
	public synchronized static Logger getLogger() {
		if(instance == null) {
			instance = new Logger(System.err);
		}
		return instance;
	}
	
	/**
	 * Prints a formatted message to the debug log
	 * @param message
	 */
	public synchronized void print(String message) {
		if(output) {
			out.println(message);
			out.flush();
		}
	}
	
	/**
	 * Sets the status for this Logger
	 * @param status true for full debug output
	 * or false for no debug logging
	 */
	public void setDebug(boolean status) {
		output = status;
	}
}
