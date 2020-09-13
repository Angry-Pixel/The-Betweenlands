package thebetweenlands.common.entity.mobs;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.MoverType;
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
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.common.entity.ai.IPathObstructionAwareEntity;
import thebetweenlands.common.entity.ai.ObstructionAwarePathNavigateGround;
import thebetweenlands.common.entity.movement.ClimbMoveHelper;
import thebetweenlands.util.BoxSmoothingUtil;

public class EntityClimber extends EntityCreature implements IEntityBL, IPathObstructionAwareEntity {

	public double prevRenderOffsetX, prevRenderOffsetY, prevRenderOffsetZ;
	public double renderOffsetX, renderOffsetY, renderOffsetZ;

	public Vec3d renderNormal = new Vec3d(0, 1, 0);
	public Vec3d prevRenderNormal = new Vec3d(0, 1, 0);
	
	public EntityClimber(World world) {
		super(world);
		this.isImmuneToFire = true;
		setSize(0.9F, 0.9F);

		//tasks.addTask(0, new EntityAISwimming(this));
		tasks.addTask(1, new EntityAIAttackMelee(this, 1.0D, false));
		/*tasks.addTask(2, new EntityAIMoveTowardsRestriction(this, 1.0D));
		tasks.addTask(3, new EntityAIWatchClosest(this, EntityPlayer.class, 16.0F));
		tasks.addTask(4, new EntityAIWander(this, 1.0D));
		tasks.addTask(5, new EntityAILookIdle(this));
		targetTasks.addTask(0, new EntityAIHurtByTarget(this, true));*/
		targetTasks.addTask(1, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, 1, false, false, null));

