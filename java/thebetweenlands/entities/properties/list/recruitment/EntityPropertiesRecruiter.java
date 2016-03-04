package thebetweenlands.entities.properties.list.recruitment;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import thebetweenlands.entities.properties.BLEntityPropertiesRegistry;
import thebetweenlands.entities.properties.EntityProperties;

public class EntityPropertiesRecruiter extends EntityProperties<EntityPlayer> {
	private int recruitmentTicks = 0;
	private boolean recruiting = false;
	private EntityLiving recruit = null;

	public static final int RECRUITMENT_TIME = 2400;

	@Override
	public void saveNBTData(NBTTagCompound nbt) { }

	@Override
	public void loadNBTData(NBTTagCompound nbt) { }

	@Override
	public int getTrackingTime() {
		return 0;
	}

	@Override
	public int getTrackingUpdateTime() {
		return 0;
	}

	@Override
	protected boolean saveTrackingSensitiveData(NBTTagCompound nbt) {
		nbt.setBoolean("recruiting", this.recruiting);
		if(this.recruit != null)
			nbt.setInteger("recruitID", this.recruit.getEntityId());
		return false;
	}

	@Override
	protected void loadTrackingSensitiveData(NBTTagCompound nbt) {
		this.recruiting = nbt.getBoolean("recruiting");
		this.recruit = null;
		if(nbt.hasKey("recruitID")) {
			Entity recruit = this.getEntity().worldObj.getEntityByID(nbt.getInteger("recruitID"));
			if(recruit instanceof EntityLiving)
				this.recruit = (EntityLiving) recruit;
		}
	}

	@Override
	public String getID() {
		return "blPropertyRecruiter";
	}

	@Override
	public Class<EntityPlayer> getEntityClass() {
		return EntityPlayer.class;
	}

	public void startRecruiting(EntityLiving entity) {
		this.recruiting = true;
		this.recruit = entity;
		this.recruitmentTicks = 0;
	}

	public void stopRecruiting() {
		this.recruiting = false;
		this.recruit = null;
		this.recruitmentTicks = 0;
	}

	public boolean isRecruiting() {
		return this.recruiting && this.recruit != null && this.recruit.isEntityAlive();
	}

	public EntityLiving getRecruit() {
		return this.recruit;
	}

	public int getRequiredRecruitmentTime() {
		return (int)(this.recruit == null ? 0 : this.recruit.getMaxHealth() / 1.5F);
	}

	public boolean canRecruit() {
		List<Entity> loadedEntities = this.getEntity().worldObj.loadedEntityList;
		for(Entity entity : loadedEntities) {
			if(entity instanceof EntityLiving) {
				EntityLiving living = (EntityLiving) entity;
				EntityPropertiesRecruit props = BLEntityPropertiesRegistry.HANDLER.getProperties(entity, EntityPropertiesRecruit.class);
				if(props != null && props.getRecruiter() == this.getEntity() && props.isRecruited() && entity.isEntityAlive())
					return false;
			}
		}
		return !this.isRecruiting();
	}

	public void update() {
		if(this.isRecruiting()) {
			if(this.recruit.getDistanceToEntity(this.getEntity()) > 6) {
				this.stopRecruiting();
			} else {
				this.recruitmentTicks++;
				if(this.recruitmentTicks >= this.getRequiredRecruitmentTime()) {
					EntityPropertiesRecruit props = BLEntityPropertiesRegistry.HANDLER.getProperties(this.recruit, EntityPropertiesRecruit.class);
					if(props != null)
						props.setRecruited(RECRUITMENT_TIME, this.getEntity());
					this.stopRecruiting();
				}
			}
		}
	}
}
