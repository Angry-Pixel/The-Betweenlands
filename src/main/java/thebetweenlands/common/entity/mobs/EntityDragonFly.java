package thebetweenlands.common.entity.mobs;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class EntityDragonFly extends EntityAmbientCreature implements IEntityBL {
	public BlockPos currentFlightTarget;
	public boolean entityFlying;

	public EntityDragonFly(World world) {
		super(world);
		setSize(0.9F, 0.5F);
	}

	@Override
	protected void initEntityAI() {
		tasks.addTask(0, new EntityAISwimming(this));
		tasks.addTask(1, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
		tasks.addTask(2, new EntityAILookIdle(this));
	}
	
	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.9D);
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
		getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(16.0D);
	}

	@Override
	public boolean isAIDisabled() {
		return false;
	}

	@Override
	public int getMaxSpawnedInChunk() {
		return 2;
	}

	@Override
	public EnumCreatureAttribute getCreatureAttribute() {
		return EnumCreatureAttribute.ARTHROPOD;
	}

	@Override
	public void fall(float distance, float damageMultiplier) {
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundRegistry.DRAGONFLY;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.SQUISH;
	}

	@Override
	protected void playStepSound(BlockPos pos, Block blockIn) {
		playSound(SoundEvents.ENTITY_SPIDER_STEP, 0.15F, 1.0F);
	}

	@Override
	protected ResourceLocation getLootTable() {
		return LootTableRegistry.DRAGONFLY;
	}

	public boolean isFlying() {
		return !onGround;
	}

	public void setEntityFlying(boolean state) {
		entityFlying = state;
	}

	@Override
	public void onUpdate() {
		if (motionY < 0.0D) {
			motionY *= 0.6D;
		}
		if (!world.isRemote) {
			if (rand.nextInt(200) == 0) {
				if (!entityFlying) {
					setEntityFlying(true);
				} else {
					setEntityFlying(false);
				}
			}
			if (entityFlying) {
				flyAbout();
			} else {
				land();
			}
			if (!entityFlying) {
				if (isInWater()) {
					motionY += 0.2F;
				}
				if (world.containsAnyLiquid(getEntityBoundingBox().expand(0D, 1D, 0D))) {
					flyAbout();
				}
				if (world.getClosestPlayerToEntity(this, 4.0D) != null) {
					flyAbout();
				}
			}
		}
		super.onUpdate();
	}

	public void flyAbout() {
		if (currentFlightTarget != null) {
			if (!world.isAirBlock(currentFlightTarget) || currentFlightTarget.getY() < 1 || world.getBlockState(currentFlightTarget.up()).getBlock() == Blocks.WATER) {
				currentFlightTarget = null;
			}
		}
		if (currentFlightTarget == null || rand.nextInt(30) == 0 || currentFlightTarget.getDistance((int) posX, (int) posY, (int) posZ) < 10F) {
			currentFlightTarget = new BlockPos((int) posX + rand.nextInt(7) - rand.nextInt(7), (int) posY + rand.nextInt(6) - 1, (int) posZ + rand.nextInt(7) - rand.nextInt(7));
		}
		flyToTarget();
	}

	public void flyToTarget() {
		if (currentFlightTarget != null) {
			double targetX = currentFlightTarget.getX() + 0.5D - posX;
			double targetY = currentFlightTarget.getY() + 1D - posY;
			double targetZ = currentFlightTarget.getZ() + 0.5D - posZ;
			motionX += (Math.signum(targetX) * 0.5D - motionX) * 0.1D;
			motionY += (Math.signum(targetY) * 0.7D - motionY) * 0.1D;
			motionZ += (Math.signum(targetZ) * 0.5D - motionZ) * 0.1D;
			float angle = (float) (Math.atan2(motionZ, motionX) * 180.0D / Math.PI) - 90.0F;
			float rotation = MathHelper.wrapDegrees(angle - rotationYaw);
			moveForward = 0.5F;
			rotationYaw += rotation;
		}
	}

	private void land() {
		// Nothing to see here - yet
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean isInRangeToRender3d(double x, double y, double z) {
		return !isInLurkersMouth() && super.isInRangeToRender3d(x, y, z);
	}

	@Override
	public boolean canBeCollidedWith() {
		return !isInLurkersMouth();
	}

	@Override
	protected boolean canDespawn() {
		return false;
	}

	@Override
	public boolean canEntityBeSeen(Entity entity) {
		return !isInLurkersMouth() && super.canEntityBeSeen(entity);
	}

	private boolean isInLurkersMouth() {
		return getRidingEntity() instanceof EntityLurker;
	}

	@Override
	protected void onDeathUpdate() {
		deathTime++;
		if (deathTime == 20) {
			if (!world.isRemote && (recentlyHit > 0 || isPlayer()) && !this.isChild() && world.getGameRules().getBoolean("doMobLoot")) {
				int experiencePoints = getExperiencePoints(attackingPlayer);
				while (experiencePoints > 0) {
					int amount = EntityXPOrb.getXPSplit(experiencePoints);
					experiencePoints -= amount;
					world.spawnEntity(new EntityXPOrb(world, posX, posY, posZ, amount));
				}
			}
			setDead();
			if (!isInLurkersMouth()) {
				for (int particle = 0; particle < 20; particle++) {
					double motionX = rand.nextGaussian() * 0.02D;
					double motionY = rand.nextGaussian() * 0.02D;
					double motionZ = rand.nextGaussian() * 0.02D;
					world.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, posX + rand.nextFloat() * width * 2.0F - width, posY + rand.nextFloat() * height, posZ + rand.nextFloat() * width * 2.0F - width, motionX, motionY, motionZ);
				}
			}
		}
	}

	@Override
	protected boolean canBeRidden(Entity entityIn) {
		return super.canBeRidden(entityIn);
	}
}
