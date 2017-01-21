package thebetweenlands.common.capability.recruitment;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import thebetweenlands.common.capability.base.EntityCapability;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.CapabilityRegistry;

public class EntityPuppeteerCapability extends EntityCapability<EntityPuppeteerCapability, IPuppeteerCapability, EntityPlayer> implements IPuppeteerCapability {
	@Override
	public ResourceLocation getID() {
		return new ResourceLocation(ModInfo.ID, "puppeteer");
	}

	@Override
	protected Capability<IPuppeteerCapability> getCapability() {
		return CapabilityRegistry.CAPABILITY_PUPPETEER;
	}

	@Override
	protected Class<IPuppeteerCapability> getCapabilityClass() {
		return IPuppeteerCapability.class;
	}

	@Override
	protected EntityPuppeteerCapability getDefaultCapabilityImplementation() {
		return new EntityPuppeteerCapability();
	}

	@Override
	public boolean isApplicable(Entity entity) {
		return entity instanceof EntityPlayer;
	}



	private int activatingTicks;
	private int activatingEntityId;
	private Entity activatingEntity;

	@Override
	public List<Entity> getPuppets() {
		return this.getEntity().getEntityWorld().getEntitiesWithinAABB(Entity.class, this.getEntity().getEntityBoundingBox().expandXyz(24.0D), entity -> {
			if(entity.hasCapability(CapabilityRegistry.CAPABILITY_PUPPET, null)) {
				IPuppetCapability cap = entity.getCapability(CapabilityRegistry.CAPABILITY_PUPPET, null);
				if(cap.getPuppeteer() == this.getEntity()) {
					return true;
				}
			}
			return false;
		});
	}

	@Override
	public void setActivatingEntity(Entity entity) {
		this.activatingEntityId = entity == null ? -1 : entity.getEntityId();
		this.activatingEntity = entity;
		this.markDirty();
	}

	@Override
	public Entity getActivatingEntity() {
		if(this.activatingEntityId < 0) {
			this.activatingEntity = null;
		} else if(this.activatingEntity == null || !this.activatingEntity.isEntityAlive() || this.activatingEntity.getEntityId() != this.activatingEntityId) {
			this.activatingEntity = this.getEntity().worldObj.getEntityByID(this.activatingEntityId);
		}
		return this.activatingEntity;
	}

	@Override
	public int getActivatingTicks() {
		return this.activatingTicks;
	}

	@Override
	public void setActivatingTicks(int ticks) {
		this.activatingTicks = ticks;
	}

	@Override
	public void writeTrackingDataToNBT(NBTTagCompound nbt) {
		nbt.setInteger("activatingEntityId", this.activatingEntityId);
	}

	@Override
	public void readTrackingDataFromNBT(NBTTagCompound nbt) {
		this.activatingEntityId = nbt.getInteger("activatingEntityId");
	}

	@Override
	public int getTrackingTime() {
		return 0;
	}
}