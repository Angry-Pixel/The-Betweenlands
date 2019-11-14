package thebetweenlands.common.capability.decay;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.EnumDifficulty;
import net.minecraftforge.common.capabilities.Capability;
import thebetweenlands.api.capability.IDecayCapability;
import thebetweenlands.api.capability.ISerializableCapability;
import thebetweenlands.common.capability.base.EntityCapability;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.CapabilityRegistry;
import thebetweenlands.common.registries.GameruleRegistry;

public class DecayEntityCapability extends EntityCapability<DecayEntityCapability, IDecayCapability, EntityPlayer> implements IDecayCapability, ISerializableCapability {
	@Override
	public ResourceLocation getID() {
		return new ResourceLocation(ModInfo.ID, "decay");
	}

	@Override
	protected Capability<IDecayCapability> getCapability() {
		return CapabilityRegistry.CAPABILITY_DECAY;
	}

	@Override
	protected Class<IDecayCapability> getCapabilityClass() {
		return IDecayCapability.class;
	}

	@Override
	protected DecayEntityCapability getDefaultCapabilityImplementation() {
		return new DecayEntityCapability();
	}

	@Override
	public boolean isApplicable(Entity entity) {
		return entity instanceof EntityPlayer;
	}






	private DecayStats decayStats = new DecayStats(this);
	private int removedHealth = 0;
	
	@Override
	public DecayStats getDecayStats() {
		return this.decayStats;
	}
	
	@Override
	public int getRemovedHealth() {
		return this.removedHealth;
	}

	@Override
	public void setRemovedHealth(int removedHealth) {
		this.removedHealth = removedHealth;
	}
	
	@Override
	public float getMaxPlayerHealth(int decayLevel) {
		return Math.min(20f + BetweenlandsConfig.GENERAL.decayMinHealth - decayLevel, 20f);
	}

	@Override
	public float getMaxPlayerHealthPercentage(int decayLevel) {
		return BetweenlandsConfig.GENERAL.decayMinHealthPercentage + (1.0f - BetweenlandsConfig.GENERAL.decayMinHealthPercentage) * (Math.min(26f - decayLevel, 20f) - 6f) / (20.0f - 6f);
	}
	
	@Override
	public boolean isDecayEnabled() {
		return this.getEntity().getEntityWorld().getDifficulty() != EnumDifficulty.PEACEFUL &&
				GameruleRegistry.getGameRuleBooleanValue(GameruleRegistry.BL_DECAY) &&
				BetweenlandsConfig.GENERAL.useDecay &&
				(this.getEntity().dimension == BetweenlandsConfig.WORLD_AND_DIMENSION.dimensionId || BetweenlandsConfig.GENERAL.decayDimensionListSet.contains(this.getEntity().dimension)) && 
				!this.getEntity().capabilities.isCreativeMode && 
				!this.getEntity().capabilities.disableDamage;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		this.decayStats.writeNBT(nbt);
		nbt.setInteger("removedHealth", this.removedHealth);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		this.decayStats.readNBT(nbt);
		this.removedHealth = nbt.getInteger("removedHealth");
	}

	@Override
	public void writeTrackingDataToNBT(NBTTagCompound nbt) {
		this.writeToNBT(nbt);
	}

	@Override
	public void readTrackingDataFromNBT(NBTTagCompound nbt) {
		this.readFromNBT(nbt);
	}

	@Override
	public int getTrackingTime() {
		return 10;
	}
}
