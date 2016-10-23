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
import net.minecraft.entity.monster.IMob;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos.PooledMutableBlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import thebetweenlands.client.particle.BLParticles;
import thebetweenlands.client.particle.ParticleFactory;
import thebetweenlands.client.particle.entity.ParticleGasCloud;
import thebetweenlands.common.entity.ai.EntityAIFlyRandomly;
import thebetweenlands.common.entity.movement.FlightMoveHelper;

public class EntityGasCloud extends EntityFlying implements IMob, IEntityBL {
	public static final DataParameter<Integer> GAS_CLOUD_COLOR = EntityDataManager.createKey(EntityGasCloud.class, DataSerializers.VARINT);

	public List<Object> gasParticles = new ArrayList<>();

	protected double aboveLayer = 6.0D;

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
		this.tasks.addTask(5, new EntityAIFlyRandomly(this) {
			@Override
			protected double getRandomY(Random rand) {
				if(this.entity.posY <= 0.0D) {
					return this.entity.posY + 16.0F;
				}

				int worldHeight = 0;

				PooledMutableBlockPos checkPos = PooledMutableBlockPos.retain();

				for(int yo = 0; yo < MathHelper.ceiling_double_int(EntityGasCloud.this.aboveLayer); yo++) {
					checkPos.setPos(this.entity.posX, this.entity.posY - yo, this.entity.posZ);

					if(!this.entity.getEntityWorld().isAirBlock(checkPos)) {
						worldHeight = checkPos.getY();
						break;
					}
				}

				checkPos.release();

				if(this.entity.posY > worldHeight + EntityGasCloud.this.aboveLayer) {
					return this.entity.posY + (-rand.nextFloat() * 2.0F) * 16.0F;
				} else {
					float rndFloat = rand.nextFloat() * 2.0F - 1.0F;
					if(rndFloat > 0.0D) {
						double maxRange = worldHeight + EntityGasCloud.this.aboveLayer - this.entity.posY;
						return this.entity.posY + (-rand.nextFloat() * 2.0F) * maxRange;
					} else {
						return this.entity.posY + (rand.nextFloat() * 2.0F - 1.0F) * 16.0F;
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

	public void setGasColor(int color) {
		this.dataManager.set(GAS_CLOUD_COLOR, color);
	}

	public int getGasColor() {
		return this.dataManager.get(GAS_CLOUD_COLOR);
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0D);
		getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataManager.register(GAS_CLOUD_COLOR, 0xAA68C4B3);
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
			ParticleGasCloud particle = (ParticleGasCloud) BLParticles.GAS_CLOUD
					.create(this.worldObj, x, y, z, ParticleFactory.ParticleArgs.get()
							.withMotion(mx, my, mz)
							.withColor(this.getGasColor()));
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
				this.moveHelper.setMoveTo(this.getAttackTarget().posX, this.getAttackTarget().posY, this.getAttackTarget().posZ, 0.06D);
			}
		}

		if (!this.worldObj.isRemote && this.isEntityAlive()) {
			List<EntityLivingBase> targets = this.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox().expand(0.5D, 0.5D, 0.5D));
			for (EntityLivingBase target : targets) {
				if (!(target instanceof EntityGasCloud) && !(target instanceof IEntityBL)) {
					target.addPotionEffect(new PotionEffect(MobEffects.POISON, 60, 0));
					if (target.ticksExisted % 10 == 0)
						target.attackEntityFrom(DamageSource.drown, 2.0F);
				}
			}
		}
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setInteger("gasColor", this.getGasColor());
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		this.setGasColor(nbt.getInteger("gasColor"));
	}

	@Override
	public boolean shouldRenderInPass(int pass) {
		return pass == 1;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float damage) {
		return source != DamageSource.inWall && super.attackEntityFrom(source, damage);
	}
}
