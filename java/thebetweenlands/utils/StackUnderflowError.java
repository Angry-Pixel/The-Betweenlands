package thebetweenlands.utils;

/**
 * Thrown when a stack underflow occurs because an application attempted to pop a matrix stack with no matrices to pop.
 *
 * @author Paul Fulham
 */
public class StackUnderflowError extends Error {
	private static final long serialVersionUID = -6946629885006358454L;

	/**
	 * Constructs a <code>StackUnderflowError</code> with no detail message.
	 */
	public StackUnderflowError() {
		super();
	}

	/**
	 * Constructs a <code>StackUnderflowError</code> with the specified detail message.
	 *
	 * @param s
	 *            the detail message.
	 */
	public StackUnderflowError(String s) {
		super(s);
	}
}
