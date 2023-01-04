package thebetweenlands.common.capability.mud_walker;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import thebetweenlands.api.capability.IMudWalkerCapability;
import thebetweenlands.api.capability.ISerializableCapability;
import thebetweenlands.common.capability.base.EntityCapability;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.CapabilityRegistry;

public class MudWalkerCapability extends EntityCapability<MudWalkerCapability, IMudWalkerCapability, EntityPlayer> implements IMudWalkerCapability, ISerializableCapability {
	@Override
	public ResourceLocation getID() {
		return new ResourceLocation(ModInfo.ID, "mud_walker");
	}

	@Override
	protected Capability<IMudWalkerCapability> getCapability() {
		return CapabilityRegistry.CAPABILITY_MUD_WALKER;
	}

	@Override
	protected Class<IMudWalkerCapability> getCapabilityClass() {
		return IMudWalkerCapability.class;
	}

	@Override
	protected MudWalkerCapability getDefaultCapabilityImplementation() {
		return new MudWalkerCapability();
	}

	@Override
	public boolean isApplicable(Entity entity) {
		return entity instanceof EntityPlayer;
	}

	private long reductionTime = -1;

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setLong("reductionTime", this.reductionTime);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		this.reductionTime = nbt.getLong("reductionTime");
	}

	@Override
	public boolean isActive() {
		return this.getRemainingActiveTicks() > 0;
	}

	@Override
	public int getRemainingActiveTicks() {
		return this.reductionTime >= 0 ? Math.max(0, (int)(this.reductionTime - this.getEntity().world.getTotalWorldTime())) : 0;
	}

	@Override
	public void setActive(int duration) {
		if(duration <= 0) {
			this.setNotActive();
		} else {
			this.reductionTime = this.getEntity().world.getTotalWorldTime() + duration;
			this.markDirty();
		}
	}

	@Override
	public void setNotActive() {
		if(this.reductionTime != -1) {
			this.reductionTime = -1;
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
