package thebetweenlands.common.capability.item;

import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import thebetweenlands.api.rune.impl.RuneChainComposition;
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

	private RuneChainData data;
	private RuneChainComposition.Blueprint blueprint;

	public static final String RUNE_CHAIN_BLUEPRINT_NBT_KEY = "thebetweenlands.runechain.blueprint";

	@Override
	protected void init() {

	}

	@Override
	public void setData(RuneChainData data) {
		this.data = data;
		this.blueprint = data.createBlueprint();

		NBTTagCompound itemNbt = this.getItemStack().getTagCompound();
		if(itemNbt == null) {
			itemNbt = new NBTTagCompound();
		}
		itemNbt.setTag(RUNE_CHAIN_BLUEPRINT_NBT_KEY, data.writeToNBT(new NBTTagCompound()));
		this.getItemStack().setTagCompound(itemNbt);
	}

	private void initFromNbt() {
		if(this.data == null) {
			NBTTagCompound itemNbt = this.getItemStack().getTagCompound();

			if(itemNbt != null) {
				this.data = RuneChainData.readFromNBT(itemNbt.getCompoundTag(RUNE_CHAIN_BLUEPRINT_NBT_KEY));
				this.blueprint = this.data.createBlueprint();
			}
		}
	}

	@Override
	public RuneChainData getData() {
		this.initFromNbt();
		return this.data;
	}

	@Override
	public RuneChainComposition.Blueprint getBlueprint() {
		this.initFromNbt();
		return this.blueprint;
	}
}
