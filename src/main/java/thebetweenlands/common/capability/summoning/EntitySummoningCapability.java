package thebetweenlands.common.capability.summoning;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import thebetweenlands.api.capability.ISerializableCapability;
import thebetweenlands.api.capability.ISummoningCapability;
import thebetweenlands.common.capability.base.EntityCapability;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.CapabilityRegistry;

public class EntitySummoningCapability extends EntityCapability<EntitySummoningCapability, ISummoningCapability, EntityCreature> implements ISummoningCapability, ISerializableCapability {
	@Override
	public ResourceLocation getID() {
		return new ResourceLocation(ModInfo.ID, "summoning");
	}

	@Override
	protected Capability<ISummoningCapability> getCapability() {
		return CapabilityRegistry.CAPABILITY_SUMMON;
	}

	@Override
	protected Class<ISummoningCapability> getCapabilityClass() {
		return ISummoningCapability.class;
	}

	@Override
	protected EntitySummoningCapability getDefaultCapabilityImplementation() {
		return new EntitySummoningCapability();
	}

	@Override
	public boolean isApplicable(Entity entity) {
		return entity instanceof EntityPlayer;
	}




	private boolean active;
	private int cooldownTicks;
	private int activeTicks;

	@Override
	public void setActive(boolean active) {
		this.active = active;
		this.markDirty();
	}

	@Override
	public boolean isActive() {
		return this.active;
	}

	@Override
	public int getCooldownTicks() {
		return this.cooldownTicks;
	}

	@Override
	public void setCooldownTicks(int ticks) {
		this.cooldownTicks = ticks;
	}

	@Override
	public int getActiveTicks() {
		return this.activeTicks;
	}

	@Override
	public void setActiveTicks(int ticks) {
		this.activeTicks = ticks;
		this.markDirty();
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setInteger("cooldown", this.cooldownTicks);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		this.cooldownTicks = nbt.getInteger("cooldown");
	}

	@Override
	public void writeTrackingDataToNBT(NBTTagCompound nbt) {
		nbt.setBoolean("active", this.active);
	}

	@Override
	public void readTrackingDataFromNBT(NBTTagCompound nbt) {
		this.active = nbt.getBoolean("active");
	}

	@Override
	public int getTrackingTime() {
		return 0;
	}
}