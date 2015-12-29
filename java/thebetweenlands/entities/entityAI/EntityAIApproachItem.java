package thebetweenlands.entities.entityAI;

import java.util.List;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.Vec3;

public class EntityAIApproachItem extends EntityAIBase {
	public final IEntitySelector entitySelector = new IEntitySelector() {
		@Override
		public boolean isEntityApplicable(Entity entity) {
			return entity.isEntityAlive() && entity.getDistanceToEntity(EntityAIApproachItem.this.entity) <= EntityAIApproachItem.this.targetDistance 
					&& entity instanceof EntityItem && 
					(EntityAIApproachItem.this.ignoreDamage ? ((EntityItem)entity).getEntityItem().getItem() == EntityAIApproachItem.this.targetItem.getItem() : ((EntityItem)entity).getEntityItem().isItemEqual(EntityAIApproachItem.this.targetItem));
		}
	};

	private EntityCreature entity;
	private double farSpeed;
	private double nearSpeed;
	private EntityItem targetEntity;
	private Vec3 targetPos;
	private float targetDistance;
	private PathEntity entityPathEntity;
	private PathNavigate entityPathNavigate;
	private ItemStack targetItem;
	private boolean ignoreDamage;
	private int distractionTime;
	private int distractionTicks;

	public EntityAIApproachItem(EntityCreature entity, Item item, int distractionTime, float targetDistance, double farSpeed, double nearSpeed) {
		this(entity, new ItemStack(item), distractionTime, targetDistance, farSpeed, nearSpeed, true);
	}

	public EntityAIApproachItem(EntityCreature entity, ItemStack item, int distractionTime, float targetDistance, double farSpeed, double nearSpeed, boolean ignoreDamage) {
		this.entity = entity;
		this.targetItem = item;
		this.targetDistance = targetDistance;
		this.farSpeed = farSpeed;
		this.nearSpeed = nearSpeed;
		this.entityPathNavigate = entity.getNavigator();
		this.ignoreDamage = ignoreDamage;
		this.distractionTime = distractionTime;
		setMutexBits(1);
	}

	@Override
	public boolean shouldExecute() {
		List list = entity.worldObj.selectEntitiesWithinAABB(EntityItem.class, this.entity.boundingBox.expand(this.targetDistance, 3, this.targetDistance), entitySelector);
		if (list == null || list.isEmpty()) {
			return false;
		}
		this.targetEntity = (EntityItem) list.get(0);
		if(this.targetEntity == null || !this.targetEntity.isEntityAlive()) return false;
		this.entityPathEntity = this.entityPathNavigate.getPathToXYZ(this.targetEntity.posX, this.targetEntity.posY, this.targetEntity.posZ);
		Vec3 pos = Vec3.createVectorHelper(this.targetEntity.posX, this.targetEntity.posY, this.targetEntity.posZ);
		this.targetPos = pos;
		boolean execute = this.entityPathEntity == null ? false : true;
		return execute;
	}

	@Override
	public boolean continueExecuting() {
		boolean cont = (this.entityPathEntity != null && this.targetEntity != null && this.targetEntity.isEntityAlive()) || !this.entityPathNavigate.noPath();
		return cont;
	}

	@Override
	public void startExecuting() {
		this.entityPathNavigate.setPath(this.entityPathEntity, this.farSpeed);
		this.distractionTicks = 0;
	}

	@Override
	public void resetTask() {
		this.targetEntity = null;
		this.entityPathNavigate.clearPathEntity();
	}

	@Override
	public void updateTask() {
		if(this.targetEntity.getDistance(this.targetPos.xCoord, this.targetPos.yCoord, this.targetPos.zCoord) > 0.5) {
			this.entityPathEntity = this.entityPathNavigate.getPathToXYZ(this.targetEntity.posX, this.targetEntity.posY, this.targetEntity.posZ);
			this.entityPathNavigate.setPath(this.entityPathEntity, this.farSpeed);
			this.targetPos = Vec3.createVectorHelper(this.targetEntity.posX, this.targetEntity.posY, this.targetEntity.posZ);;
		}
		if (this.entity.getDistanceToEntity(this.targetEntity) < 4.0D) {
			this.entity.getNavigator().setSpeed(this.getNearSpeed());
			if(this.entity.getDistanceToEntity(this.targetEntity) < 2.0D) {
				this.distractionTicks++;
				if(this.distractionTicks > this.getDistractionTime()) {
					this.targetEntity.setDead();
					this.onPickup();
					this.resetTask();
				}
			}
		} else {
			this.entity.getNavigator().setSpeed(this.getFarSpeed());
		}
	}

	protected double getNearSpeed() {
		return this.nearSpeed;
	}

	protected double getFarSpeed() {
		return this.farSpeed;
	}

	protected int getDistractionTime() {
		return this.distractionTime;
	}

	protected void onPickup() {}
}
