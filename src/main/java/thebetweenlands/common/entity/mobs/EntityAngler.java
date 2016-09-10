package thebetweenlands.common.entity.mobs;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.util.AnimationMathHelper;

import javax.annotation.Nullable;

public class EntityAngler extends EntityWaterMob implements IEntityBL, IMob {
	private static final DataParameter<Boolean> IS_LEAPING = EntityDataManager.createKey(EntityAngler.class, DataSerializers.BOOLEAN);
	public float moveProgress;
	AnimationMathHelper animation = new AnimationMathHelper();
	private BlockPos currentSwimTarget;

	public EntityAngler(World world) {
		super(world);
		setSize(1F, 0.7F);
		setAir(80);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(IS_LEAPING, false);
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.6D);
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(34.0D);
		getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(16.0D);
	}

	@Override
	protected SoundEvent getHurtSound() {
		return super.getHurtSound();
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.ANGLER_DEATH;
	}

	@Override
	protected SoundEvent getSwimSound() {
		return SoundEvents.ENTITY_HOSTILE_SWIM;
	}

	@Override
	protected float getSoundVolume() {
		return 0.4F;
	}


	@Nullable
	@Override
	protected ResourceLocation getLootTable() {
		return LootTableRegistry.ANGLER;
	}

	@Override
	public boolean getCanSpawnHere() {
		return worldObj.getDifficulty() != EnumDifficulty.PEACEFUL && worldObj.getBlockState(new BlockPos(MathHelper.floor_double(posX), MathHelper.floor_double(posY), MathHelper.floor_double(posZ))).getBlock() == BlockRegistry.SWAMP_WATER;
	}

	@Override
	public boolean isInWater() {
		return worldObj.handleMaterialAcceleration(getEntityBoundingBox(), Material.WATER, this);
	}

	public boolean isGrounded() {
		return !isInWater() && worldObj.isAirBlock(new BlockPos(MathHelper.floor_double(posX), MathHelper.floor_double(posY + 1), MathHelper.floor_double(posZ))) && worldObj.getBlockState(new BlockPos(MathHelper.floor_double(posX), MathHelper.floor_double(posY - 1), MathHelper.floor_double(posZ))).getBlock().isCollidable();
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();

		if (!this.worldObj.isRemote) {
			EntityPlayer target = worldObj.getClosestPlayerToEntity(this, 16.0D);
			setAttackTarget(target);
			if (isInWater()) {
				if (!worldObj.isRemote) {
					if (getAttackTarget() != null) {
						currentSwimTarget = new BlockPos((int) getAttackTarget().posX, (int) ((int) getAttackTarget().posY + getAttackTarget().getEyeHeight()), (int) getAttackTarget().posZ);
						swimToTarget();
					} else
						swimAbout();
				}
			} else {
				if (!worldObj.isRemote) {
					if (!onGround) {
						motionX = 0.0D;
						motionY -= 0.08D;
						motionY *= 0.9800000190734863D;
						motionZ = 0.0D;
					} else {
						setIsLeaping(false);
						motionY += 0.4F;
						motionX += (rand.nextFloat() - rand.nextFloat()) * 0.3F;
						motionZ += (rand.nextFloat() - rand.nextFloat()) * 0.3F;
					}
				}
			}
		} else {
			if (isInWater()) {
				moveProgress = animation.swing(1.2F, 0.4F, false);
				renderYawOffset += (-((float) Math.atan2(motionX, motionZ)) * 180.0F / (float) Math.PI - renderYawOffset) * 0.1F;
				rotationYaw = renderYawOffset;
			} else {
				moveProgress = animation.swing(2F, 0.4F, false);
			}
		}

	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (worldObj.getDifficulty() == EnumDifficulty.PEACEFUL)
			setDead();
	}

	@Override
	public void onEntityUpdate() {
		int air = getAir();
		super.onEntityUpdate();

		if (isEntityAlive() && !isInWater()) {
			--air;
			setAir(air);

			if (getAir() == -20) {
				setAir(0);
				attackEntityFrom(DamageSource.drown, 2.0F);
			}
		} else
			setAir(80);
	}

	private void swimAbout() {
		if (currentSwimTarget != null && (worldObj.getBlockState(currentSwimTarget).getBlock() != BlockRegistry.SWAMP_WATER && worldObj.getBlockState(currentSwimTarget).getBlock() != Blocks.WATER || currentSwimTarget.getY() < 1))
			currentSwimTarget = null;

		if (currentSwimTarget == null || rand.nextInt(30) == 0 || currentSwimTarget.getDistance((int) posX, (int) posY, (int) posZ) < 10.0F)
			currentSwimTarget = new BlockPos((int) posX + rand.nextInt(10) - rand.nextInt(10), (int) posY - rand.nextInt(6) + 2, (int) posZ + rand.nextInt(10) - rand.nextInt(10));

		swimToTarget();
	}

	private void swimToTarget() {
		double targetX = currentSwimTarget.getX() + 0.5D - posX;
		double targetY = currentSwimTarget.getY() + 0.5D - posY;
		double targetZ = currentSwimTarget.getZ() + 0.5D - posZ;
		motionX += (Math.signum(targetX) * 0.3D - motionX) * 0.1D;
		motionY += (Math.signum(targetY) * 0.4D - motionY) * 0.08D;
		motionY -= 0.01D;
		motionZ += (Math.signum(targetZ) * 0.3D - motionZ) * 0.1D;
		moveForward = 0.5F;
	}

	@Override
	public void onCollideWithPlayer(EntityPlayer player) {
		super.onCollideWithPlayer(player);
		if (!player.capabilities.isCreativeMode && !worldObj.isRemote && getEntitySenses().canSee(player)) {
			if (getDistanceToEntity(player) <= 1.5F)
				if (player.getEntityBoundingBox().maxY >= getEntityBoundingBox().minY && player.getEntityBoundingBox().minY <= getEntityBoundingBox().maxY) {
					player.attackEntityFrom(DamageSource.causeMobDamage(this), 2F);
				}
		}
	}


	@Override
	public boolean attackEntityAsMob(Entity entityIn) {
		Double distance = this.getPosition().getDistance((int) entityIn.posX, (int) entityIn.posY, (int) entityIn.posZ);
		if (distance > 2.0F && distance < 6.0F && entityIn.getEntityBoundingBox().maxY >= getEntityBoundingBox().minY && entityIn.getEntityBoundingBox().minY <= getEntityBoundingBox().maxY && rand.nextInt(3) == 0)
			if (isInWater() && worldObj.isAirBlock(new BlockPos((int) posX, (int) posY + 1, (int) posZ))) {
				setIsLeaping(true);
				double distanceX = entityIn.posX - posX;
				double distanceZ = entityIn.posZ - posZ;
				float distanceSqrRoot = MathHelper.sqrt_double(distanceX * distanceX + distanceZ * distanceZ);
				motionX = distanceX / distanceSqrRoot * 1.5D * 0.900000011920929D + motionX * 2.70000000298023224D;
				motionZ = distanceZ / distanceSqrRoot * 1.5D * 0.900000011920929D + motionZ * 2.70000000298023224D;
				motionY = 0.8D;
				return true;
			}
		return false;
	}

	public boolean isLeaping() {
		return dataManager.get(IS_LEAPING);
	}

	private void setIsLeaping(boolean leaping) {
		dataManager.set(IS_LEAPING, leaping);
	}

}
