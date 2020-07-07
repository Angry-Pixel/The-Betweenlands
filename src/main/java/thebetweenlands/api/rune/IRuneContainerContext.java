package thebetweenlands.api.rune;

import javax.annotation.Nullable;

import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public interface IRuneContainerContext {
	@Nullable
	public IRuneWeavingTableContainer getRuneWeavingTableContainer();

	@Nullable
	public IRuneWeavingTableGui getRuneWeavingTableGui();

	public int getRuneIndex();

	public ItemStack getRuneItemStack();

	/**
	 * Returns the NBT data that is associated with the rune container this context belongs to.
	 * @return NBT data associated with the rune container this context belongs to
	 */
	public NBTTagCompound getData();

	/**
	 * Sets the NBT data that is associated with the rune container this context belongs to.
	 * If this data changes and affects the rune container's {@link IRuneContainer#getBlueprint()}, {@link IRuneWeavingTableContainer#onRuneChanged(int)} must be called
	 * if the rune weaving table container is not <code>null</code>.
	 * @param nbt - NBT data that is associated with the rune container this context belongs to
	 */
	public void setData(NBTTagCompound nbt);

	public void addSlot(Slot slot);

	/**
	 * Returns the configuration to be used for the rune.
	 * Returns <code>null</code> before {@link IRuneContainer#init()} is called.
	 * @return configuration to be used for the rune
	 */
	@Nullable
	public INodeConfiguration getConfiguration();
	
	/**
	 * Sets the configuration to be used for the rune.
	 * @param configuration - configuration to be used for the rune
	 */
	public void setConfiguration(INodeConfiguration configuration);
}
