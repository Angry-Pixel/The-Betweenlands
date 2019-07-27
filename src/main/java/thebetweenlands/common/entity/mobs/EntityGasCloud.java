package thebetweenlands.common.entity.mobs;

import java.util.List;
import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.BatchedParticleRenderer;
import thebetweenlands.client.render.particle.DefaultParticleBatches;
import thebetweenlands.client.render.particle.BatchedParticleRenderer.ParticleBatch;
import thebetweenlands.client.render.particle.ParticleBatchTypeBuilder;
import thebetweenlands.client.render.particle.ParticleFactory;
import thebetweenlands.client.render.particle.entity.ParticleGasCloud;
import thebetweenlands.common.entity.ai.EntityAIFlyRandomly;
import thebetweenlands.common.entity.movement.FlightMoveHelper;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class EntityGasCloud extends EntityFlyingMob implements IEntityBL {
	public static final IAttribute GAS_CLOUD_COLOR_R = (new RangedAttribute(null, "bl.gasCloudColorRed", 104, 0, 255)).setDescription("Gas cloud color red component").setShouldWatch(true);
	public static final IAttribute GAS_CLOUD_COLOR_G = (new RangedAttribute(null, "bl.gasCloudColorGreen", 196, 0, 255)).setDescription("Gas cloud color green component").setShouldWatch(true);
	public static final IAttribute GAS_CLOUD_COLOR_B = (new RangedAttribute(null, "bl.gasCloudColorBlue", 179, 0, 255)).setDescription("Gas cloud color blue component").setShouldWatch(true);
	public static final IAttribute GAS_CLOUD_COLOR_A = (new RangedAttribute(null, "bl.gasCloudColorAlpha", 170, 0, 255)).setDescription("Gas cloud color alpha component").setShouldWatch(true);

	protected double aboveLayer = 6.0D;
	protected int targetBlockedTicks = 0;

	public static final DamageSource damageSourceSuffocation = (new DamageSource("suffocation")).setDamageBypassesArmor();

	@SideOnly(Side.CLIENT)
	private ParticleBatch particleBatch;
	
	public EntityGasCloud(World world) {
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

				boolean canPassSolidBlocks = ((EntityGasCloud) this.entity).getAttackTarget() != null;

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
		
		if(this.world.isRemote) {
			this.initParticleBatch();
		}
	}

	@SideOnly(Side.CLIENT)
	private void initParticleBatch() {
		this.particleBatch = BatchedParticleRenderer.INSTANCE.createBatchType(new ParticleBatchTypeBuilder().pass().depthMaskPass(true).texture((ResourceLocation)null).end().build());
	}
	
	@Override
	protected void initEntityAI() {
		this.tasks.addTask(1, new EntityAIFlyRandomly<EntityGasCloud>(this) {
			@Override
			protected double getTargetY(Random rand, double distanceMultiplier) {
				if(this.entity.posY <= 0.0D) {
					return this.entity.posY + 16.0F;
				}

				int worldHeight = 0;

				PooledMutableBlockPos checkPos = PooledMutableBlockPos.retain();

				for(int yo = 0; yo < MathHelper.ceil(EntityGasCloud.this.aboveLayer); yo++) {
					checkPos.setPos(this.entity.posX, this.entity.posY - yo, this.entity.posZ);

					if(!this.entity.getEntityWorld().isBlockLoaded(checkPos))
						return this.entity.posY;

					if(!this.entity.getEntityWorld().isAirBlock(checkPos)) {
						worldHeight = checkPos.getY();
						break;
					}
				}

				checkPos.release();

				if(this.entity.posY > worldHeight + EntityGasCloud.this.aboveLayer) {
					return this.entity.posY + (-rand.nextFloat() * 2.0F) * 16.0F * distanceMultiplier;
				} else {
					float rndFloat = rand.nextFloat() * 2.0F - 1.0F;
					if(rndFloat > 0.0D) {
						double maxRange = worldHeight + EntityGasCloud.this.aboveLayer - this.entity.posY;
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
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(16.0D);
		getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
		getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2.0D);
		getAttributeMap().registerAttribute(GAS_CLOUD_COLOR_R);
		getAttributeMap().registerAttribute(GAS_CLOUD_COLOR_G);
		getAttributeMap().registerAttribute(GAS_CLOUD_COLOR_B);
		getAttributeMap().registerAttribute(GAS_CLOUD_COLOR_A);
	}

	/**
	 * Sets the gas color
	 * @param r
	 * @param g
	 * @param b
	 * @param a
	 */
	public void setGasColor(int r, int g, int b, int a) {
		this.getEntityAttribute(GAS_CLOUD_COLOR_R).setBaseValue(r);
		this.getEntityAttribute(GAS_CLOUD_COLOR_G).setBaseValue(g);
		this.getEntityAttribute(GAS_CLOUD_COLOR_B).setBaseValue(b);
		this.getEntityAttribute(GAS_CLOUD_COLOR_A).setBaseValue(a);
	}

	/**
	 * Returns the gas color in an array [red, green, blue, alpha]
	 * @return
	 */
	public int[] getGasColor() {
		return new int[] { (int)this.getEntityAttribute(GAS_CLOUD_COLOR_R).getAttributeValue(),
				(int)this.getEntityAttribute(GAS_CLOUD_COLOR_G).getAttributeValue(),
				(int)this.getEntityAttribute(GAS_CLOUD_COLOR_B).getAttributeValue(),
				(int)this.getEntityAttribute(GAS_CLOUD_COLOR_A).getAttributeValue() };
	};

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

		if (!this.world.isRemote && this.world.getDifficulty() == EnumDifficulty.PEACEFUL) {
			this.setDead();
		}

		if (this.world.isRemote) {
			this.spawnCloudParticle(false);
			this.updateParticleBatch();
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
				if (!(target instanceof EntityGasCloud) && !(target instanceof IEntityBL)) {
					target.addPotionEffect(new PotionEffect(MobEffects.POISON, 60, 0));
					if (target.ticksExisted % 10 == 0)
						target.attackEntityFrom(damageSourceSuffocation, (float) this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue());
				}
			}
		}
	}
	
	@SideOnly(Side.CLIENT)
	private void updateParticleBatch() {
		BatchedParticleRenderer.INSTANCE.updateBatch(this.particleBatch);
	}

	@SideOnly(Side.CLIENT)
	private void spawnCloudParticle(boolean strongMotion) {
		if(strongMotion) {
			double x = this.posX + this.motionX + (this.world.rand.nextFloat() - 0.5F) / 2.0F;
			double y = this.posY + this.height / 2.0D + this.motionY + (this.world.rand.nextFloat() - 0.5F) / 2.0F;
			double z = this.posZ + this.motionZ + (this.world.rand.nextFloat() - 0.5F) / 2.0F;
			int[] color = this.getGasColor();

			ParticleGasCloud particle = (ParticleGasCloud) BLParticles.GAS_CLOUD
					.create(this.world, x, y, z, ParticleFactory.ParticleArgs.get()
							.withData(this)
							.withMotion((this.rand.nextFloat() - 0.5F) * this.rand.nextFloat() * 0.25F, (this.rand.nextFloat() - 0.5F) * this.rand.nextFloat() * 0.25F, (this.rand.nextFloat() - 0.5F) * this.rand.nextFloat() * 0.25F)
							.withColor(color[0] / 255.0F, color[1] / 255.0F, color[2] / 255.0F, color[3] / 255.0F));

			BatchedParticleRenderer.INSTANCE.addParticle(this.particleBatch, particle);
			BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.GAS_CLOUDS_HEAT_HAZE, particle);
		} else {
			double x = this.posX + this.motionX + (this.world.rand.nextFloat() - 0.5F) / 2.0F;
			double y = this.posY + this.height / 2.0D + this.motionY + (this.world.rand.nextFloat() - 0.5F) / 2.0F;
			double z = this.posZ + this.motionZ + (this.world.rand.nextFloat() - 0.5F) / 2.0F;
			double mx = this.motionX + (this.world.rand.nextFloat() - 0.5F) / 16.0F;
			double my = this.motionY + (this.world.rand.nextFloat() - 0.5F) / 16.0F;
			double mz = this.motionZ + (this.world.rand.nextFloat() - 0.5F) / 16.0F;
			int[] color = this.getGasColor();

			ParticleGasCloud particle = (ParticleGasCloud) BLParticles.GAS_CLOUD
					.create(this.world, x, y, z, ParticleFactory.ParticleArgs.get()
							.withData(this)
							.withMotion(mx, my, mz)
							.withColor(color[0] / 255.0F, color[1] / 255.0F, color[2] / 255.0F, color[3] / 255.0F));

			BatchedParticleRenderer.INSTANCE.addParticle(this.particleBatch, particle);
			BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.GAS_CLOUDS_HEAT_HAZE, particle);
		}
	}
	
	@SideOnly(Side.CLIENT)
	public ParticleBatch getParticleBatch() {
		return this.particleBatch;
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

		if(this.world.isRemote) {
			for(int i = 0; i < 6; i++) {
				this.spawnCloudParticle(true);
			}
		}

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
		return LootTableRegistry.GAS_CLOUD;
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
