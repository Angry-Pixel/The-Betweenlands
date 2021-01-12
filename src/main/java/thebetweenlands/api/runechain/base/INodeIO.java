package thebetweenlands.api.runechain.base;

import javax.annotation.Nullable;

/**
 * A node input/output for the currently executing node that allows retrieving and setting the node's input and
 * output values. Also allows scheduling a task that can run over an arbitrary duration while the node composition's
 * execution is suspended.
 */
public interface INodeIO extends INodeInput {
	//TODO Exceptions when invalid output?
	/**
	 * Sets the value at the specified output.
	 * This call may be deferred until the last {@link INodeBlueprint#run(INode, Object, INodeIO)} call of
	 * the same node.
	 * @param output output index
	 * @param obj the value
	 */
	public void set(int output, Object obj);

	/**
	 * Causes the node composition's execution to branch, i.e.
	 * any following node executions will run on the old branches
	 * and additionally on a newly created branch.
	 * Branching must be done before calling {@link #set(int, Object)}
	 * or {@link #fail()}.
	 * @throws IllegalStateException if this method is called after {@link #set(int, Object)} or
	 * {@link #fail()} was already called
	 */
	public void branch();

	/**
	 * Returns whether the node composition's execution is set to branch.
	 * @return whether the node composition's execution is set to branch
	 */
	public boolean branched();

	/**
	 * Sets the node's state to failed after returning. This will cause the branch that this
	 * node was executed on to fail, i.e. that branch will terminate.
	 */
	public void fail();

	/**
	 * Returns whether the node's state will be set to failed after returning.
	 * @return whether the node's state will be set to failed after returning
	 */
	public boolean failed();

	/**
	 * Schedules a task to be run immediately after returning. The task will be executed even if the node's state is set to failed.
	 * Only one task can be scheduled at once.
	 * <p>
	 * <b>Do not forget to terminate the task with {@link IScheduler#terminate()} once it has
	 * finished!</b>
	 * @param task the scheduler task to run
	 */
	public void schedule(ISchedulerTask task);

	/**
	 * Returns the currently scheduled task that will be run immediately after returning.
	 * @return the currently scheduled task that will be run immediately after returning
	 */
	@Nullable
	public ISchedulerTask scheduled();

	/**
	 * Causes the node composition's execution
	 * to permanently terminate after returning.
	 */
	public void terminate();

	/**
	 * Returns whether the node composition's execution will be
	 * permanently terminated after returning.
	 * @return whether the node composition's execution will be
	 * permanently terminated after returning
	 */
	public boolean terminated();
}