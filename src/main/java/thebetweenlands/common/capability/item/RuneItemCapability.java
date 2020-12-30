package thebetweenlands.common.capability.item;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import thebetweenlands.api.capability.IRuneCapability;
import thebetweenlands.api.runechain.container.IRuneContainerFactory;
import thebetweenlands.common.capability.base.ItemCapability;
import thebetweenlands.common.item.herblore.rune.ItemRune;
import thebetweenlands.common.item.herblore.rune.ItemRune.RuneItemProperties;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.CapabilityRegistry;

public class RuneItemCapability extends ItemCapability<RuneItemCapability, IRuneCapability> implements IRuneCapability {
	public static final ResourceLocation ID = new ResourceLocation(ModInfo.ID, "rune");

	@Override
	public boolean isApplicable(Item item) {
		return item instanceof ItemRune;
	}

	@Override
	public ResourceLocation getID() {
		return ID;
	}

	@Override
	protected RuneItemCapability getDefaultCapabilityImplementation() {
		return new RuneItemCapability();
	}

	@Override
	protected Capability<IRuneCapability> getCapability() {
		return CapabilityRegistry.CAPABILITY_RUNE;
	}

	@Override
	protected Class<IRuneCapability> getCapabilityClass() {
		return IRuneCapability.class;
	}

	@Override
	protected void init() {

	}

	@Override
	public IRuneContainerFactory getRuneContainerFactory() {
		RuneItemProperties properties = ((ItemRune) this.getItemStack().getItem()).getProperties(this.getItemStack());
		return properties != null ? properties.getFactory(this.getItemStack()) : null;
	}
}
