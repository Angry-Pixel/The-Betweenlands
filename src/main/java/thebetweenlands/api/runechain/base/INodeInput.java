package thebetweenlands.api.runechain.base;

/**
 * A node input for the currently executing node that allows retrieving the node's input values.
 */
@FunctionalInterface
public interface INodeInput {
	/**
	 * Returns the value at the specified input.
	 * @param input the input index
	 * @return value at the specified input
	 */
	public Object get(int input);
}