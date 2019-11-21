package thebetweenlands.common.capability.recruitment;

import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import thebetweenlands.api.capability.IPuppetCapability;
import thebetweenlands.api.capability.ISerializableCapability;
import thebetweenlands.common.capability.base.EntityCapability;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.CapabilityRegistry;

public class EntityPuppetCapability extends EntityCapability<EntityPuppetCapability, IPuppetCapability, EntityLiving> implements IPuppetCapability, ISerializableCapability {
	@Override
	public ResourceLocation getID() {
		return new ResourceLocation(ModInfo.ID, "puppet");
	}

	@Override
	protected Capability<IPuppetCapability> getCapability() {
		return CapabilityRegistry.CAPABILITY_PUPPET;
	}

	@Override
	protected Class<IPuppetCapability> getCapabilityClass() {
		return IPuppetCapability.class;
	}

	@Override
	protected EntityPuppetCapability getDefaultCapabilityImplementation() {
		return new EntityPuppetCapability();
	}

	@Override
	public boolean isApplicable(Entity entity) {
		return entity instanceof EntityLiving;
	}


	private Entity puppeteer;
	private UUID puppeteerUUID;
	private int remainingTicks;
	private boolean stay;

	@Nullable
	private UUID ringUUID;
	private int recruitmentCost;

	@Override
	public void setPuppeteer(Entity puppeteer) {
		this.puppeteerUUID = puppeteer == null ? null : puppeteer.getUniqueID();
		this.puppeteer = puppeteer;
		this.markDirty();
	}

	@Override
	public boolean hasPuppeteer() {
		return this.puppeteerUUID != null;
	}

	@Override
	public Entity getPuppeteer() {
		if(this.puppeteerUUID == null) {
			this.puppeteer = null;
		} else if(this.puppeteer == null || !this.puppeteer.isEntityAlive() || !this.puppeteer.getUniqueID().equals(this.puppeteerUUID)) {
			this.puppeteer = null;
			for(Entity entity : this.getEntity().getEntityWorld().getEntitiesWithinAABB(Entity.class, this.getEntity().getEntityBoundingBox().grow(24.0D, 24.0D, 24.0D))) {
				if(entity.getUniqueID().equals(this.puppeteerUUID)) {
					this.puppeteer = entity;
					break;
				}
			}
		}
		return this.puppeteer;
	}

	@Override
	public void setRemainingTicks(int ticks) {
		this.remainingTicks = ticks;
	}

	@Override
	public int getRemainingTicks() {
		return this.remainingTicks;
	}

	@Override
	public void setStay(boolean stay) {
		this.stay = stay;
		this.markDirty();
	}

	@Override
	public boolean getStay() {
		return this.stay;
	}

	@Override
	public void setRingUuid(@Nullable UUID uuid) {
		this.ringUUID = uuid;
	}

	@Override
	@Nullable
	public UUID getRingUuid() {
		return this.ringUUID;
	}

	@Override
	public void setRecruitmentCost(int cost) {
		this.recruitmentCost = cost;
	}

	@Override
	public int getRecruitmentCost() {
		return this.recruitmentCost;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setInteger("ticks", this.remainingTicks);
		if(this.puppeteerUUID != null) {
			nbt.setUniqueId("puppeteer", this.puppeteerUUID);
		}
		nbt.setBoolean("stay", this.stay);
		if(this.ringUUID != null) {
			nbt.setUniqueId("ring", this.ringUUID);
		}
		nbt.setInteger("recruitmentCost", this.recruitmentCost);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		this.remainingTicks = nbt.getInteger("ticks");
		if(nbt.hasUniqueId("puppeteer")) {
			this.puppeteerUUID = nbt.getUniqueId("puppeteer");
		} else {
			this.puppeteerUUID = null;
		}
		this.stay = nbt.getBoolean("stay");
		if(nbt.hasUniqueId("ring")) {
			this.ringUUID = nbt.getUniqueId("ring");
		} else {
			this.ringUUID = null;
		}
		this.recruitmentCost = nbt.getInteger("recruitmentCost");
	}

	@Override
	public void writeTrackingDataToNBT(NBTTagCompound nbt) {
		if(this.puppeteerUUID != null) {
			nbt.setUniqueId("puppeteer", this.puppeteerUUID);
		}
		nbt.setBoolean("stay", this.stay);
	}

	@Override
	public void readTrackingDataFromNBT(NBTTagCompound nbt) {
		if(nbt.hasUniqueId("puppeteer")) {
			this.puppeteerUUID = nbt.getUniqueId("puppeteer");
		} else {
			this.puppeteerUUID = null;
			this.puppeteer = null;
		}
		this.stay = nbt.getBoolean("stay");
	}

	@Override
	public int getTrackingTime() {
		return 0;
	}
}