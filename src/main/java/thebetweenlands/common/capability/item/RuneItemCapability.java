package thebetweenlands.common.capability.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import thebetweenlands.api.capability.IRuneCapability;
import thebetweenlands.api.rune.IRuneContainerFactory;
import thebetweenlands.common.capability.base.ItemCapability;
import thebetweenlands.common.item.herblore.ItemRune;
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
		return ((ItemRune) this.getItemStack().getItem()).getRuneContainerFactory(this.getItemStack());
	}
}
