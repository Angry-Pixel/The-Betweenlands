package thebetweenlands.common.entity.mobs;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIFindEntityNearestPlayer;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.IMob;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos.PooledMutableBlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory;
import thebetweenlands.client.render.particle.entity.ParticleGasCloud;
import thebetweenlands.common.entity.ai.EntityAIFlyRandomly;
import thebetweenlands.common.entity.movement.FlightMoveHelper;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class EntityGasCloud extends EntityFlying implements IMob, IEntityBL {
	public static final IAttribute GAS_CLOUD_COLOR_R = (new RangedAttribute(null, "bl.gasCloudColorRed", 104, 0, 255)).setDescription("Gas cloud color red component").setShouldWatch(true);
	public static final IAttribute GAS_CLOUD_COLOR_G = (new RangedAttribute(null, "bl.gasCloudColorGreen", 196, 0, 255)).setDescription("Gas cloud color green component").setShouldWatch(true);
	public static final IAttribute GAS_CLOUD_COLOR_B = (new RangedAttribute(null, "bl.gasCloudColorBlue", 179, 0, 255)).setDescription("Gas cloud color blue component").setShouldWatch(true);
	public static final IAttribute GAS_CLOUD_COLOR_A = (new RangedAttribute(null, "bl.gasCloudColorAlpha", 170, 0, 255)).setDescription("Gas cloud color alpha component").setShouldWatch(true);

	public List<Object> gasParticles = new ArrayList<>();

	protected double aboveLayer = 6.0D;
	protected int targetBlockedTicks = 0;

	public static final DamageSource damageSourceSuffocation = (new DamageSource("suffocation")).setDamageBypassesArmor();

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
	}

	@Override
	protected void initEntityAI() {
		this.tasks.addTask(5, new EntityAIFlyRandomly<EntityGasCloud>(this) {
			@Override
			protected double getTargetY(Random rand, double distanceMultiplier) {
				if(this.entity.posY <= 0.0D) {
					return this.entity.posY + 16.0F;
				}

				int worldHeight = 0;

				PooledMutableBlockPos checkPos = PooledMutableBlockPos.retain();

				for(int yo = 0; yo < MathHelper.ceiling_double_int(EntityGasCloud.this.aboveLayer); yo++) {
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
				return 0.015D;
			}
		});

		this.targetTasks.addTask(1, new EntityAIFindEntityNearestPlayer(this));
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
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0D);
		getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
		getAttributeMap().registerAttribute(GAS_CLOUD_COLOR_R);
		getAttributeMap().registerAttribute(GAS_CLOUD_COLOR_G);
		getAttributeMap().registerAttribute(GAS_CLOUD_COLOR_B);
		getAttributeMap().registerAttribute(GAS_CLOUD_COLOR_A);
		getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2.0D);
	}

	@Override
	public boolean getCanSpawnHere() {
		return super.getCanSpawnHere() && this.worldObj.getDifficulty() != EnumDifficulty.PEACEFUL;
	}

	@Override
	public int getMaxSpawnedInChunk() {
		return 1;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if (!this.worldObj.isRemote && this.worldObj.getDifficulty() == EnumDifficulty.PEACEFUL) {
			this.setDead();
		}

		if (this.worldObj.isRemote) {
			double x = this.posX + this.motionX + (this.worldObj.rand.nextFloat() - 0.5F) / 2.0F;
			double y = this.posY + this.height / 2.0D + this.motionY + (this.worldObj.rand.nextFloat() - 0.5F) / 2.0F;
			double z = this.posZ + this.motionZ + (this.worldObj.rand.nextFloat() - 0.5F) / 2.0F;
			double mx = this.motionX + (this.worldObj.rand.nextFloat() - 0.5F) / 16.0F;
			double my = this.motionY + (this.worldObj.rand.nextFloat() - 0.5F) / 16.0F;
			double mz = this.motionZ + (this.worldObj.rand.nextFloat() - 0.5F) / 16.0F;
			int[] color = this.getGasColor();
			ParticleGasCloud particle = (ParticleGasCloud) BLParticles.GAS_CLOUD
					.create(this.worldObj, x, y, z, ParticleFactory.ParticleArgs.get()
							.withMotion(mx, my, mz)
							.withColor(color[0] / 255.0F, color[1] / 255.0F, color[2] / 255.0F, color[3] / 255.0F));
			this.gasParticles.add(particle);

			for (int i = 0; i < this.gasParticles.size(); i++) {
				Particle gasParticle = (Particle)this.gasParticles.get(i);
				gasParticle.onUpdate();
				if (!gasParticle.isAlive()) {
					this.gasParticles.remove(i);
				}
			}
		}

		if (this.isInWater()) {
			this.moveHelper.setMoveTo(this.posX, this.posY + 1.0D, this.posZ, 0.1D);
		} else {
			if(this.getAttackTarget() != null) {
				this.moveHelper.setMoveTo(this.getAttackTarget().posX, this.getAttackTarget().posY + this.getAttackTarget().getEyeHeight(), this.getAttackTarget().posZ, 0.06D);
			}
		}

		if (!this.worldObj.isRemote && this.isEntityAlive()) {
			List<EntityLivingBase> targets = this.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox().expand(0.5D, 0.5D, 0.5D));
			for (EntityLivingBase target : targets) {
				if (!(target instanceof EntityGasCloud) && !(target instanceof IEntityBL)) {
					target.addPotionEffect(new PotionEffect(MobEffects.POISON, 60, 0));
					if (target.ticksExisted % 10 == 0)
						target.attackEntityFrom(damageSourceSuffocation, (float) this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue());
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
		return source != DamageSource.inWall && super.attackEntityFrom(source, damage);
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundRegistry.GAS_CLOUD_LIVING;
	}

	@Override
	protected SoundEvent getHurtSound() {
		return SoundRegistry.GAS_CLOUD_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.GAS_CLOUD_DEATH;
	}

	@Override
	protected void onDeathUpdate() {
		++this.deathTime;

		if(this.worldObj.isRemote) {
			for(int i = 0; i < 6; i++) {
				double x = this.posX + this.motionX + (this.worldObj.rand.nextFloat() - 0.5F) / 2.0F;
				double y = this.posY + this.height / 2.0D + this.motionY + (this.worldObj.rand.nextFloat() - 0.5F) / 2.0F;
				double z = this.posZ + this.motionZ + (this.worldObj.rand.nextFloat() - 0.5F) / 2.0F;
				int[] color = this.getGasColor();
				ParticleGasCloud particle = (ParticleGasCloud) BLParticles.GAS_CLOUD
						.create(this.worldObj, x, y, z, ParticleFactory.ParticleArgs.get()
								.withMotion((this.rand.nextFloat() - 0.5F) * this.rand.nextFloat() * 0.25F, (this.rand.nextFloat() - 0.5F) * this.rand.nextFloat() * 0.25F, (this.rand.nextFloat() - 0.5F) * this.rand.nextFloat() * 0.25F)
								.withColor(color[0] / 255.0F, color[1] / 255.0F, color[2] / 255.0F, color[3] / 255.0F));
				this.gasParticles.add(particle);
			}
		}

		if (this.deathTime >= 80) {
			if (!this.worldObj.isRemote && (this.isPlayer() || this.recentlyHit > 0 && this.canDropLoot() && this.worldObj.getGameRules().getBoolean("doMobLoot"))) {
				int i = this.getExperiencePoints(this.attackingPlayer);
				i = net.minecraftforge.event.ForgeEventFactory.getExperienceDrop(this, this.attackingPlayer, i);
				while (i > 0) {
					int j = EntityXPOrb.getXPSplit(i);
					i -= j;
					this.worldObj.spawnEntityInWorld(new EntityXPOrb(this.worldObj, this.posX, this.posY, this.posZ, j));
				}
			}

			this.setDead();
		}
	}
	
	@Override
	protected ResourceLocation getLootTable() {
		return LootTableRegistry.GAS_CLOUD;
	}
}
