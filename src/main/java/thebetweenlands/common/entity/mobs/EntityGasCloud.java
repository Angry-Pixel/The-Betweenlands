package thebetweenlands.common.entity.mobs;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import thebetweenlands.client.particle.BLParticles;
import thebetweenlands.client.particle.ParticleFactory;
import thebetweenlands.client.particle.entity.ParticleGasCloud;

//TODO: Rewrite with new AI and movement (see EntityGhast)
public class EntityGasCloud extends EntityFlying implements IMob, IEntityBL {
	public static final DataParameter<Integer> GAS_CLOUD_COLOR = EntityDataManager.createKey(EntityGasCloud.class, DataSerializers.VARINT);

	public List<Object> gasParticles = new ArrayList<>();

	private int courseChangeCooldown;
	private double waypointX;
	private double waypointY;
	private double waypointZ;

	private double aboveLayer = 6.0D;

	public EntityGasCloud(World world) {
		super(world);
		this.setSize(1.75F, 1.75F);
		this.noClip = true;
		this.ignoreFrustumCheck = true;
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
	protected void updateAITasks() {
		if (!this.worldObj.isRemote && this.worldObj.getDifficulty() == EnumDifficulty.PEACEFUL) {
			this.setDead();
		}

		super.updateAITasks();

		if (this.worldObj.isRemote) {
			return;
		}

		double dx = this.waypointX - this.posX;
		double dy = this.waypointY - this.posY;
		double dz = this.waypointZ - this.posZ;
		double dist = dx * dx + dy * dy + dz * dz;

		if (dist < 1.0D || dist > 3600.0D) {
			this.waypointX = this.posX + (this.rand.nextFloat() * 2.0F - 1.0F) * 16.0F;
			this.waypointY = this.posY + (this.rand.nextFloat() * 2.0F - 1.5F) * 6.0F;
			this.waypointZ = this.posZ + (this.rand.nextFloat() * 2.0F - 1.0F) * 16.0F;
		}

		float speed = 0.02F;

		EntityPlayer closestTarget = this.worldObj.getNearestAttackablePlayer(this, 16.0D, 16.0D);
		if (closestTarget != null) {
			this.waypointX = closestTarget.posX;
			this.waypointY = closestTarget.posY;
			this.waypointZ = closestTarget.posZ;
			speed = 0.05F;
		}

		if (this.courseChangeCooldown-- <= 0) {
			this.courseChangeCooldown += this.rand.nextInt(5) + 2;
			dist = Math.min(MathHelper.sqrt_double(dist), 23); //Limit steps

			if (this.isCourseTraversable(this.waypointX, this.waypointY, this.waypointZ, dist, closestTarget != null)) {
				this.motionX += dx / dist * speed;
				this.motionY += dy / dist * speed;
				this.motionZ += dz / dist * speed;
			} else {
				this.waypointX = this.posX;
				this.waypointY = this.posY;
				this.waypointZ = this.posZ;
			}
		}
	}

	private boolean isCourseTraversable(double x, double y, double z, double step, boolean canPassSolidBlocks) {
		double dx = (this.waypointX - this.posX) / step;
		double dy = (this.waypointY - this.posY) / step;
		double dz = (this.waypointZ - this.posZ) / step;
		MutableBlockPos checkPos = new MutableBlockPos();
		for (int i = 1; i < step; ++i) {
			checkPos.setPos(this.posX + dx * step, this.posY + dy * step, this.posZ + dz * step);
			if(this.worldObj.isBlockLoaded(checkPos)) {
				IBlockState state = this.worldObj.getBlockState(checkPos);
				if ((!canPassSolidBlocks && state.isOpaqueCube()) || state.getMaterial().isLiquid()) {
					return false;
				}
			} else {
				return false;
			}
		}
		return true;
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
		} else {
			if (this.isInWater()) {
				this.motionY += 0.01D;
				this.waypointX = this.posX;
				this.waypointY = this.posY + 0.1D;
				this.waypointZ = this.posZ;
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
