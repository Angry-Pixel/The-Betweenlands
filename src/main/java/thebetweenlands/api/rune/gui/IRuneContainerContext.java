package thebetweenlands.api.rune.gui;

import javax.annotation.Nullable;

import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import thebetweenlands.api.rune.INodeConfiguration;

public interface IRuneContainerContext {
	@Nullable
	public IRuneChainAltarContainer getRuneChainAltarContainer();

	@Nullable
	public IRuneChainAltarGui getRuneChainAltarGui();

	public int getRuneIndex();

	public ItemStack getRuneItemStack();

	public NBTTagCompound getData();

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
