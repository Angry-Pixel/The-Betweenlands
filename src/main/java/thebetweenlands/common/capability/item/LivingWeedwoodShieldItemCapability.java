package thebetweenlands.common.capability.item;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import thebetweenlands.common.capability.base.ItemCapability;
import thebetweenlands.common.item.shields.ItemLivingWeedwoodShield;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.CapabilityRegistry;

public class LivingWeedwoodShieldItemCapability extends ItemCapability<LivingWeedwoodShieldItemCapability, ILivingWeedwoodShieldCapability> implements ILivingWeedwoodShieldCapability {
	public static final ResourceLocation ID = new ResourceLocation(ModInfo.ID, "living_weedwood_shield");

	@Override
	public boolean isApplicable(Item item) {
		return item instanceof ItemLivingWeedwoodShield;
	}

	@Override
	public ResourceLocation getID() {
		return ID;
	}

	@Override
	protected LivingWeedwoodShieldItemCapability getDefaultCapabilityImplementation() {
		return new LivingWeedwoodShieldItemCapability();
	}

	@Override
	protected Capability<ILivingWeedwoodShieldCapability> getCapability() {
		return CapabilityRegistry.CAPABILITY_LIVING_WEEDWOOD_SHIELD;
	}

	@Override
	protected Class<ILivingWeedwoodShieldCapability> getCapabilityClass() {
		return ILivingWeedwoodShieldCapability.class;
	}


	private int spitTicks;
	private int spitCooldown;

	@Override
	public int getSpitTicks() {
		return this.spitTicks;
	}

	@Override
	public void setSpitTicks(int ticks) {
		this.spitTicks = ticks;
	}

	@Override
	public int getSpitCooldown() {
		return this.spitCooldown;
	}

	@Override
	public void setSpitCooldown(int cooldown) {
		this.spitCooldown = cooldown;
	}
}
