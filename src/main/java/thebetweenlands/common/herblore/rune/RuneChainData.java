package thebetweenlands.common.herblore.rune;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.Constants;
import thebetweenlands.api.rune.IRuneChainContainerData;
import thebetweenlands.api.rune.IRuneChainData;
import thebetweenlands.common.inventory.container.runechainaltar.RuneChainContainerData;

public class RuneChainData implements IRuneChainData {
	private final NonNullList<ItemStack> runes;
	private final RuneChainContainerData containerData;
	
	public RuneChainData(NonNullList<ItemStack> runes, IRuneChainContainerData containerData) {
		this.runes = NonNullList.withSize(runes.size(), ItemStack.EMPTY);
		for(int i = 0; i < runes.size(); i++) {
			this.runes.set(i, runes.get(i).copy());
		}

		this.containerData = RuneChainContainerData.readFromNBT(RuneChainContainerData.writeToNBT(containerData, new NBTTagCompound()));
	}

	public static NBTTagCompound writeToNBT(IRuneChainData data, NBTTagCompound nbt) {
		nbt.setTag("container", RuneChainContainerData.writeToNBT(data.getContainerData(), new NBTTagCompound()));

		NBTTagList itemsNbtList = new NBTTagList();
		NonNullList<ItemStack> runes = data.getRuneItems();
		for (int i = 0; i < runes.size(); i++) {
			if (!runes.get(i).isEmpty()) {
				NBTTagCompound itemTag = new NBTTagCompound();
				itemTag.setInteger("Slot", i);
				runes.get(i).writeToNBT(itemTag);
				itemsNbtList.appendTag(itemTag);
			}
		}
		nbt.setTag("Items", itemsNbtList);
		nbt.setInteger("Size", runes.size());

		return nbt;
	}

	public static RuneChainData readFromNBT(NBTTagCompound nbt) {
		int size = nbt.hasKey("Size", Constants.NBT.TAG_INT) ? nbt.getInteger("Size") : 0;

		NonNullList<ItemStack> runes = NonNullList.withSize(size, ItemStack.EMPTY);

		NBTTagList itemsNbtList = nbt.getTagList("Items", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < itemsNbtList.tagCount(); i++) {
			NBTTagCompound itemTag = itemsNbtList.getCompoundTagAt(i);
			int slot = itemTag.getInteger("Slot");
			if (slot >= 0 && slot < runes.size()) {
				runes.set(slot, new ItemStack(itemTag));
			}
		}

		RuneChainContainerData containerData = RuneChainContainerData.readFromNBT(nbt.getCompoundTag("container"));

		return new RuneChainData(runes, containerData);
	}

	@Override
	public IRuneChainContainerData getContainerData() {
		return this.containerData;
	}

	@Override
	public NonNullList<ItemStack> getRuneItems() {
		return this.runes;
	}
}
