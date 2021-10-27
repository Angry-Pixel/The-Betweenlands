package thebetweenlands.common.entity;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeColorHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.client.render.particle.entity.ParticleEntitySwirl;
import thebetweenlands.client.render.particle.entity.ParticleFancyDrip;

public class EntityFishVortex extends Entity {
	public EntityFishVortex(World world) {
		super(world);
		setSize(0.1F, 0.1F);
	}

	@Override
	protected void entityInit() {	
	}

	@Override
	public void updatePassenger(Entity entity) {
		super.updatePassenger(entity);
		if (entity instanceof EntityLivingBase) {
			entity.setPositionAndRotation(posX, posY, posZ, entity.rotationYaw, entity.rotationPitch);
			((EntityLivingBase) entity).renderYawOffset = ((EntityLivingBase) entity).rotationYaw -= 12;
		}
	}

	@Override
	public void onUpdate() {
		boolean wasInWater = this.isInWater();
		
		super.onUpdate();

		if(!this.world.isRemote) {
			if(!this.isBeingRidden() || this.ticksExisted >= 80 || (wasInWater && !this.isInWater())) {
				this.setDead();
			}
		} else {
			this.spawnParticles();
		}

		if(isBeingRidden()) {
			Entity rider = getPassengers().get(0);
			AxisAlignedBB box = rider.getEntityBoundingBox().grow(0D, 0.1D, 0D);

			if(this.world.getCollisionBoxes(rider, box).isEmpty()) {
				this.motionX = 0;
				this.motionY = 0.05D;
				this.motionZ = 0;
			} else {
				this.motionX = 0;
				this.motionY = 0;
				this.motionZ = 0;
			}

			this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
		}
	}

	@SideOnly(Side.CLIENT)
	protected void spawnParticles() {
		if(this.isInWater()) {
			if(this.rand.nextInt(3) != 0) {
				ParticleArgs<?> args = ParticleArgs.get().withData(400, this.rand.nextFloat(), this);
				args.withScale((1.5F + rand.nextFloat() * 1.5F) * 0.5f);
				ParticleEntitySwirl particle = (ParticleEntitySwirl) BLParticles.FISH_VORTEX.spawn(this.world, this.posX + rand.nextFloat() * 4F, this.posY, this.posZ + rand.nextFloat() * 4F, args);
				particle.setRotationSpeed(1.5f + this.rand.nextFloat() * 8.0f);
			}
		} else if(this.ticksExisted < 70) {
			List<AxisAlignedBB> aabbs = this.world.getCollisionBoxes(null, this.getEntityBoundingBox().expand(0, -8, 0));
			
			if(!aabbs.isEmpty()) {
				double surfaceY = 0;
				for(AxisAlignedBB aabb : aabbs) {
					surfaceY = Math.max(aabb.maxY, surfaceY);
				}
				
				double radius = 0.25D;
				double ox = (rand.nextFloat() - 0.5f) * radius * 0.5f;
				double oz = (rand.nextFloat() - 0.5f) * radius * 0.5f;
				double velX = ox * rand.nextFloat() * 2.0f;
				double velY = 0.5f + this.ticksExisted / 160.0f;
				double velZ = oz * rand.nextFloat() * 2.0f;
				
				int waterColor = BiomeColorHelper.getWaterColorAtPos(this.world, new BlockPos(this));

				float r = (waterColor >> 16 & 255) / 255.0f * 0.5f;
				float g = (waterColor >> 8 & 255) / 255.0f * 0.5f;
				float b = (waterColor & 255) / 255.0f * 1.0f;
				
				for(int i = 0; i < 3; i++) {
					((ParticleFancyDrip) BLParticles.FANCY_DRIP.spawn(getEntityWorld(), this.posX + ox, surfaceY, this.posZ + oz, ParticleArgs.get().withMotion(velX, velY, velZ).withScale(1.20f).withColor(r, g, b, 1.0f))).setSpawnRipples(false).setSpawnSplashes(false);
					((ParticleFancyDrip) BLParticles.FANCY_DRIP.spawn(getEntityWorld(), this.posX + ox, surfaceY, this.posZ + oz, ParticleArgs.get().withMotion(velX * 0.05f, velY, velZ * 0.05f).withScale(0.9f + this.rand.nextFloat() * 0.8f).withColor(r, g, b, 1.0f))).setSpawnRipples(false).setSpawnSplashes(false);
				}
				
				if(this.world.rand.nextInt(10) == 0) {
					BLParticles.FANCY_BUBBLE.spawn(getEntityWorld(), posX + (this.world.rand.nextFloat() - 0.5f) * radius, surfaceY, posZ + (this.world.rand.nextFloat() - 0.5f) * radius, 
							ParticleArgs.get().withMotion((this.world.rand.nextFloat() - 0.5f) * 0.01f, 0.05f, (this.world.rand.nextFloat() - 0.5f) * 0.01f)
							.withScale(0.5f + this.world.rand.nextFloat() * 1.5f)
							.withData(true)
							.withColor(r * 0.9f, g * 0.9f, b * 0.9f, 0.9f));
				}
			}
		}
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
	}

}
