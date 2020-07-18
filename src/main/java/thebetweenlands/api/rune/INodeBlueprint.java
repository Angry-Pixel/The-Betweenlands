package thebetweenlands.api.rune;

import java.util.List;

import javax.annotation.Nullable;

import thebetweenlands.api.rune.INodeBlueprint.INodeIO;

public interface INodeBlueprint<T extends INode<?, E>, E> {
	/**
	 * A node input for the currently executing node that allows retrieving the node's input values.
	 */
	public static interface INodeInput {
		/**
		 * Returns the value at the specified input.
		 * @param input - input index
		 * @return value at the specified input
		 */
		public Object get(int input);
	}
	
	/**
	 * A node input/output for the currently executing node that allows retrieving and setting the node's input and
	 * output values. Also allows scheduling a task that can run over an arbitrary duration while the node composition's
	 * execution is suspended.
	 */
	public static interface INodeIO extends INodeInput {
		/**
		 * A scheduler task that can be used to execute timed tasks. To implement timed tasks {@link IScheduler#sleep(float)}
		 * can be used. The task can be terminated with {@link IScheduler#terminate()}
		 * so that the node composition can continue execution.
		 */
		@FunctionalInterface
		public static interface ISchedulerTask {
			/**
			 * Updates the scheduler task
			 * @param scheduler - scheduler that is running the task
			 */
			public void update(IScheduler scheduler);
		}

		/**
		 * The scheduler allows a {@link ISchedulerTask} to time actions
		 * and terminate.
		 */
		public static interface IScheduler {
			/**
			 * Returns how many times the task has already been updated.
			 * This counter is incremented every time <i>after</i> {@link ISchedulerTask#update(IScheduler)} was called
			 * and starts at 0.
			 * @return how many times the task has already been updated
			 */
			public int getUpdateCount();

			/**
			 * Causes the task's execution to temporarily stop after returning.
			 * The task will continue execution after the specified duration.
			 * If sleep is called multiple times the duration is accumulated.
			 * @param duration - how long the execution should be paused. In ticks
			 */
			public void sleep(float duration);

			/**
			 * Terminates this task's execution and lets the node
			 * composition continue execution. Termination happens after
			 * the sleep duration has run out.
			 */
			public void terminate();
		}

		//TODO Exceptions when invalid output?
		/**
		 * Sets the value at the specified output.
		 * @param output - output index
		 * @param obj - the value
		 */
		public void set(int output, Object obj);

		/**
		 * Causes the node composition's execution to branch, i.e.
		 * any following node executions will run on the old branches
		 * and additionally on a newly created branch.
		 * Branching must be done before calling {@link #set(int, Object)}
		 * or {@link #fail(Throwable)}.
		 * @throws IllegalStateException if this method is called after {@link #set(int, Object)} or
		 * {@link #fail(Throwable)} was already called
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
		 * @param task - scheduler task to run
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

	/**
	 * Returns an unmodifiable list containing all valid configurations for
	 * nodes of this blueprint.
	 * @return an unmodifiable list containing all valid configurations for
	 * nodes of this blueprint
	 */
	public List<? extends INodeConfiguration> getConfigurations();

	/**
	 * Creates a node instance from the specified configuration.
	 * @param index - index of the node instance
	 * @param composition - node composition this blueprint belongs to
	 * @param configuration - configuration of the node. <b>Must be from this blueprint's {@link #getConfigurations()}</b>.
	 * @return a node instance with the specified configuration
	 */
	public T create(int index, INodeComposition<E> composition, INodeConfiguration configuration);

	/**
	 * Called when the node's execution has failed, e.g. when inputs are missing
	 * or {@link #run(INode, Object, INodeInput, INodeIO)} has set the state
	 * to failed.
	 * @param state - node instance created by {@link #create(INodeComposition, INodeConfiguration)}
	 * @param context - context that is executing the node
	 */
	public default void fail(T state, E context) { }

	/**
	 * Called when the node composition's execution is terminated, e.g. when all nodes and branches have finished
	 * executing or {@link INodeIO#terminate()} was called.
	 * @param state - node instance created by {@link #create(INodeComposition, INodeConfiguration)}
	 * @param context - context that was executing the node
	 */
	public default void terminate(T state, E context) { }

	/**
	 * Called when the node's function is executed.
	 * @param state - node instance created by {@link #create(INodeComposition, INodeConfiguration)}
	 * @param context - context that is executing the node
	 * @param io - node input/output that allow reading input values and  writing output values
	 */
	public void run(T state, E context, INodeIO io);

	/**
	 * Called when the node's function execution is suspended, i.e. temporarily interrupted during a running task or between calling {@link #run(INode, Object, INodeIO)}
	 * for different inputs or different branches. 
	 * @param state - node instance created by {@link #create(INodeComposition, INodeConfiguration)}
	 * @param context - context that was executing the node
	 */
	public default void suspend(T state, E context) { }
}
