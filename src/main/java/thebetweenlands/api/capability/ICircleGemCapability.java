package thebetweenlands.api.capability;

import java.util.List;

import javax.annotation.Nonnull;

import thebetweenlands.common.capability.circlegem.CircleGem;

public interface ICircleGemCapability {
	/**
	 * Returns whether the specified gem can be added
	 * @param gem
	 * @return
	 */
	public boolean canAdd(CircleGem gem);

	/**
	 * Adds the specified gem
	 * @param gem
	 */
	public void addGem(CircleGem gem);

	/**
	 * Removes the first occurrence of the specified gem
	 * @param gem
	 * @return True if the gem was removed
	 */
	public boolean removeGem(CircleGem gem);

	/**
	 * Removes all gems
	 * @return True if a gem was removed
	 */
	public boolean removeAll();

	/**
	 * Returns an unmodifiable list of all gems
	 * @return
	 */
	@Nonnull
	public List<CircleGem> getGems();
}
