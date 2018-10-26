package thebetweenlands.common.entity.mobs;

import java.util.List;
import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.PooledMutableBlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.common.entity.ai.EntityAIFlyRandomly;
import thebetweenlands.common.entity.movement.FlightMoveHelper;
import thebetweenlands.common.registries.SoundRegistry;

public class EntityDarkLight extends EntityFlyingMob implements IEntityBL {
	protected double aboveLayer = 6.0D;
	protected int targetBlockedTicks = 0;
    private static final float ROTATION_SPEED = 2.0F;
    public float rotation;
    public float prevRotation;

	public static final DamageSource DAMAGE_WITHER = (new DamageSource("wither")).setDamageBypassesArmor();

	public EntityDarkLight(World world) {
		super(world);
		this.setSize(1.75F, 1.75F);
		this.noClip = true;
		this.ignoreFrustumCheck = true;
		this.moveHelper = new FlightMoveHelper(this) {
			@Override
			protected boolean isNotColliding(double x, double y, double z, double step) {
				double stepX = (x - this.entity.posX) / step;
				double stepY = (y - this.entity.posY) / step;
				double stepZ = (z - this.entity.posZ) / step;

				double cx = this.entity.posX;
				double cy = this.entity.posY;
				double cz = this.entity.posZ;

				boolean canPassSolidBlocks = ((EntityDarkLight) this.entity).getAttackTarget() != null;

				PooledMutableBlockPos checkPos = PooledMutableBlockPos.retain();

				for(int i = 1; (double)i < step; ++i) {
					cx += stepX;
					cy += stepY;
					cz += stepZ;

					checkPos.setPos(cx, cy, cz);

					if(this.entity.getEntityWorld().isBlockLoaded(checkPos)) {
						IBlockState state = this.entity.getEntityWorld().getBlockState(checkPos);

						if ((!canPassSolidBlocks && state.isOpaqueCube()) || state.getMaterial().isLiquid()) {
							return false;
						}
					} else {
						return false;
					}
				}

				checkPos.release();

				return true;
			}
		};

		setPathPriority(PathNodeType.WATER, -8F);
		setPathPriority(PathNodeType.BLOCKED, -8.0F);
		setPathPriority(PathNodeType.OPEN, 8.0F);
	}

	@Override
	protected void initEntityAI() {
		this.tasks.addTask(1, new EntityAIFlyRandomly<EntityDarkLight>(this) {
			@Override
			protected double getTargetY(Random rand, double distanceMultiplier) {
				if(this.entity.posY <= 0.0D) {
					return this.entity.posY + 16.0F;
				}

				int worldHeight = 0;

				PooledMutableBlockPos checkPos = PooledMutableBlockPos.retain();

				for(int yo = 0; yo < MathHelper.ceil(EntityDarkLight.this.aboveLayer); yo++) {
					checkPos.setPos(this.entity.posX, this.entity.posY - yo, this.entity.posZ);

					if(!this.entity.getEntityWorld().isBlockLoaded(checkPos))
						return this.entity.posY;

					if(!this.entity.getEntityWorld().isAirBlock(checkPos)) {
						worldHeight = checkPos.getY();
						break;
					}
				}

				checkPos.release();

				if(this.entity.posY > worldHeight + EntityDarkLight.this.aboveLayer) {
					return this.entity.posY + (-rand.nextFloat() * 2.0F) * 16.0F * distanceMultiplier;
				} else {
					float rndFloat = rand.nextFloat() * 2.0F - 1.0F;
					if(rndFloat > 0.0D) {
						double maxRange = worldHeight + EntityDarkLight.this.aboveLayer - this.entity.posY;
						return this.entity.posY + (-rand.nextFloat() * 2.0F) * maxRange * distanceMultiplier;
					} else {
						return this.entity.posY + (rand.nextFloat() * 2.0F - 1.0F) * 16.0F * distanceMultiplier;
					}
				}
			}

			@Override
			protected double getFlightSpeed() {
				return 0.3D;
			}
		});


		targetTasks.addTask(1, new EntityAINearestAttackableTarget<EntityPlayer>(this, EntityPlayer.class, false));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.065D);
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0D);
		getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
		getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2.0D);
	}

	@Override
	public boolean getCanSpawnHere() {
		return super.getCanSpawnHere() && this.world.getDifficulty() != EnumDifficulty.PEACEFUL;
	}

	@Override
	public int getMaxSpawnedInChunk() {
		return 1;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if (this.getEntityWorld().isRemote) {
			this.prevRotation = this.rotation;
			this.rotation += ROTATION_SPEED;
			if (this.rotation >= 360.0F) {
				this.rotation -= 360.0F;
				this.prevRotation -= 360.0F;
			}
		}

		if (!this.world.isRemote && this.world.getDifficulty() == EnumDifficulty.PEACEFUL) {
			this.setDead();
		}

		if (this.isInWater()) {
			this.moveHelper.setMoveTo(this.posX, this.posY + 1.0D, this.posZ, 1.0D);
		} else {
			if(this.getAttackTarget() != null) {
				this.moveHelper.setMoveTo(this.getAttackTarget().posX, this.getAttackTarget().posY + this.getAttackTarget().getEyeHeight(), this.getAttackTarget().posZ, 1.0D);
			}
		}

		if (!this.world.isRemote && this.isEntityAlive()) {
			List<EntityLivingBase> targets = this.world.getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox().grow(0.5D, 0.5D, 0.5D));
			for (EntityLivingBase target : targets) {
				if (!(target instanceof EntityDarkLight) && !(target instanceof IEntityBL)) {
					target.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 60, 0));
					if (target.ticksExisted % 10 == 0)
						target.attackEntityFrom(DAMAGE_WITHER, (float) this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue());
				}
			}
		}
	}

	@Override
	public boolean shouldRenderInPass(int pass) {
		return pass == 1;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float damage) {
		return source != DamageSource.IN_WALL && super.attackEntityFrom(source, damage);
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundRegistry.GAS_CLOUD_LIVING;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundRegistry.GAS_CLOUD_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.GAS_CLOUD_DEATH;
	}

	@Override
	protected void onDeathUpdate() {
		++this.deathTime;

		if (this.deathTime >= 80) {
			if (!this.world.isRemote && (this.isPlayer() || this.recentlyHit > 0 && this.canDropLoot() && this.world.getGameRules().getBoolean("doMobLoot"))) {
				int i = this.getExperiencePoints(this.attackingPlayer);
				i = net.minecraftforge.event.ForgeEventFactory.getExperienceDrop(this, this.attackingPlayer, i);
				while (i > 0) {
					int j = EntityXPOrb.getXPSplit(i);
					i -= j;
					this.world.spawnEntity(new EntityXPOrb(this.world, this.posX, this.posY, this.posZ, j));
				}
			}

			this.setDead();
		}
	}

	@Override
	protected ResourceLocation getLootTable() {
		return null;//LootTableRegistry.DARK_LIGHT;
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
