package thebetweenlands.common.herblore.rune;

import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.IItemHandler;
import thebetweenlands.api.item.IRuneItem;
import thebetweenlands.api.rune.INodeBlueprint;
import thebetweenlands.api.rune.INodeConfiguration;
import thebetweenlands.api.rune.gui.IRuneChainAltarContainer;
import thebetweenlands.api.rune.gui.IRuneChainAltarGui;
import thebetweenlands.api.rune.gui.IRuneContainer;
import thebetweenlands.api.rune.gui.IRuneContainerContext;
import thebetweenlands.api.rune.impl.NodeDummy;
import thebetweenlands.api.rune.impl.RuneChainComposition;
import thebetweenlands.api.rune.impl.RuneChainComposition.RuneExecutionContext;
import thebetweenlands.common.inventory.container.runechainaltar.RuneChainContainerData;

public class RuneChainData implements IItemHandler {
	private final NonNullList<ItemStack> runes;
	private final RuneChainContainerData containerData;

	public RuneChainData(NonNullList<ItemStack> runes, RuneChainContainerData containerData) {
		this.runes = NonNullList.withSize(runes.size(), ItemStack.EMPTY);
		for(int i = 0; i < runes.size(); i++) {
			this.runes.set(i, runes.get(i).copy());
		}

		this.containerData = RuneChainContainerData.readFromNBT(containerData.writeToNBT(new NBTTagCompound()));
	}

	public RuneChainComposition.Blueprint createBlueprint() {
		RuneChainComposition.Blueprint blueprint = new RuneChainComposition.Blueprint();

		for(int i = 0; i < this.runes.size(); i++) {
			ItemStack stack = this.runes.get(i);

			if(!stack.isEmpty() && stack.getItem() instanceof IRuneItem) {
				final int runeIndex = i;

				IRuneItem runeItem = (IRuneItem) stack.getItem();

				IRuneContainer container = runeItem.getRuneContainerFactory(stack).createContainer();

				IRuneContainerContext context = new IRuneContainerContext() {
					@Override
					public IRuneChainAltarContainer getRuneChainAltarContainer() {
						return null;
					}

					@Override
					public IRuneChainAltarGui getRuneChainAltarGui() {
						return null;
					}

					@Override
					public int getRuneIndex() {
						return runeIndex;
					}

					@Override
					public ItemStack getRuneItemStack() {
						return stack;
					}

					@Override
					public NBTTagCompound getData() {
						RuneChainContainerData info = RuneChainData.this.containerData;
						NBTTagCompound nbt = info.getContainerNbt(runeIndex);
						if(nbt == null) {
							nbt = new NBTTagCompound();
						}
						return nbt;
					}

					@Override
					public void setData(NBTTagCompound nbt) { }

					@Override
					public void addSlot(Slot slot) { }

					@Override
					public INodeConfiguration getConfiguration() {
						return null;
					}

					@Override
					public void setConfiguration(INodeConfiguration configuration) { }
				};

				container.setContext(context);

				INodeBlueprint<?, RuneExecutionContext> nodeBlueprint = container.getBlueprint();

				INodeConfiguration nodeConfiguration = null;

				if(this.containerData.hasConfigurationId(runeIndex)) {
					int savedConfigurationId = this.containerData.getConfigurationId(runeIndex);

					for(INodeConfiguration configuration : nodeBlueprint.getConfigurations()) {
						if(configuration.getId() == savedConfigurationId) {
							nodeConfiguration = configuration;
							break;
						}
					}
				}

				if(nodeConfiguration == null) {
					nodeConfiguration = nodeBlueprint.getConfigurations().get(0);
				}

				//Always specify the used configuration
				blueprint.addNodeBlueprint(nodeBlueprint, nodeConfiguration);
			} else {
				blueprint.addNodeBlueprint(NodeDummy.Blueprint.INSTANCE);
			}
		}

		for(int nodeIndex = 0; nodeIndex < blueprint.getNodeBlueprints(); nodeIndex++) {
			INodeConfiguration configuration = blueprint.getNodeConfiguration(nodeIndex);

			//Configuration should not be null for any node except for dummy nodes where we don't have links anyways
			if(configuration != null) {
				for(int inputIndex = 0; inputIndex < configuration.getInputs().size(); inputIndex++) {
					RuneChainContainerData.Link link = this.containerData.getLink(nodeIndex, inputIndex);
	
					if(link != null) {
						blueprint.link(nodeIndex, inputIndex, link.getOutputRune(), link.getOutput());
					}
				}
			}
		}

		return blueprint;
	}

	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt.setTag("container", this.containerData.writeToNBT(new NBTTagCompound()));

		NBTTagList itemsNbtList = new NBTTagList();
		for (int i = 0; i < this.runes.size(); i++) {
			if (!this.runes.get(i).isEmpty()) {
				NBTTagCompound itemTag = new NBTTagCompound();
				itemTag.setInteger("Slot", i);
				this.runes.get(i).writeToNBT(itemTag);
				itemsNbtList.appendTag(itemTag);
			}
		}
		nbt.setTag("Items", itemsNbtList);
		nbt.setInteger("Size", this.runes.size());

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
	public int getSlots() {
		return this.runes.size();
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return this.runes.get(slot);
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		return stack;
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		return ItemStack.EMPTY;
	}

	@Override
	public int getSlotLimit(int slot) {
		return 64;
	}
	
	public RuneChainContainerData copyContainerData() {
		return RuneChainContainerData.readFromNBT(this.containerData.writeToNBT(new NBTTagCompound()));
	}
}
