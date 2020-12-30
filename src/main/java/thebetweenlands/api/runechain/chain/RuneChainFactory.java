package thebetweenlands.api.runechain.chain;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import thebetweenlands.api.capability.IRuneCapability;
import thebetweenlands.api.runechain.base.IConfigurationLinkAccess;
import thebetweenlands.api.runechain.base.IConfigurationOutput;
import thebetweenlands.api.runechain.base.INodeBlueprint;
import thebetweenlands.api.runechain.base.INodeConfiguration;
import thebetweenlands.api.runechain.container.IRuneChainContainerData;
import thebetweenlands.api.runechain.container.IRuneContainer;
import thebetweenlands.api.runechain.container.IRuneContainerContext;
import thebetweenlands.api.runechain.container.IRuneContainerFactory;
import thebetweenlands.api.runechain.container.IRuneLink;
import thebetweenlands.api.runechain.container.IRuneWeavingTableContainer;
import thebetweenlands.api.runechain.container.gui.IRuneWeavingTableGui;
import thebetweenlands.api.runechain.rune.NodePlaceholder;
import thebetweenlands.common.herblore.rune.RuneChainComposition;
import thebetweenlands.common.registries.CapabilityRegistry;

public class RuneChainFactory implements IRuneChainFactory {
	public static final IRuneChainFactory INSTANCE = new RuneChainFactory();

	@Override
	public IRuneChainBlueprint create(@Nullable IRuneChainData data) {
		RuneChainComposition.Blueprint blueprint = new RuneChainComposition.Blueprint();

		if(data != null) {
			NonNullList<ItemStack> runes = data.getRuneItems();
			IRuneChainContainerData containerData = data.getContainerData();

			for(int i = 0; i < runes.size(); i++) {
				ItemStack stack = runes.get(i);

				IRuneCapability runeCap = null;
				IRuneContainerFactory factory = null;

				if(!stack.isEmpty() && (runeCap = stack.getCapability(CapabilityRegistry.CAPABILITY_RUNE, null)) != null && (factory = runeCap.getRuneContainerFactory()) != null) {
					final int runeIndex = i;

					IRuneContainer container = factory.createContainer();

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
						public INodeConfiguration getProvisionalConfiguration() {
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

					INodeBlueprint<?, IRuneExecutionContext> nodeBlueprint = container.getBlueprint();

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

						for(INodeConfiguration configuration : nodeBlueprint.getConfigurations(linkAccess, false)) {
							if(configuration.getId() == savedConfigurationId) {
								nodeConfiguration = configuration;
								break;
							}
						}
					}

					if(nodeConfiguration == null) {
						nodeConfiguration = nodeBlueprint.getConfigurations(linkAccess, false).get(0);
					}

					//Always specify the used configuration
					blueprint.addNodeBlueprint(nodeBlueprint, nodeConfiguration);
				} else {
					blueprint.addNodeBlueprint(NodePlaceholder.Blueprint.INSTANCE);
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
		}

		return blueprint;
	}
}