		this.moveHelper = new ClimbMoveHelper(this);
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
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
				for(int i = 0; i <= 16; i++) {
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

			float penalty = Math.max(0, 6 - height) * 1f; 

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

	public Pair<EnumFacing, Vec3d> getWalkingSide() {
		//TODO When path is available check next point and sync

		EnumFacing avoidPathingFacing = EnumFacing.DOWN;

		Path path = this.getNavigator().getPath();
		if(path != null) {
			int index = path.getCurrentPathIndex();

			if(index < path.getCurrentPathLength()) {
				PathPoint point = path.getPathPointFromIndex(index);

				double maxDist = 0;

				for(EnumFacing facing : EnumFacing.VALUES) {
					double posEntity = Math.abs(facing.getXOffset()) * this.posX + Math.abs(facing.getYOffset()) * this.posY + Math.abs(facing.getZOffset()) * this.posZ;
					double posPath = Math.abs(facing.getXOffset()) * point.x + Math.abs(facing.getYOffset()) * point.y + Math.abs(facing.getZOffset()) * point.z;

					double distSigned = posPath + 0.5f - posEntity;
					if(distSigned * (facing.getXOffset() + facing.getYOffset() + facing.getZOffset()) > 0) {
						double dist = Math.abs(distSigned) - (facing.getAxis().isHorizontal() ? this.width / 2 : (facing == EnumFacing.DOWN ? 0 : this.height));

						if(dist > maxDist) {
							maxDist = dist;
							avoidPathingFacing = facing.getOpposite();
						}
					}
				}
			}
		}

		AxisAlignedBB entityBox = this.getEntityBoundingBox();

		double closestFacingDst = Double.MAX_VALUE;
		EnumFacing closestFacing = EnumFacing.DOWN;

		Vec3d weighting = new Vec3d(0, 0, 0);

		float stickingDst = 2.0f;

		for(EnumFacing facing : EnumFacing.VALUES) {
			if(avoidPathingFacing == facing) {
				continue;
			}

			List<AxisAlignedBB> collisionBoxes = this.world.getCollisionBoxes(this, entityBox.grow(0.2f).expand(facing.getXOffset() * stickingDst, facing.getYOffset() * stickingDst, facing.getZOffset() * stickingDst));

			double closestDst = Double.MAX_VALUE;

			for(AxisAlignedBB collisionBox : collisionBoxes) {
				switch(facing) {
				case EAST:
				case WEST:
					closestDst = Math.min(closestDst, Math.abs(entityBox.calculateXOffset(collisionBox, -facing.getXOffset() * stickingDst)));
					break;
				case UP:
				case DOWN:
					closestDst = Math.min(closestDst, Math.abs(entityBox.calculateYOffset(collisionBox, -facing.getYOffset() * stickingDst)));
					break;
				case NORTH:
				case SOUTH:
					closestDst = Math.min(closestDst, Math.abs(entityBox.calculateZOffset(collisionBox, -facing.getZOffset() * stickingDst)));
					break;
				}
			}

			if(closestDst < closestFacingDst) {
				closestFacingDst = closestDst;
				closestFacing = facing;
			}

			if(closestDst < Double.MAX_VALUE) {
				weighting = weighting.add(new Vec3d(facing.getXOffset(), facing.getYOffset(), facing.getZOffset()).scale(1 - Math.min(closestDst, stickingDst) / stickingDst));
			}
		}

		return Pair.of(closestFacing, weighting.normalize().add(0, -0.001f, 0).normalize());
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		this.setNoGravity(true);

		//TODO Pathing debug
		if(!this.world.isRemote) {
			Path p = this.getNavigator().getPath();
			if(p != null) {
				for(int i = 0; i < p.getCurrentPathLength(); i++) {
					PathPoint po = p.getPathPointFromIndex(i);
					if(this.world.isAirBlock(new BlockPos(po.x, po.y, po.z))) {
						//this.world.setBlockState(new BlockPos(po.x, po.y, po.z), Blocks.REEDS.getDefaultState(), 2);
					}
				}
				//this.setDead();
			}
		}

		float inclusionRange = 1.5f;

		float smoothingRange = 1f;

		Vec3d p = this.getPositionVector();

		Vec3d s = p.add(0, this.height / 2, 0);
		AxisAlignedBB inclusionBox = new AxisAlignedBB(s.x, s.y, s.z, s.x, s.y, s.z).grow(inclusionRange);

		List<AxisAlignedBB> boxes = this.world.getCollisionBoxes(this, inclusionBox);



		Pair<Vec3d, Vec3d> closestSmoothPoint = BoxSmoothingUtil.findClosestSmoothPoint(boxes, smoothingRange, 0.9f, 0.005f, 20, 0.05f, p);

		this.prevRenderOffsetX = this.renderOffsetX;
		this.prevRenderOffsetY = this.renderOffsetY;
		this.prevRenderOffsetZ = this.renderOffsetZ;
		
		this.renderOffsetX = closestSmoothPoint.getLeft().x - p.x;
		this.renderOffsetY = closestSmoothPoint.getLeft().y - p.y;
		this.renderOffsetZ = closestSmoothPoint.getLeft().z - p.z;
		
		this.prevRenderNormal = this.renderNormal;
		this.renderNormal = closestSmoothPoint.getRight();
	}

	@Override
	public void travel(float strafe, float vertical, float forward) {
		//super.travel(strafe, vertical, forward);

		if(this.isServerWorld() || this.canPassengerSteer()) {
			Pair<EnumFacing, Vec3d> walkingSide = this.getWalkingSide();

			if(!this.world.isRemote) {
				//System.out.println("    Walk on: " + walkingSide.getLeft());
			}

			//"Gravity"
			this.motionX += walkingSide.getRight().x * 0.04D;
			this.motionY += walkingSide.getRight().y * 0.04D;
			this.motionZ += walkingSide.getRight().z * 0.04D;

			this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);

			if(!this.world.isRemote) {
				//System.out.println(walkingSide.getRight());
			}

			if(this.collidedHorizontally || this.collidedVertically) {
				this.fallDistance = 0;

				BlockPos offsetPos = new BlockPos(this).offset(walkingSide.getLeft());
				IBlockState offsetState = this.world.getBlockState(offsetPos);
				float blockSlipperiness = offsetState.getBlock().getSlipperiness(offsetState, this.world, offsetPos, this);

				float slipperiness = blockSlipperiness * 0.91F;

				switch(walkingSide.getLeft().getAxis()) {
				case X:
					this.motionZ *= slipperiness;
					this.motionY *= slipperiness;
					this.motionX = 0;
					break;
				case Y:
					this.motionX *= slipperiness;
					this.motionZ *= slipperiness;
					this.motionY = 0;
					break;
				case Z:
					this.motionX *= slipperiness;
					this.motionY *= slipperiness;
					this.motionZ = 0;
					break;
				}
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