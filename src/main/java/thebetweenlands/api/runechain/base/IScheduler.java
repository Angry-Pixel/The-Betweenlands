package thebetweenlands.api.runechain.base;

/**
 * The scheduler allows a {@link ISchedulerTask} to time actions
 * and terminate.
 */
public interface IScheduler {
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
	 * @param duration how long the execution should be paused in ticks
	 */
	public void sleep(float duration);

	/**
	 * Terminates this task's execution and lets the node
	 * composition continue execution. Termination happens after
	 * the sleep duration has run out.
	 */
	public void terminate();
}