package thebetweenlands.common.capability.circlegem;

import java.util.List;

import javax.annotation.Nonnull;

import thebetweenlands.common.capability.base.ISerializableCapability;

public interface ICircleGemCapability extends ISerializableCapability {
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
	 * Removes a gem
	 * @param gem True if the gem was removed
	 * @return
	 */
	public boolean removeGem(CircleGem gem);

	/**
	 * Removes all gems
	 */
	public void removeAll();

	/**
	 * Returns an unmodifiable list of all gems
	 * @return
	 */
	@Nonnull
	public List<CircleGem> getGems();
}
