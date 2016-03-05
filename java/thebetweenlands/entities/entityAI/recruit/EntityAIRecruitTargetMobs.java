package thebetweenlands.entities.entityAI.recruit;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.IMob;
import net.minecraft.util.AxisAlignedBB;
import thebetweenlands.entities.properties.BLEntityPropertiesRegistry;
import thebetweenlands.entities.properties.list.recruitment.EntityPropertiesRecruit;

public class EntityAIRecruitTargetMobs extends EntityAITarget implements IRecruitAI {
	protected final EntityPropertiesRecruit properties;

	public EntityAIRecruitTargetMobs(EntityCreature taskOwner, boolean checkSight) {
		super(taskOwner, checkSight);
		this.properties = BLEntityPropertiesRegistry.HANDLER.getProperties(taskOwner, EntityPropertiesRecruit.class);
	}

	@Override
	public boolean shouldExecute() {
		return this.properties != null && (this.taskOwner.getAttackTarget() == null || !this.taskOwner.getAttackTarget().isEntityAlive() || !this.isValidTarget(this.taskOwner.getAttackTarget()));
	}

	@Override
	public void startExecuting() {
		this.taskOwner.setAttackTarget(this.getMobToAttack(this.taskOwner));
		super.startExecuting();
	}

	private boolean isValidTarget(Entity target) {
		return target != null && target.isEntityAlive() && target != this.properties.getRecruiter();
	}

	private EntityLivingBase getMobToAttack(EntityLiving entity) {
		AxisAlignedBB selection = AxisAlignedBB.getBoundingBox(entity.posX, entity.posY, entity.posZ, 
				entity.posX + 1, entity.posY + 1, entity.posZ + 1).expand(16, 16, 16);
		List<EntityLivingBase> eligibleTargets = entity.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, selection);
		EntityLivingBase closest = null;
		for(EntityLivingBase target : eligibleTargets) {
			if(target != entity && (target instanceof EntityMob || target instanceof IMob) && 
					((closest == null || target.getDistanceToEntity(entity) < closest.getDistanceToEntity(entity)) && 
							this.isValidTarget(target) && this.isSuitableTarget(target, true)))
				closest = target;
		}
		return closest;
	}
}
