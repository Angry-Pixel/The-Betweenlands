package thebetweenlands.common.capability.pheromonebrew;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import thebetweenlands.api.capability.IInfestationIgnoreCapability;
import thebetweenlands.api.capability.ISerializableCapability;
import thebetweenlands.common.capability.base.EntityCapability;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.CapabilityRegistry;

public class InfestationIgnoreEntityCapability extends EntityCapability<InfestationIgnoreEntityCapability, IInfestationIgnoreCapability, EntityPlayer> implements IInfestationIgnoreCapability, ISerializableCapability {
	@Override
	public ResourceLocation getID() {
		return new ResourceLocation(ModInfo.ID, "infestation_ignore");
	}

	@Override
	protected Capability<IInfestationIgnoreCapability> getCapability() {
		return CapabilityRegistry.CAPABILITY_INFESTATION_IGNORE;
	}

	@Override
	protected Class<IInfestationIgnoreCapability> getCapabilityClass() {
		return IInfestationIgnoreCapability.class;
	}

	@Override
	protected InfestationIgnoreEntityCapability getDefaultCapabilityImplementation() {
		return new InfestationIgnoreEntityCapability();
	}

	@Override
	public boolean isApplicable(Entity entity) {
		return entity instanceof EntityPlayer;
	}

	private long immunityTime = -1;

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setLong("immunityTime", this.immunityTime);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		this.immunityTime = nbt.getLong("immunityTime");
	}

	@Override
	public boolean isImmune() {
		return this.getRemainingImmunityTicks() > 0;
	}

	@Override
	public int getRemainingImmunityTicks() {
		return this.immunityTime >= 0 ? Math.max(0, (int)(this.immunityTime - this.getEntity().world.getTotalWorldTime())) : 0;
	}

	@Override
	public void setImmune(int duration) {
		if(duration <= 0) {
			this.setNotImmune();
		} else {
			this.immunityTime = this.getEntity().world.getTotalWorldTime() + duration;
			this.markDirty();
		}
	}

	@Override
	public void setNotImmune() {
		if(this.immunityTime != -1) {
			this.immunityTime = -1;
			this.markDirty();
		}
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
		return 20;
	}
}
