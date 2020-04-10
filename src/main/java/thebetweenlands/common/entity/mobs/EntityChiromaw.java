package thebetweenlands.common.entity.mobs;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.common.entity.ai.EntityAIAttackOnCollide;
import thebetweenlands.common.entity.ai.EntityAIFlyingWander;
import thebetweenlands.common.entity.movement.FlightMoveHelper;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class EntityChiromaw extends EntityFlyingMob implements IEntityBL {
	private static final DataParameter<Boolean> IS_HANGING = EntityDataManager.createKey(EntityChiromaw.class, DataSerializers.BOOLEAN);

	public EntityChiromaw(World world) {
		super(world);
		setSize(0.7F, 0.9F);
		setIsHanging(false);

		this.moveHelper = new FlightMoveHelper(this);
		setPathPriority(PathNodeType.WATER, -8F);
		setPathPriority(PathNodeType.BLOCKED, -8.0F);
		setPathPriority(PathNodeType.OPEN, 8.0F);
		setPathPriority(PathNodeType.FENCE, -8.0F);
	}

	@Override
	protected void initEntityAI() {
		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(1, new EntityAIAttackMelee(this, 1.0D, true));
		this.tasks.addTask(2, new EntityAIFlyingWander(this, 0.5D));
		this.targetTasks.addTask(1, new EntityAINearestAttackableTarget<EntityPlayer>(this, EntityPlayer.class, true).setUnseenMemoryTicks(160));
	}

	@Override
	protected void entityInit() {
		super.entityInit();

		this.dataManager.register(IS_HANGING, false);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if (!world.isRemote && world.getDifficulty() == EnumDifficulty.PEACEFUL) {
			setDead();
		}

		if (this.isJumping && this.isInWater()) {
			//Moving out of water
			this.getMoveHelper().setMoveTo(this.posX, this.posY + 1, this.posZ, 1.0D);
		}

		if (this.getIsHanging()) {
			this.motionX = this.motionY = this.motionZ = 0.0D;
			if(!(this instanceof EntityLargeChiromaw))
				this.posY = (double) MathHelper.floor(this.posY) + 1.0D - (double) this.height;
		}

		if (motionY < 0.0D && this.getAttackTarget() == null) {
			motionY *= 0.25D;
		}

		if(getEntityWorld().getBlockState(getPosition().down()).isSideSolid(getEntityWorld(), getPosition().down(), EnumFacing.UP)) {
			getMoveHelper().setMoveTo(this.posX, this.posY + 1, this.posZ, 1.0D);
		}
	}

	@Override
	protected void updateAITasks() {
		super.updateAITasks();

		if (getIsHanging()) {
			if (!this.world.isRemote) {
				this.moveHelper.setMoveTo(this.posX, this.posY + 0.5D, this.posZ, 0);

				if (this.rand.nextInt(250) == 0 || !this.world.getBlockState(new BlockPos(this.posX, this.posY + 1, this.posZ)).isNormalCube()) {
					setIsHanging(false);
					this.world.playEvent(null, 1025, this.getPosition(), 0);
				} else if (this.getAttackTarget() != null) {
					setIsHanging(false);
					this.world.playEvent(null, 1025, this.getPosition(), 0);
				}
			}
		} else {
			if (this.getAttackTarget() == null) {
				if (!this.world.isRemote && this.rand.nextInt(20) == 0 && world.getBlockState(new BlockPos(this.posX, this.posY + 1, this.posZ)).isNormalCube()) {
					setIsHanging(true);
				}
			}
		}
	}

	public boolean getIsHanging() {
		return dataManager.get(IS_HANGING);
	}

	public void setIsHanging(boolean hanging) {
		dataManager.set(IS_HANGING, hanging);
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
	protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
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
		return 3;
	}
	
	@Override
    public float getBlockPathWeight(BlockPos pos) {
        return 0.5F;
    }

    @Override
    protected boolean isValidLightLevel() {
    	return true;
    }
}
