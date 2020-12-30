package thebetweenlands.api.runechain.base;

/**
 * A scheduler task that can be used to execute timed tasks. To implement timed tasks {@link IScheduler#sleep(float)}
 * can be used. The task can be terminated with {@link IScheduler#terminate()}
 * so that the node composition can continue execution.
 */
@FunctionalInterface
public interface ISchedulerTask {
	/**
	 * Updates the scheduler task
	 * @param scheduler the scheduler that is running the task
	 */
	public void update(IScheduler scheduler);
}