package thebetweenlands.api.runechain.base;

public interface INode<T extends INode<T, E>, E> {
	/**
	 * Returns the blueprint that created this node.
	 * @return blueprint that created this node
	 */
	public INodeBlueprint<T, E> getBlueprint();
	
	/**
	 * Returns the input and output configuration of this node.
	 * @return input and output configuration of this node
	 */
	public INodeConfiguration getConfiguration();
	
	/**
	 * Returns the node composition this node belongs to.
	 * @return node composition this node belongs to
	 */
	public INodeComposition<E> getComposition();
	
	/**
	 * Returns the index of this node in the node composition it belongs to.
	 * @return index of this node in the node composition it belongs to
	 */
	public int getIndex();
}
