package thebetweenlands.common.entity.mobs;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.common.entity.ai.IPathObstructionAwareEntity;
import thebetweenlands.common.entity.ai.ObstructionAwarePathNavigateGround;

public class EntityClimber extends EntityCreature implements IEntityBL, IPathObstructionAwareEntity {

	public EntityClimber(World world) {
		super(world);
		this.isImmuneToFire = true;
		setSize(0.5F, 0.5F);

		//tasks.addTask(0, new EntityAISwimming(this));
		tasks.addTask(1, new EntityAIAttackMelee(this, 1.0D, false));
		/*tasks.addTask(2, new EntityAIMoveTowardsRestriction(this, 1.0D));
		tasks.addTask(3, new EntityAIWatchClosest(this, EntityPlayer.class, 16.0F));
		tasks.addTask(4, new EntityAIWander(this, 1.0D));
		tasks.addTask(5, new EntityAILookIdle(this));
		targetTasks.addTask(0, new EntityAIHurtByTarget(this, true));*/
		targetTasks.addTask(1, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, false));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3D);
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(0.1D);
		getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(40.0D);
		getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
	}

	@Override
	protected PathNavigate createNavigator(World worldIn) {
		ObstructionAwarePathNavigateGround<EntityClimber> navigate = new ObstructionAwarePathNavigateGround<EntityClimber>(this, worldIn, false, true, true, false) {
			@Override
			public Path getPathToEntityLiving(Entity entityIn) {
				BlockPos pos = new BlockPos(entityIn);

				//Path to ceiling above target if possible
				for(int i = 0; i < 6; i++) {
					if(!entityIn.world.isAirBlock(pos.up(i))) {
						pos = pos.up(i - 1);
						break;
					}
				}

				return this.getPathToPos(pos);
			}
		};
		navigate.setCanSwim(true);
		return navigate;
	}

	@Override
	public float getBridgePathingMalus(EntityLiving entity, BlockPos pos, PathPoint fallPathPoint) {
		return -1.0f;
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

			float penalty = Math.max(0, 6 - height) * 0.5f; 

			return priority + penalty;
		}

		return priority;
	}

	@Override
	public void onPathingObstructed(EnumFacing facing) {

	}

	@Override
	public int getMaxFallHeight() {
		return 1;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		//TODO Pathing debug
		if(!this.world.isRemote) {
			Path p = this.getNavigator().getPath();
			if(p != null) {
				for(int i = 0; i < p.getCurrentPathLength(); i++) {
					PathPoint po = p.getPathPointFromIndex(i);
					if(this.world.isAirBlock(new BlockPos(po.x, po.y, po.z))) {
						this.world.setBlockState(new BlockPos(po.x, po.y, po.z), Blocks.REEDS.getDefaultState(), 2);
					}
				}
				//this.setDead();
			}
		}
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
	}

	@Override
	public float getBlockPathWeight(BlockPos pos) {
		return 0.5F;
	}
}