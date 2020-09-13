package thebetweenlands.common.entity.mobs;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityClimber extends EntityClimberBase {
	public EntityClimber(World world) {
		super(world);
		this.maxPathingTargetHeight = 16;
	}

	@Override
	protected void initEntityAI() {
		this.tasks.addTask(1, new EntityAIAttackMelee(this, 1.0D, false));
		this.targetTasks.addTask(1, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, 1, false, false, null));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3D);
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(5D);
		this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(40.0D);
		this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
	}

	@Override
	public float getPathingMalus(EntityLiving entity, PathNodeType nodeType, BlockPos pos) {
		float priority = super.getPathPriority(nodeType);

		if(priority >= 0.0f) {
			int height = 0;

			while(pos.getY() - height > 0) {
				height++;

				if(!this.world.isAirBlock(pos.offset(EnumFacing.DOWN, height))) {
					break;
				}
			}

			float penalty = Math.max(0, 6 - height) * 1f; 

			return priority + penalty;
		}

		return priority;
	}
}
