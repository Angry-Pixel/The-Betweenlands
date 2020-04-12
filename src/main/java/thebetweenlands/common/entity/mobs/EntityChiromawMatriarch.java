package thebetweenlands.common.entity.mobs;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.common.entity.ai.EntityAIAttackOnCollide;
import thebetweenlands.common.entity.movement.FlightMoveHelper;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class EntityChiromawMatriarch extends EntityFlyingMob implements IEntityBL {
	private static final DataParameter<Boolean> IS_NESTING = EntityDataManager.createKey(EntityChiromawMatriarch.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> IS_BROODY = EntityDataManager.createKey(EntityChiromawMatriarch.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> IS_LANDING = EntityDataManager.createKey(EntityChiromawMatriarch.class, DataSerializers.BOOLEAN);
	public int broodCount;
	@Nullable
	private BlockPos boundOrigin;
	
	public EntityChiromawMatriarch(World world) {
		super(world);
		setSize(1.75F, 2F);
		setIsHanging(false);

		this.moveHelper = new FlightMoveHelper(this);
		setPathPriority(PathNodeType.WATER, -8F);
		setPathPriority(PathNodeType.BLOCKED, -8.0F);
		setPathPriority(PathNodeType.OPEN, 8.0F);
		setPathPriority(PathNodeType.FENCE, -8.0F);
	}
	
	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(IS_NESTING, false);
		dataManager.register(IS_BROODY, false);
		dataManager.register(IS_LANDING, false);
	}

	@Override
	protected void initEntityAI() {
		tasks.addTask(0, new EntityAISwimming(this));
		tasks.addTask(1, new EntityAIAttackMelee(this, 1.0D, true));
		tasks.addTask(2, new EntityChiromawMatriarch.AIMoveRandom(this));
		tasks.addTask(3, new EntityChiromawMatriarch.AIReturnToNest(this, 1.25D));
		targetTasks.addTask(1, new EntityAINearestAttackableTarget<EntityPlayer>(this, EntityPlayer.class, true).setUnseenMemoryTicks(160));
	}
	
	@Override
	public void onUpdate() {
		super.onUpdate();

		if (!getEntityWorld().isRemote) {
			if (getEntityWorld().getDifficulty() == EnumDifficulty.PEACEFUL)
				setDead();
			if (getBroodCount() <= 0) {
				if (getIsBroody())
					setIsBroody(false);
				else if (!getIsBroody())
					setIsBroody(true);
				setBroodCount(240);
			}

			if (getBroodCount() > 0)
				setBroodCount(getBroodCount() - 1);
		}

		if (isJumping && isInWater()) {
			//Moving out of water
			getMoveHelper().setMoveTo(posX, posY + 1, posZ, 1.0D);
		}

		if (getIsNesting()) {
			motionX = motionY = motionZ = 0.0D;
		}

		if (getBroodCount() > 0 && getAttackTarget() == null && getIsBroody() && !getIsNesting()) {
			if(getEntityBoundingBox().intersects(getNestBox())) {
				double d0 = getBoundOrigin().getX() + 0.5D - posX;
				double d1 = getBoundOrigin().getY() - posY;
				double d2 = getBoundOrigin().getZ() + 0.5D - posZ;
				motionX += (Math.signum(d0) - motionX) * 0.0000000003D;
				motionY += (Math.signum(d1) - motionY) * 0.03125D;
				motionZ += (Math.signum(d2) - motionZ) * 0.0000000003D;

				if (getEntityBoundingBox().minY > getNestBox().minY) {
					if (!getEntityWorld().isRemote) {
						if (!getIsLanding())
							setIsLanding(true);
					}
				}
				if (getEntityBoundingBox().minY <= getNestBox().minY + 0.0625D) {
					if (!getEntityWorld().isRemote) {
						if(getIsLanding())
							setIsLanding(false);
						setIsHanging(true);
						setPosition(getBoundOrigin().getX() + 0.5D, getBoundOrigin().getY(), getBoundOrigin().getZ() + 0.5D);
					}
				}
			}
		}

		if(getEntityWorld().getBlockState(getPosition().down()).isSideSolid(getEntityWorld(), getPosition().down(), EnumFacing.UP)) {
			if(!getIsLanding() && !getIsNesting())
				getMoveHelper().setMoveTo(posX, posY + 3, posZ, 1.0D);
		}
	}

	@Override
	protected void updateAITasks() {
		if (getIsNesting()) {
			if (!getEntityWorld().isRemote) {
				if (getEntityWorld().getBlockState(new BlockPos(this.posX, this.posY - 1, this.posZ)).isNormalCube()) {
					setIsHanging(false);
				} else if (getAttackTarget() != null) {
					setIsHanging(false);
					getEntityWorld().playEvent(null, 1025, this.getPosition(), 0);
				}
			}
		}
	}

	public boolean getIsBroody() {
		return dataManager.get(IS_BROODY);
	}

	public void setIsBroody(boolean broody) {
		dataManager.set(IS_BROODY, broody);
	}
	
	public boolean getIsLanding() {
		return dataManager.get(IS_LANDING);
	}

	public void setIsLanding(boolean landing) {
		dataManager.set(IS_LANDING, landing);
	}

	public void setBroodCount(int count) {
		broodCount = count;
	}

	public int getBroodCount() {
		return broodCount;
	}

	public boolean getIsNesting() {
		return dataManager.get(IS_NESTING);
	}

	public void setIsHanging(boolean nesting) {
		dataManager.set(IS_NESTING, nesting);
	}

	@Override
	protected ResourceLocation getLootTable() {
		return LootTableRegistry.CHIROMAW;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundRegistry.FLYING_FIEND_LIVING;
	}

	@Nullable
	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundRegistry.FLYING_FIEND_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.FLYING_FIEND_DEATH;
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
		getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(20.0D);
		getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2.0D);
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.095D);
	}

	@Override
	public boolean attackEntityAsMob(Entity entityIn) {
		return EntityAIAttackOnCollide.useStandardAttack(this, entityIn);
	}

	@Override
	public int getMaxSpawnedInChunk() {
		return 1;
	}
	
	@Override
    public float getBlockPathWeight(BlockPos pos) {
        return 0.5F;
    }

    @Override
    protected boolean isValidLightLevel() {
    	return true;
    }

	@Override
	@Nullable
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
		setBoundOrigin(getPosition());
		return livingdata;
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		if (compound.hasKey("BoundX")) 
			boundOrigin = new BlockPos(compound.getInteger("BoundX"), compound.getInteger("BoundY"), compound.getInteger("BoundZ"));
		setBroodCount(compound.getInteger("BroodCount"));
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		if (boundOrigin != null) {
			compound.setInteger("BoundX", boundOrigin.getX());
			compound.setInteger("BoundY", boundOrigin.getY());
			compound.setInteger("BoundZ", boundOrigin.getZ());
		}
		compound.setInteger("BroodCount", getBroodCount());
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float damage) {
		if (source.equals(DamageSource.IN_WALL) || source.equals(DamageSource.DROWN))
			return false;
		return super.attackEntityFrom(source, damage);
	}

	@Nullable
	public BlockPos getBoundOrigin() {
		return boundOrigin;
	}

	public void setBoundOrigin(@Nullable BlockPos boundOriginIn) {
		boundOrigin = boundOriginIn;
	}
	
	public AxisAlignedBB getNestBox() {
		return new AxisAlignedBB(boundOrigin, boundOrigin.up(4)).grow(0.0625D, 0F, 0.0625D);
		
	}
	
	class AIMoveRandom extends EntityAIBase {
		private final EntityChiromawMatriarch largeChiromaw;

		public AIMoveRandom(EntityChiromawMatriarch large_chiromaw) {
			setMutexBits(1);
			largeChiromaw = large_chiromaw;
		}

		@Override
		public boolean shouldExecute() {
			return !largeChiromaw.getIsBroody() && !largeChiromaw.getMoveHelper().isUpdating() && largeChiromaw.rand.nextInt(10) == 0;
		}

		@Override
		public boolean shouldContinueExecuting() {
			return false;
		}

		@Override
		public void updateTask() {
			BlockPos blockpos = largeChiromaw.getBoundOrigin();

			if (blockpos == null) {
				blockpos = new BlockPos(largeChiromaw);
			}

			for (int i = 0; i < 3; ++i) {
				BlockPos blockpos1 = blockpos.add(largeChiromaw.rand.nextInt(33) - 16, largeChiromaw.rand.nextInt(17) - 8, largeChiromaw.rand.nextInt(33) - 16);

				if (largeChiromaw.world.isAirBlock(blockpos1)) {
					largeChiromaw.moveHelper.setMoveTo((double) blockpos1.getX() + 0.5D, (double) blockpos1.getY() + 0.5D, (double) blockpos1.getZ() + 0.5D, 3D);

					if (largeChiromaw.getAttackTarget() == null) {
						largeChiromaw.getLookHelper().setLookPosition((double) blockpos1.getX() + 0.5D, (double) blockpos1.getY() + 0.5D, (double) blockpos1.getZ() + 0.5D, 180.0F, 20.0F);
					}
					break;
				}
			}
		}
	}
	
	class AIReturnToNest extends EntityAIBase {
		private final EntityChiromawMatriarch largeChiromaw;
		protected double x;
		protected double y;
		protected double z;
		private final double speed;

		public AIReturnToNest(EntityChiromawMatriarch large_chiromaw, double speedIn) {
			largeChiromaw = large_chiromaw;
			speed = speedIn;
			setMutexBits(1);
		}

		@Override
		public boolean shouldExecute() {
			if (largeChiromaw.getIsBroody() && !largeChiromaw.getIsNesting()) {

				Vec3d nestLocation = getNestPosition();

				if (nestLocation == null) {
					return false;
				} else {

					x = nestLocation.x;
					y = nestLocation.y;
					z = nestLocation.z;
					return true;
				}
			}
			return false;
		}

		@Nullable
		protected Vec3d getNestPosition() {
			return new Vec3d(getBoundOrigin().getX() + 0.5D, largeChiromaw.getBoundOrigin().getY() + 0.5D, largeChiromaw.getBoundOrigin().getZ() + 0.5D);
		}

		@Override
		public boolean shouldContinueExecuting() {
			return !largeChiromaw.getNavigator().noPath() && !largeChiromaw.getMoveHelper().isUpdating();
		}

		@Override
		public void startExecuting() {
			if(!largeChiromaw.getMoveHelper().isUpdating()) {
				largeChiromaw.getNavigator().tryMoveToXYZ(x, y, z, speed);
			}
		}
	}
}
