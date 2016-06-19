package thebetweenlands.entities.mobs;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import thebetweenlands.entities.particles.EntityGasCloudFX;

public class EntityGasCloud extends EntityFlying implements IMob, IEntityBL {
	public List<Object> gasParticles = new ArrayList<Object>();

	private int courseChangeCooldown;
	private double waypointX;
	private double waypointY;
	private double waypointZ;

	private double aboveLayer = 6.0D;

	public static final int GAS_CLOUD_COLOR_DW = 20;

	public EntityGasCloud(World world) {
		super(world);
		this.setSize(1.75F, 1.75F);
		this.noClip = true;
		this.ignoreFrustumCheck = true;
	}

	public void setGasColor(int color) {
		this.dataWatcher.updateObject(GAS_CLOUD_COLOR_DW, color);
	}

	public int getGasColor() {
		return this.dataWatcher.getWatchableObjectInt(GAS_CLOUD_COLOR_DW);
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(30.0D);
		getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(1.0D);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataWatcher.addObject(GAS_CLOUD_COLOR_DW, (int) 0xAA68C4B3);
	}

	@Override
	protected void updateEntityActionState() {
		if (!this.worldObj.isRemote && this.worldObj.difficultySetting == EnumDifficulty.PEACEFUL) {
			this.setDead();
		}

		super.updateEntityActionState();

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

		EntityPlayer closestTarget = this.worldObj.getClosestVulnerablePlayerToEntity(this, 16.0D);
		if(closestTarget != null) {
			this.waypointX = closestTarget.posX;
			this.waypointY = closestTarget.posY;
			this.waypointZ = closestTarget.posZ;
			speed = 0.05F;
		}

		if (this.courseChangeCooldown-- <= 0) {
			this.courseChangeCooldown += this.rand.nextInt(5) + 2;
			dist = MathHelper.sqrt_double(dist);

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

		for (int i = 1; i < step; ++i) {
			Block block = this.worldObj.getBlock(MathHelper.floor_double(this.posX + dx * step), MathHelper.floor_double(this.posY + dy * step), MathHelper.floor_double(this.posZ + dz * step));
			if ((!canPassSolidBlocks && block.isOpaqueCube()) || block.getMaterial().isLiquid()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public boolean getCanSpawnHere() {
		return super.getCanSpawnHere() && this.worldObj.difficultySetting != EnumDifficulty.PEACEFUL;
	}

	@Override
	public int getMaxSpawnedInChunk() {
		return 1;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if (this.worldObj.isRemote) {
			EntityGasCloudFX newGasCloud = new EntityGasCloudFX(
					this.worldObj, 
					this.posX + this.motionX + (this.worldObj.rand.nextFloat() - 0.5F) / 2.0F, 
					this.posY + this.height / 2.0D + this.motionY + (this.worldObj.rand.nextFloat() - 0.5F) / 2.0F, 
					this.posZ + this.motionZ + (this.worldObj.rand.nextFloat() - 0.5F) / 2.0F,
					this.motionX + (this.worldObj.rand.nextFloat() - 0.5F) / 16.0F,
					this.motionY + (this.worldObj.rand.nextFloat() - 0.5F) / 16.0F,
					this.motionZ + (this.worldObj.rand.nextFloat() - 0.5F) / 16.0F,
					this.getGasColor());
			this.gasParticles.add(newGasCloud);

			for(int i = 0; i < this.gasParticles.size(); i++) {
				EntityGasCloudFX gasCloud = (EntityGasCloudFX) this.gasParticles.get(i);
				gasCloud.onUpdate();
				if(gasCloud.isDead) {
					this.gasParticles.remove(i);
				}
			}
		} else {
			if(this.isInWater()) {
				this.motionY += 0.01D;
				this.waypointX = this.posX;
				this.waypointY = this.posY + 0.1D;
				this.waypointZ = this.posZ;
			}
		}

		if(!this.worldObj.isRemote && this.isEntityAlive()) {
			List<EntityLivingBase> targets = this.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, this.boundingBox.expand(0.5D, 0.5D, 0.5D));
			for(EntityLivingBase target : targets) {
				if(target instanceof EntityGasCloud == false && target instanceof IEntityBL == false) {
					target.addPotionEffect(new PotionEffect(Potion.poison.getId(), 60, 0));
					if(target.ticksExisted % 10 == 0)
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
	public String pageName() {
		return "gasCloud";
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float damage) {
		if(source == DamageSource.inWall) {
			return false;
		}
		return super.attackEntityFrom(source, damage);
	}
}
