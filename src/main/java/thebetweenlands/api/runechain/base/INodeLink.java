package thebetweenlands.api.runechain.base;

/**
 * This class represents a link to a node's output
 */
public interface INodeLink {
	/**
	 * Returns the output node's index
	 * @return the output node's index
	 */
	public int getNode();
	
	/**
	 * Returns the output node's output index
	 * @return the output node's output index
	 */
	public int getOutput();
}