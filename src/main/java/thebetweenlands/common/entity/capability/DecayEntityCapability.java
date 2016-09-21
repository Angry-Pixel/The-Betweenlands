package thebetweenlands.common.entity.capability;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import thebetweenlands.common.entity.capability.base.EntityCapability;
import thebetweenlands.common.lib.ModInfo;

public class DecayEntityCapability extends EntityCapability<DecayEntityCapability, IDecayCapability> implements IDecayCapability {
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
	protected IDecayCapability getDefaultCapabilityImplementation() {
		return new DecayEntityCapability();
	}

	@Override
	public boolean isApplicable(Entity entity) {
		return entity instanceof EntityPlayer;
	}






	private int decay = 0;

	@Override
	public int getDecay() {
		return this.decay;
	}

	@Override
	public void setDecay(int decay) {
		this.decay = decay;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setInteger("Decay", this.decay);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		this.decay = nbt.getInteger("Decay");
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
