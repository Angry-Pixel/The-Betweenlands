package thebetweenlands.common.entity.mobs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import thebetweenlands.common.world.gen.biome.decorator.SurfaceType;

public class EntityBoulderSprite extends EntityMob {
	protected static final DataParameter<Float> ROLL_SPEED = EntityDataManager.createKey(EntityBoulderSprite.class, DataSerializers.FLOAT);

	protected EnumFacing hideoutEntrance = null;
	protected BlockPos hideout = null;

	private float prevRollAnimationInAirWeight = 0.0F;
	private float prevRollAnimation = 0.0F;
	private float prevRollAnimationWeight = 0.0F;
	private float rollAnimationInAirWeight = 0.0F;
	private float rollAnimationSpeed = 0.0F;
	private float rollAnimation = 0.0F;
	private float rollAnimationWeight = 0.0F;

	protected double rollingSpeed = 0;
	protected int rollingTicks = 0;
	protected int rollingAccelerationTime = 0;
	protected int rollingDuration = 0;
	protected Vec3d rollingDir = null;

	public EntityBoulderSprite(World worldIn) {
		super(worldIn);
		this.setSize(0.9F, 1.2F);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataManager.register(ROLL_SPEED, 0.0F);
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.2D);
	}

	@Override
	protected void initEntityAI() {
		this.targetTasks.addTask(0, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));

		//TODO
		this.tasks.addTask(0, new AIRollTowardsTarget(this));
		this.tasks.addTask(1, new EntityAIAttackMelee(this, 1, false));
		//this.tasks.addTask(0, new AIMoveToHideout(this, 1.0D));
	}

	@Override
	public void knockBack(Entity entityIn, float strength, double xRatio, double zRatio) { }

	@Override
	public AxisAlignedBB getCollisionBoundingBox() {
		return this.isRolling() ? null : this.getEntityBoundingBox();
	}

	@Override
	public AxisAlignedBB getCollisionBox(Entity entityIn) {
		return this.isRolling() ? null : this.getEntityBoundingBox();
	}

	@Override
	public void onUpdate() {
		double prevMotionX = this.motionX;
		double prevMotionZ = this.motionZ;

		super.onUpdate();

		if(!this.world.isRemote) {
			BlockPos checkPos = this.getPosition().add(this.rand.nextInt(12) - 6, 0, this.rand.nextInt(12) - 6);
			if(this.getHideout() == null && !this.world.isAirBlock(checkPos)) {
				this.setHideout(checkPos);
			}

			if(this.getHideout() != null && this.getHideout().distanceSqToCenter(this.posX, this.posY, this.posZ) <= 1.5) {
				this.setHideout(null);
			}

			if(this.getAIMoveSpeed() > 0.3F && this.moveForward != 0) {
				this.dataManager.set(ROLL_SPEED, 0.05F + (this.getAIMoveSpeed() - 0.3F) / 3.0F);
			} else {
				this.dataManager.set(ROLL_SPEED, 0.0F);
			}

			if(this.collidedHorizontally && this.isRolling()) {
				boolean pg = this.onGround;
				double pmx = this.motionX;
				double pmy = this.motionY;
				double pmz = this.motionZ;
				double px = this.posX;
				double py = this.posY;
				double pz = this.posZ;

				this.move(MoverType.SELF, prevMotionX, 0, 0);

				boolean cx = Math.abs(this.posX - px) < Math.abs(prevMotionX) / 2.0D;

				this.onGround = pg;
				this.motionX = pmx;
				this.motionY = pmy;
				this.motionZ = pmz;
				this.posX = px;
				this.posY = py;
				this.posZ = pz;

				this.move(MoverType.SELF, 0, 0, prevMotionZ);

				boolean cz = Math.abs(this.posZ - pz) < Math.abs(prevMotionZ) / 2.0D;

				this.onGround = pg;
				this.motionX = pmx;
				this.motionY = pmy;
				this.motionZ = pmz;
				this.posX = px;
				this.posY = py;
				this.posZ = pz;

				this.onRollIntoWall(cx, cz, prevMotionX, prevMotionZ);
			}

			if(this.rollingTicks > 0 && this.rollingDir != null) {
				double speed;
				if(this.rollingDuration - this.rollingTicks < this.rollingAccelerationTime) {
					speed = 0.5D + (this.rollingSpeed - 0.5D) / this.rollingAccelerationTime * (this.rollingDuration - this.rollingTicks);
				} else if(this.rollingTicks < this.rollingAccelerationTime) {
					speed = 0.5D + (this.rollingSpeed - 0.5D) / this.rollingAccelerationTime * this.rollingTicks;
				} else {
					speed = this.rollingSpeed;
				}

				this.getMoveHelper().setMoveTo(this.posX + this.rollingDir.x, this.posY, this.posZ + this.rollingDir.z, speed);

				this.rollingTicks--;
			}
		} else {
			if(this.isEntityAlive() && this.isRolling()) {
				this.setRollSpeed(this.dataManager.get(ROLL_SPEED));
			}

			this.updateRollAnimationState();
		}
	}

	protected void onRollIntoWall(boolean cx, boolean cz, double mx, double mz) {
		if(this.onGround) this.motionY += Math.min(Math.sqrt(mx * mx + mz * mz) * 3, 0.7F);
		if(cx) this.motionX -= mx * 4;
		if(cz) this.motionZ -= mz * 4;
		if(this.onGround || cx || cz) this.velocityChanged = true;

		this.stopRolling();
	}

	public void startRolling(int duration, int accelerationTime, Vec3d dir, double rollingSpeed) {
		this.rollingTicks = duration;
		this.rollingAccelerationTime = accelerationTime;
		this.rollingDuration = duration;
		this.rollingDir = dir;
		this.rollingSpeed = rollingSpeed + 1.5D;
	}

	public int getRollingTicks() {
		return this.rollingTicks;
	}

	public void stopRolling() {
		this.rollingTicks = Math.min(this.rollingAccelerationTime, this.rollingTicks);
	}

	public boolean isRolling() {
		return this.dataManager.get(ROLL_SPEED) > 0.04F;
	}

	public void setRollSpeed(float speed) {
		this.rollAnimationSpeed = speed;
	}

	protected void updateRollAnimationState() {
		this.prevRollAnimationInAirWeight = this.rollAnimationInAirWeight;
		this.prevRollAnimation = this.rollAnimation;
		this.prevRollAnimationWeight = this.rollAnimationWeight;

		if(this.rollAnimationSpeed > 0) {
			if(!this.onGround) {
				this.rollAnimationInAirWeight = Math.min(1, this.rollAnimationInAirWeight + 0.2F);
			} else {
				this.rollAnimationInAirWeight = Math.max(0, this.rollAnimationInAirWeight - 0.2F);
			}

			if(this.rollAnimationSpeed < 0.04F) {
				double p = this.rollAnimation % 1;
				double incr = Math.pow((1 - (this.rollAnimation % 1)) * this.rollAnimationSpeed, 0.65D);
				this.rollAnimation += incr;
				this.rollAnimationWeight = (float) Math.max(0, this.rollAnimationWeight - incr / (1 - (this.rollAnimation % 1)) / 4);
				if(this.rollAnimation % 1 < p) {
					this.prevRollAnimation = this.rollAnimation = 0;
					this.prevRollAnimationWeight = this.rollAnimationWeight = 0;
					this.rollAnimationSpeed = 0;
					this.rollAnimationInAirWeight = 0;
				}
			} else {
				this.rollAnimation += this.rollAnimationSpeed;
				this.rollAnimationWeight = Math.min(1, this.rollAnimationWeight + 0.1F);
				this.rollAnimationSpeed *= 0.5F;
			}
		}
	}

	public float getRollAnimation(float partialTicks) {
		return this.prevRollAnimation + (this.rollAnimation - this.prevRollAnimation) * partialTicks;
	}

	public float getRollAnimationWeight(float partialTicks) {
		return this.prevRollAnimationWeight + (this.rollAnimationWeight - this.prevRollAnimationWeight) * partialTicks;
	}

	public float getRollAnimationInAirWeight(float partialTicks) {
		return this.prevRollAnimationInAirWeight + (this.rollAnimationInAirWeight - this.prevRollAnimationInAirWeight) * partialTicks;
	}

	public void setHideout(@Nullable BlockPos pos) {
		this.hideout = pos;
	}

	@Nullable
	public BlockPos getHideout() {
		return this.hideout;
	}

	public void setHideoutEntrance(@Nullable EnumFacing entrance) {
		this.hideoutEntrance = entrance;
	}

	@Nullable
	protected EnumFacing getHideoutEntrance() {
		return this.hideoutEntrance;
	}

	protected boolean isValidHideout(BlockPos pos) {
		return SurfaceType.UNDERGROUND.matches(this.world.getBlockState(pos));
	}

	protected static class AIRollTowardsTarget extends EntityAIBase {
		protected final EntityBoulderSprite entity;

		protected int cooldown = 18;
		protected Vec3d rollDir;

		public AIRollTowardsTarget(EntityBoulderSprite entity) {
			this.entity = entity;
			this.setMutexBits(3);
		}

		@Override
		public boolean shouldExecute() {
			if(this.cooldown-- <= 0) {
				return this.entity.getAttackTarget() != null && this.entity.getRollingTicks() <= 0 && this.entity.onGround && this.entity.getAttackTarget().isEntityAlive() && this.entity.getEntitySenses().canSee(this.entity.getAttackTarget());
			}
			return false;
		}

		@Override
		public void startExecuting() {
			Entity target = this.entity.getAttackTarget();
			this.rollDir = new Vec3d(target.posX - this.entity.posX, 0, target.posZ - this.entity.posZ).normalize();
			this.entity.startRolling(160, 30, this.rollDir, 1.5D);
		}

		@Override
		public void resetTask() {
			this.cooldown = 20 + this.entity.getRNG().nextInt(26);
		}

		@Override
		public boolean shouldContinueExecuting() {
			//Keep task active while rolling to block other movement tasks
			if(this.entity.getRollingTicks() > 0) {
				Entity target = this.entity.getAttackTarget();
				if(target != null) {
					double overshoot = this.rollDir.dotProduct(new Vec3d(this.entity.posX - target.posX, 0, this.entity.posZ - target.posZ));
					if(overshoot >= 2) {
						this.entity.stopRolling();
					}
				}
				return true;
			}
			return false;
		}
	}

	protected static class AIMoveToHideout extends EntityAIBase {
		protected final EntityBoulderSprite entity;
		protected double speed;

		protected List<EnumFacing> potentialEntrances = new ArrayList<>();

		protected BlockPos targetHideout;
		protected EnumFacing targetEntrance;
		protected BlockPos target;
		protected Path path;

		protected int delayCounter;
		protected int pathingFails;

		protected double approachSpeedFar;
		protected double approachSpeedNear;

		protected double lastFinalPositionDistSq;
		protected int stuckCounter;

		protected boolean finished;

		public AIMoveToHideout(EntityBoulderSprite entity, double speed) {
			this.entity = entity;
			this.speed = this.approachSpeedFar = this.approachSpeedNear = speed;
			this.setMutexBits(3);
		}

		@Override
		public boolean shouldExecute() {
			if(this.entity.isEntityAlive() && this.entity.getHideout() != null) {
				EnumFacing entrance;
				if(this.entity.getHideoutEntrance() == null) {
					if(this.potentialEntrances.isEmpty()) {
						for(EnumFacing dir : EnumFacing.HORIZONTALS) {
							BlockPos offset = this.entity.getHideout().offset(dir);
							PathNodeType node = this.entity.getNavigator().getNodeProcessor().getPathNodeType(this.entity.world, offset.getX(), offset.getY(), offset.getZ());
							if(node == PathNodeType.OPEN || node == PathNodeType.WALKABLE) {
								this.potentialEntrances.add(dir);
							}
						}
						if(this.potentialEntrances.isEmpty()) {
							return false;
						}
						Collections.sort(this.potentialEntrances, (f1, f2) -> 
						Double.compare(
								this.entity.getHideout().offset(f2).distanceSq(this.entity.posX, this.entity.posY, this.entity.posZ),
								this.entity.getHideout().offset(f1).distanceSq(this.entity.posX, this.entity.posY, this.entity.posZ)
								));
					}
					entrance = this.potentialEntrances.remove(this.potentialEntrances.size() - 1);
				} else {
					entrance = this.entity.getHideoutEntrance();
				}
				BlockPos entrancePos = this.entity.getHideout().offset(entrance);
				this.path = this.entity.getNavigator().getPathToPos(entrancePos);
				if(this.path != null) {
					this.entity.setHideoutEntrance(entrance);
					this.target = entrancePos;
					this.targetEntrance = entrance;
					this.targetHideout = this.entity.getHideout();
					return true;
				}
			}
			return false;
		}

		@Override
		public void startExecuting() {
			this.entity.getNavigator().setPath(this.path, this.speed);
			this.approachSpeedFar = this.approachSpeedNear = this.entity.getAIMoveSpeed();
		}

		@Override
		public void updateTask() {
			if(this.delayCounter-- <= 0) {
				double dist = this.entity.getDistanceSq(this.target.getX() + 0.5D, this.target.getY() + 0.5D, this.target.getZ() + 0.5D);

				this.delayCounter = 4 + this.entity.getRNG().nextInt(7);

				if(dist > 1024.0D) {
					this.delayCounter += 10;
				} else if(dist > 256.0D) {
					this.delayCounter += 5;
				}

				if(!this.entity.getNavigator().tryMoveToXYZ(this.target.getX(), this.target.getY(), this.target.getZ(), this.speed)) {
					this.delayCounter += 15;
					this.pathingFails++;
				}
			}

			double dstSq = this.entity.getDistanceSq(this.target.getX() + 0.5D, this.target.getY(), this.target.getZ() + 0.5D);

			if(this.entity.getNavigator().noPath()) {
				if(this.path.isFinished()) {
					this.entity.getMoveHelper().setMoveTo(this.target.getX() + 0.5D, this.target.getY(), this.target.getZ() + 0.5D, this.approachSpeedNear / this.entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue());
					this.entity.getLookHelper().setLookPosition(this.target.getX() - this.targetEntrance.getFrontOffsetX() + 0.5D, this.target.getY() + this.entity.getEyeHeight(), this.target.getZ() - this.targetEntrance.getFrontOffsetZ() + 0.5D, 30, 30);

					this.approachSpeedNear = this.approachSpeedNear * 0.9D + Math.min((dstSq + 0.2D) / 4.0D, 0.4D / 4.0D) * 0.1D;

					if(this.lastFinalPositionDistSq == 0) {
						this.lastFinalPositionDistSq = dstSq;
					} else {
						if(dstSq > this.lastFinalPositionDistSq - 0.05D) {
							this.stuckCounter += this.entity.getRNG().nextInt(3) + 1;
						} else {
							this.lastFinalPositionDistSq = dstSq;
						}
						if(this.stuckCounter >= 80) {
							this.finished = true;
						}
					}

					if(this.entity.getDistanceSq(this.target.getX() + 0.5D, this.target.getY(), this.target.getZ() + 0.5D) < 0.015D && this.entity.getAIMoveSpeed() <= 0.1D) {
						this.finished = true;
					}
				} else {
					this.finished = true;
				}
			} else {
				if(dstSq <= this.entity.getAIMoveSpeed() * 5 * 5) {
					double decay = (this.entity.getAIMoveSpeed() * 5 * 5 - dstSq) / (this.entity.getAIMoveSpeed() * 5 * 5) * 0.33D;

					this.approachSpeedNear = this.approachSpeedFar = this.approachSpeedFar * (1 - decay) + Math.min(0.6D / 4.0D, this.speed / 4.0D) * decay;
					this.entity.getNavigator().setSpeed(this.approachSpeedFar / this.entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue());
				} else {
					this.approachSpeedFar = this.approachSpeedNear = this.entity.getAIMoveSpeed();
				}

				if(this.entity.getNavigator().getPath() != this.path) {
					this.finished = true;
				}
			}
		}

		@Override
		public void resetTask() {
			this.potentialEntrances.clear();
			this.path = null;
			this.entity.getNavigator().clearPath();
			this.pathingFails = 0;
			this.finished = false;
			this.lastFinalPositionDistSq = 0;
			this.target = null;
			this.targetEntrance = null;
			this.stuckCounter = 0;
		}

		@Override
		public boolean shouldContinueExecuting() {
			return this.entity.isEntityAlive() && this.entity.getHideout() != null && this.targetHideout != null && this.targetHideout.equals(this.entity.getHideout()) && this.pathingFails < 3 && !this.finished;
		}
	}
}
