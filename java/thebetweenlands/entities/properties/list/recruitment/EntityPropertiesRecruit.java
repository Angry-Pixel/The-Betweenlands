package thebetweenlands.entities.properties.list.recruitment;

import java.util.List;
import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import thebetweenlands.entities.properties.EntityProperties;
import thebetweenlands.entities.properties.list.equipment.EnumEquipmentCategory;
import thebetweenlands.entities.properties.list.equipment.Equipment;
import thebetweenlands.entities.properties.list.equipment.EquipmentInventory;
import thebetweenlands.items.equipment.ItemRingOfRecruitment;

public class EntityPropertiesRecruit extends EntityProperties<EntityLiving> {
	private String recruiterUUID = null;
	private int time = 0;

	//Client side
	private boolean recruited = false;

	private EntityLivingBase distractionEntity;

	@Override
	protected void initProperties() {
		this.distractionEntity = new EntityLivingBase(this.getEntity().worldObj){
			@Override
			public ItemStack getHeldItem() {
				return null;
			}

			@Override
			public ItemStack getEquipmentInSlot(int slot) {
				return null;
			}

			@Override
			public void setCurrentItemOrArmor(int slot, ItemStack nbt) {
			}

			@Override
			public ItemStack[] getLastActiveItems() {
				return new ItemStack[0];
			}};
	}

	@Override
	public void saveNBTData(NBTTagCompound nbt) {
		if(this.recruiterUUID != null) 
			nbt.setString("recruiterUUID", this.recruiterUUID);
		nbt.setInteger("time", this.time);
	}

	@Override
	public void loadNBTData(NBTTagCompound nbt) {
		this.recruiterUUID = nbt.hasKey("recruiterUUID") ? nbt.getString("recruiterUUID") : null;
		this.time = nbt.getInteger("time");
	}

	@Override
	protected boolean saveTrackingSensitiveData(NBTTagCompound nbt) {
		nbt.setBoolean("recruited", this.isRecruited());
		return false;
	}


	@Override
	protected void loadTrackingSensitiveData(NBTTagCompound nbt) {
		this.recruited = nbt.getBoolean("recruited");
	}

	@Override
	public String getID() {
		return "blPropertyRecruit";
	}

	@Override
	public Class<EntityLiving> getEntityClass() {
		return EntityLiving.class;
	}

	@Override
	public int getTrackingTime() {
		return 0;
	}

	public void setRecruited(int time, EntityPlayer recruiter) {
		this.time = time;
		this.recruiterUUID = recruiter.getUniqueID().toString();
		this.getEntity().setHealth(this.getEntity().getMaxHealth() / 4.0F * 3.0F);
	}

	public int getLastingTime() {
		return this.time;
	}

	public boolean isRecruited() {
		return this.time > 0 || this.recruited;
	}

	public EntityPlayer getRecruiter() {
		try {
			return this.getEntity().worldObj.func_152378_a(UUID.fromString(this.recruiterUUID));
		} catch(Exception ex) {
			this.recruiterUUID = null;
		}
		return null;
	}

	public void update() {
		if(this.time > 0) {
			this.time--;
		}
		if(this.time <= 0 || !this.shouldContinue()) {
			this.setTarget(this.getRecruiter());
			this.time = 0;
		}

		if(this.isRecruited()) {
			EntityPlayer recruiter = this.getRecruiter();
			EntityLiving entity = this.getEntity();
			this.distractionEntity.setLocationAndAngles(entity.posX, entity.posY, entity.posZ, 0, 0);
			if(entity.getAttackTarget() == recruiter) {
				entity.setAttackTarget(this.distractionEntity);
			}
			if(entity.getAITarget() == recruiter) {
				entity.setRevengeTarget(this.distractionEntity);
			}
			if(entity instanceof EntityCreature) {
				EntityCreature creature = (EntityCreature) entity;
				if(creature.getEntityToAttack() == recruiter) {
					creature.setTarget(this.distractionEntity);
				}
			}

			if(!this.isValidTarget(entity.getAITarget()) || !this.isValidTarget(entity.getAttackTarget()) || (entity instanceof EntityCreature == false || !this.isValidTarget(((EntityCreature)entity).getEntityToAttack()))) {
				EntityLivingBase newTarget = this.getMobToAttack();
				if(newTarget != null)
					this.setTarget(newTarget);
			}
		}
	}

	private boolean shouldContinue() {
		if(this.getRecruiter() == null)
			return true;
		EquipmentInventory equipmentInventory = EquipmentInventory.getEquipmentInventory(this.getRecruiter());
		List<Equipment> equipmentList = equipmentInventory.getEquipment(EnumEquipmentCategory.RING);
		boolean hasActiveRing = false;
		for(Equipment equipment : equipmentList) {
			if(equipment.item.getItem() instanceof ItemRingOfRecruitment && ((ItemRingOfRecruitment)equipment.item.getItem()).isActive(equipment.item)) {
				hasActiveRing = true;
			}
		}
		return hasActiveRing;
	}

	public void setTarget(EntityLivingBase target) {
		EntityLiving entity = this.getEntity();
		entity.setAttackTarget(target);
		entity.setRevengeTarget(target);
		if(entity instanceof EntityCreature) {
			((EntityCreature)entity).setTarget(target);
		}
	}

	private boolean isValidTarget(Entity target) {
		return target != null && target.isEntityAlive() && target != this.getRecruiter() && target != this.distractionEntity;
	}

	public EntityLivingBase getMobToAttack() {
		AxisAlignedBB selection = AxisAlignedBB.getBoundingBox(this.getEntity().posX, this.getEntity().posY, this.getEntity().posZ, 
				this.getEntity().posX + 1, this.getEntity().posY + 1, this.getEntity().posZ + 1).expand(16, 16, 16);
		List<EntityLivingBase> eligibleTargets = this.getEntity().worldObj.getEntitiesWithinAABB(EntityLivingBase.class, selection);
		EntityLivingBase closest = null;
		for(EntityLivingBase target : eligibleTargets) {
			if(target != this.getEntity() && (target instanceof EntityMob || target instanceof IMob) && 
					((closest == null || target.getDistanceToEntity(this.getEntity()) < closest.getDistanceToEntity(this.getEntity())) && 
							this.isValidTarget(target)))
				closest = target;
		}
		return closest;
	}
}
