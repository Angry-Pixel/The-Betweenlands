package thebetweenlands.common.capability.flight;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import thebetweenlands.api.capability.IFlightCapability;
import thebetweenlands.api.capability.ISerializableCapability;
import thebetweenlands.common.capability.base.EntityCapability;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.CapabilityRegistry;

public class FlightEntityCapability extends EntityCapability<FlightEntityCapability, IFlightCapability, EntityPlayer> implements IFlightCapability, ISerializableCapability {
	@Override
	public ResourceLocation getID() {
		return new ResourceLocation(ModInfo.ID, "flight");
	}

	@Override
	protected Capability<IFlightCapability> getCapability() {
		return CapabilityRegistry.CAPABILITY_FLIGHT;
	}

	@Override
	protected Class<IFlightCapability> getCapabilityClass() {
		return IFlightCapability.class;
	}

	@Override
	protected FlightEntityCapability getDefaultCapabilityImplementation() {
		return new FlightEntityCapability();
	}

	@Override
	public boolean isApplicable(Entity entity) {
		return entity instanceof EntityPlayer;
	}

	private boolean ring = false;
	private boolean flying = false;
	public int flightTime = 0;

	@Override
	public boolean isFlying() {
		return this.flying;
	}

	@Override
	public void setFlying(boolean flying) {
		this.flying = flying;
		this.markDirty();
	}

	@Override
	public int getFlightTime() {
		return this.flightTime;
	}

	@Override
	public void setFlightTime(int ticks) {
		this.flightTime = ticks;
		this.markDirty();
	}

	@Override
	public void setFlightRing(boolean ring) {
		this.ring = ring;
		this.markDirty();
	}

	@Override
	public boolean getFlightRing() {
		return this.ring;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setBoolean("flying", this.flying);
		nbt.setInteger("time", this.flightTime);
		nbt.setBoolean("ring", this.ring);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		this.flying = nbt.getBoolean("flying");
		this.flightTime = nbt.getInteger("time");
		this.ring = nbt.getBoolean("ring");
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
		return 0;
	}
}
