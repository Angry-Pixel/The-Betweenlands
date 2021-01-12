package thebetweenlands.api.runechain.base;

import java.util.List;

import javax.annotation.Nullable;

public interface INodeBlueprint<T extends INode<?, E>, E> {
	/**
	 * Returns an unmodifiable list containing all valid configurations for
	 * nodes of this blueprint.
	 * @param linkAccess allows accessing the configuration output of the node that the specified input is linked to. May be null that data is not available
	 * @param provisional whether the returned configurations may be provisional, e.g. contain potential node inputs that may not end up getting used
	 * @return an unmodifiable list containing all valid configurations for
	 * nodes of this blueprint
	 */
	public List<? extends INodeConfiguration> getConfigurations(@Nullable IConfigurationLinkAccess linkAccess, boolean provisional);

	/**
	 * Creates a node instance from the specified configuration.
	 * @param index the index of the node instance
	 * @param composition the node composition this blueprint belongs to
	 * @param configuration the configuration of the node. <b>Must be from this blueprint's {@link #getConfigurations()}</b>.
	 * @return a node instance with the specified configuration
	 */
	public T create(int index, INodeComposition<E> composition, INodeConfiguration configuration);

	/**
	 * Called when the node's execution has failed, e.g. when inputs are missing
	 * or {@link #run(INode, Object, INodeInput, INodeIO)} has set the state
	 * to failed.
	 * @param state the node instance created by {@link #create(INodeComposition, INodeConfiguration)}
	 * @param context the context that is executing the node
	 */
	public default void fail(T state, E context) { }

	/**
	 * Called when the node composition's execution is terminated, e.g. when all nodes and branches have finished
	 * executing or {@link INodeIO#terminate()} was called.
	 * @param state the node instance created by {@link #create(INodeComposition, INodeConfiguration)}
	 * @param context the context that was executing the node
	 */
	public default void terminate(T state, E context) { }

	/**
	 * Called when the node's function is executed.
	 * @param state the node instance created by {@link #create(INodeComposition, INodeConfiguration)}
	 * @param context the context that is executing the node
	 * @param io the node input/output that allow reading input values and  writing output values
	 */
	public void run(T state, E context, INodeIO io);

	/**
	 * Called when the node's function execution is suspended, i.e. temporarily interrupted during a running task or between calling {@link #run(INode, Object, INodeIO)}
	 * for different inputs or different branches. 
	 * @param state the node instance created by {@link #create(INodeComposition, INodeConfiguration)}
	 * @param context the context that was executing the node
	 */
	public default void suspend(T state, E context) { }
}
