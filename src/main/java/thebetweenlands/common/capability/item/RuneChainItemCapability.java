package thebetweenlands.common.capability.item;

import java.util.List;

import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import thebetweenlands.api.capability.IRuneChainCapability;
import thebetweenlands.api.rune.INodeBlueprint;
import thebetweenlands.api.rune.INodeBlueprint.IConfigurationLinkAccess;
import thebetweenlands.api.rune.INodeConfiguration;
import thebetweenlands.api.rune.INodeConfiguration.IConfigurationOutput;
import thebetweenlands.api.rune.IRuneChainContainerData;
import thebetweenlands.api.rune.IRuneChainData;
import thebetweenlands.api.rune.IRuneContainer;
import thebetweenlands.api.rune.IRuneContainerContext;
import thebetweenlands.api.rune.IRuneLink;
import thebetweenlands.api.rune.IRuneWeavingTableContainer;
import thebetweenlands.api.rune.IRuneWeavingTableGui;
import thebetweenlands.api.rune.impl.NodeDummy;
import thebetweenlands.api.rune.impl.RuneChainComposition;
import thebetweenlands.api.rune.impl.RuneChainComposition.RuneExecutionContext;
import thebetweenlands.common.capability.base.ItemCapability;
import thebetweenlands.common.herblore.rune.RuneChainData;
import thebetweenlands.common.item.herblore.ItemRuneChain;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.CapabilityRegistry;

public class RuneChainItemCapability extends ItemCapability<RuneChainItemCapability, IRuneChainCapability> implements IRuneChainCapability {
	public static final ResourceLocation ID = new ResourceLocation(ModInfo.ID, "rune_chain");

	@Override
	public boolean isApplicable(Item item) {
		return item instanceof ItemRuneChain;
	}

	@Override
	public ResourceLocation getID() {
		return ID;
	}

	@Override
	protected RuneChainItemCapability getDefaultCapabilityImplementation() {
		return new RuneChainItemCapability();
	}

	@Override
	protected Capability<IRuneChainCapability> getCapability() {
		return CapabilityRegistry.CAPABILITY_RUNE_CHAIN;
	}

	@Override
	protected Class<IRuneChainCapability> getCapabilityClass() {
		return IRuneChainCapability.class;
	}

	private IRuneChainData data;
	private RuneChainComposition.Blueprint blueprint;

	public static final String RUNE_CHAIN_BLUEPRINT_NBT_KEY = "thebetweenlands.runechain.blueprint";

	@Override
	protected void init() {

	}

	@Override
	public void setData(IRuneChainData data) {
		this.data = data;

		NBTTagCompound itemNbt = this.getItemStack().getTagCompound();

		if(data != null) {
			this.blueprint =createBlueprint(data);

			if(itemNbt == null) {
				itemNbt = new NBTTagCompound();
			}

			itemNbt.setTag(RUNE_CHAIN_BLUEPRINT_NBT_KEY, RuneChainData.writeToNBT(data, new NBTTagCompound()));

			this.getItemStack().setTagCompound(itemNbt);
		} else {
			this.blueprint = null;

			if(itemNbt != null) {
				itemNbt.removeTag(RUNE_CHAIN_BLUEPRINT_NBT_KEY);
			}
		}
	}

	public static RuneChainComposition.Blueprint createBlueprint(IRuneChainData data) {
		RuneChainComposition.Blueprint blueprint = new RuneChainComposition.Blueprint();

		NonNullList<ItemStack> runes = data.getRuneItems();
		IRuneChainContainerData containerData = data.getContainerData();

		for(int i = 0; i < runes.size(); i++) {
			ItemStack stack = runes.get(i);

			if(!stack.isEmpty() && stack.hasCapability(CapabilityRegistry.CAPABILITY_RUNE, null)) {
				final int runeIndex = i;

				IRuneContainer container = stack.getCapability(CapabilityRegistry.CAPABILITY_RUNE, null).getRuneContainerFactory().createContainer();

				IRuneContainerContext context = new IRuneContainerContext() {
					@Override
					public IRuneWeavingTableContainer getRuneWeavingTableContainer() {
						return null;
					}

					@Override
					public IRuneWeavingTableGui getRuneWeavingTableGui() {
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
						IRuneChainContainerData info = containerData;
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

					@Override
					public IConfigurationLinkAccess getLinkAccess() {
						return null;
					}
				};

				container.setContext(context);

				INodeBlueprint<?, RuneExecutionContext> nodeBlueprint = container.getBlueprint();

				INodeConfiguration nodeConfiguration = null;

				IConfigurationLinkAccess linkAccess = input -> {
					IRuneLink link = containerData.getLink(runeIndex, input);
					if(link != null && link.getOutputRune() >= 0 && link.getOutputRune() < runeIndex && link.getOutput() >= 0) {
						INodeConfiguration configuration = blueprint.getNodeConfiguration(link.getOutputRune());
						if(configuration != null) {
							List<? extends IConfigurationOutput> outputs = configuration.getOutputs();
							if(link.getOutput() < outputs.size()) {
								return outputs.get(link.getOutput());
							}
						}
					}
					return null;
				};
				
				if(containerData.hasConfigurationId(runeIndex)) {
					int savedConfigurationId = containerData.getConfigurationId(runeIndex);

					for(INodeConfiguration configuration : nodeBlueprint.getConfigurations(linkAccess)) {
						if(configuration.getId() == savedConfigurationId) {
							nodeConfiguration = configuration;
							break;
						}
					}
				}

				if(nodeConfiguration == null) {
					nodeConfiguration = nodeBlueprint.getConfigurations(linkAccess).get(0);
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
					IRuneLink link = containerData.getLink(nodeIndex, inputIndex);

					if(link != null) {
						blueprint.link(nodeIndex, inputIndex, link.getOutputRune(), link.getOutput());
					}
				}
			}
		}

		return blueprint;
	}

	protected void initFromNbt() {
		if(this.data == null) {
			this.blueprint = null;

			NBTTagCompound itemNbt = this.getItemStack().getTagCompound();

			if(itemNbt != null && itemNbt.hasKey(RUNE_CHAIN_BLUEPRINT_NBT_KEY, Constants.NBT.TAG_COMPOUND)) {
				this.data = RuneChainData.readFromNBT(itemNbt.getCompoundTag(RUNE_CHAIN_BLUEPRINT_NBT_KEY));
				this.blueprint = createBlueprint(this.data);
			}
		}
	}

	@Override
	public IRuneChainData getData() {
		this.initFromNbt();
		return this.data;
	}

	@Override
	public RuneChainComposition.Blueprint getBlueprint() {
		this.initFromNbt();
		return this.blueprint;
	}
}
