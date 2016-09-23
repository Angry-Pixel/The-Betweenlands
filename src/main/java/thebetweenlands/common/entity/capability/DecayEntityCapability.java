package thebetweenlands.common.entity.capability;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import thebetweenlands.common.entity.capability.base.EntityCapability;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.util.config.ConfigHandler;

public class DecayEntityCapability extends EntityCapability<DecayEntityCapability, IDecayCapability, EntityPlayer> implements IDecayCapability {
	@CapabilityInject(IDecayCapability.class)
	public static final Capability<IDecayCapability> CAPABILITY = null;

	@Override
	public ResourceLocation getID() {
		return new ResourceLocation(ModInfo.ID, "decay");
	}

	@Override
	protected Capability<IDecayCapability> getCapability() {
		return CAPABILITY;
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

	@Override
	public DecayStats getDecayStats() {
		return this.decayStats;
	}

	@Override
	public float getMaxPlayerHealth() {
		return Math.min(26f - this.decayStats.getDecayLevel(), 20f);
	}

	@Override
	public boolean isDecayEnabled() {
		return this.getEntity().dimension == ConfigHandler.dimensionId && !this.getEntity().capabilities.isCreativeMode && !this.getEntity().capabilities.disableDamage;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		this.decayStats.writeNBT(nbt);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		this.decayStats.readNBT(nbt);
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
