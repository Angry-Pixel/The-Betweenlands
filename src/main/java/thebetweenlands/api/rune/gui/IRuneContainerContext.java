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

	public INodeConfiguration getConfiguration();
	
	public void setConfiguration(INodeConfiguration configuration);
}
