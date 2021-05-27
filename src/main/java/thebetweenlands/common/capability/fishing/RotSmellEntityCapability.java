package thebetweenlands.common.capability.fishing;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import thebetweenlands.api.capability.IRotSmellCapability;
import thebetweenlands.api.capability.ISerializableCapability;
import thebetweenlands.common.capability.base.EntityCapability;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.CapabilityRegistry;

public class RotSmellEntityCapability extends EntityCapability<RotSmellEntityCapability, IRotSmellCapability, EntityPlayer> implements IRotSmellCapability, ISerializableCapability {
	@Override
	public ResourceLocation getID() {
		return new ResourceLocation(ModInfo.ID, "rot_smell");
	}

	@Override
	protected Capability<IRotSmellCapability> getCapability() {
		return CapabilityRegistry.CAPABILITY_ROT_SMELL;
	}

	@Override
	protected Class<IRotSmellCapability> getCapabilityClass() {
		return IRotSmellCapability.class;
	}

	@Override
	protected RotSmellEntityCapability getDefaultCapabilityImplementation() {
		return new RotSmellEntityCapability();
	}

	@Override
	public boolean isApplicable(Entity entity) {
		return entity instanceof EntityPlayer;
	}

	private boolean isSmelly = false;

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setBoolean("isSmelly", this.isSmelly);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		this.isSmelly = nbt.getBoolean("isSmelly");
	}

	@Override
	public boolean isSmellingBad() {
		return this.isSmelly;
	}

	@Override
	public void setIsSmellingBad(boolean isSmellyIn) {
		this.isSmelly = isSmellyIn;
	}

}
