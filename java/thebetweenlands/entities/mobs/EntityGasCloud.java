package thebetweenlands.entities.mobs;

import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.IMob;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import thebetweenlands.client.particle.BLParticle;
import thebetweenlands.world.WorldProviderBetweenlands;

public class EntityGasCloud extends EntityFlying implements IMob, IEntityBL {

	public int courseChangeCooldown;
	public double waypointX;
	public double waypointY;
	public double waypointZ;

	public double aboveLayer = 6.0D;

	public EntityGasCloud(World world) {
		super(world);
		this.setSize(1F, 1F);
		this.noClip = true;
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(20.0D);
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
			if(this.posY > WorldProviderBetweenlands.LAYER_HEIGHT + this.aboveLayer) {
				this.waypointY = this.posY + (-this.rand.nextFloat() * 2.0F) * 16.0F;
			} else {
				float rndFloat = this.rand.nextFloat() * 2.0F - 1.0F;
				if(rndFloat > 0.0D) {
					rndFloat -= 0.5D;
					double maxRange = WorldProviderBetweenlands.LAYER_HEIGHT + this.aboveLayer - this.posY;
					this.waypointY = this.posY + (-this.rand.nextFloat() * 2.0F) * maxRange;
				} else {
					this.waypointY = this.posY + (this.rand.nextFloat() * 2.0F - 1.0F) * 16.0F;
				}
			}
			this.waypointZ = this.posZ + (this.rand.nextFloat() * 2.0F - 1.0F) * 16.0F;
		}

		if (this.courseChangeCooldown-- <= 0) {
			this.courseChangeCooldown += this.rand.nextInt(5) + 2;
			dist = MathHelper.sqrt_double(dist);

			if (this.isCourseTraversable(this.waypointX, this.waypointY, this.waypointZ, dist)) {
				this.motionX += dx / dist * 0.03D;
				this.motionY += dy / dist * 0.03D;
				if(this.posY > WorldProviderBetweenlands.LAYER_HEIGHT + this.aboveLayer) {
					this.motionY -= ((1.0D - (WorldProviderBetweenlands.LAYER_HEIGHT + this.aboveLayer - this.posY) / this.aboveLayer) + 1.0D) / 100.0D;
				}
				this.motionZ += dz / dist * 0.03D;
				if(this.posY > WorldProviderBetweenlands.LAYER_HEIGHT + this.aboveLayer) {
					this.waypointX = this.posX;
					this.waypointY = this.posY;
					this.waypointZ = this.posZ;
				}
			} else {
				this.waypointX = this.posX;
				this.waypointY = this.posY;
				this.waypointZ = this.posZ;
			}
		}
	}

	private boolean isCourseTraversable(double x, double y, double z, double step) {
		double d4 = (this.waypointX - this.posX) / step;
		double d5 = (this.waypointY - this.posY) / step;
		double d6 = (this.waypointZ - this.posZ) / step;
		AxisAlignedBB axisalignedbb = this.boundingBox.copy();

		for (int i = 1; i < step; ++i)
		{
			axisalignedbb.offset(d4, d5, d6);

			if (!this.worldObj.getCollidingBoundingBoxes(this, axisalignedbb).isEmpty())
			{
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
			BLParticle.GAS_CLOUD.spawn(this.worldObj, 
					this.posX + this.motionX + (this.worldObj.rand.nextFloat() - 0.5F) / 2.0F, 
					this.posY + this.motionY + (this.worldObj.rand.nextFloat() - 0.5F) / 2.0F, 
					this.posZ + this.motionZ + (this.worldObj.rand.nextFloat() - 0.5F) / 2.0F, 
					this.motionX + (this.worldObj.rand.nextFloat() - 0.5F) / 10.0F, 
					this.motionY + (this.worldObj.rand.nextFloat() - 0.5F) / 10.0F, 
					this.motionZ + (this.worldObj.rand.nextFloat() - 0.5F) / 10.0F, 
					//0, 0, 0,
					0, 0xAA107510);
		} else {
			if(this.isInWater()) {
				this.motionY += 0.01D;
				this.waypointX = this.posX;
				this.waypointY = this.posY + 0.1D;
				this.waypointZ = this.posZ;
			}
		}
	}

	@Override
	public void setDead() {
		super.setDead();
	}

	@Override
	public String pageName() {
		return "gasCloud";
	}

	@Override
	protected boolean canDespawn() {
		return false;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float damage) {
		if(source == DamageSource.inWall) {
			return false;
		}
		return super.attackEntityFrom(source, damage);
	}
}
