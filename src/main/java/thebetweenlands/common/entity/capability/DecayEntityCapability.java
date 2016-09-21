package thebetweenlands.common.entity.capability;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import thebetweenlands.common.entity.capability.base.EntityCapability;
import thebetweenlands.common.lib.ModInfo;

public class DecayEntityCapability extends EntityCapability<DecayEntityCapability, IDecayCapability> implements IDecayCapability {
	@Override
	public ResourceLocation getID() {
		return new ResourceLocation(ModInfo.ID, "decay");
	}

	@Override
	protected Capability<IDecayCapability> getCapability() {
		return EntityCapabilities.DECAY_CAPABILITY;
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
}
