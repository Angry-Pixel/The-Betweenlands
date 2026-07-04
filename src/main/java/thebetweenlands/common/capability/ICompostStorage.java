package thebetweenlands.common.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Clearable;
import net.neoforged.neoforge.common.util.INBTSerializable;

public interface ICompostStorage extends INBTSerializable<CompoundTag>, Clearable {

	/**
	 * Gets the amount of compost that has not yet finished processing
	 * @return
	 */
	public int getRemainingCompost();
	
	/**
	 * Returns how much compost has finished being processed
	 * @return
	 */
	public int getAvailableCompost();

	/**
	 * Gets the total amount of compost to be processed
	 * @return
	 */
	public default int getTotalCompost() {
		return this.getAvailableCompost() + this.getRemainingCompost();
	}
	
	/**
	 * Tries to remove the specified amount of compost
	 * @param amount Amount to extract (may be greater than the total amount of compost)
	 * @param simulate If true, the extraction is only simulated
	 * @return Amount of compost extracted
	 */
	public int removeCompost(int amount, boolean simulate);

}
