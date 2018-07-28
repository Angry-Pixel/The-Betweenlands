package thebetweenlands.api.rune;

public interface INodeComposition<E> {
	/**
	 * Returns the blueprint that created this composition.
	 * @return blueprint that created this composition
	 */
	public INodeCompositionBlueprint<E> getBlueprint();

	/**
	 * Returns the node at the specified index
	 * @param node - index of the node
	 * @return node at the specified index
	 */
	public INode<?, E> getNode(int node);

	/**
	 * Returns whether the node composition is executing
	 * @return whether the node composition is executing
	 */
	public boolean isRunning();

	/**
	 * Starts the execution of this node composition
	 * @param context - context that is executing the node composition
	 */
	public void run(E context);
}
