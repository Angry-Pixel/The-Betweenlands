package thebetweenlands.common.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.BatchedParticleRenderer;
import thebetweenlands.client.render.particle.DefaultParticleBatches;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;

public class EntityFishVortex extends Entity {
	public int vortexRotation = 0;
	public EntityFishVortex(World world) {
		super(world);
		setSize(0F, 0F);
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
		super.onUpdate();
		if (!getEntityWorld().isRemote) {
			if (!isBeingRidden() || ticksExisted >= 80)// || !isInWater());
				setDead();
		}

		if (getEntityWorld().isRemote) {
			this.spawnFishParticles();
		}

		posY += 0.05D;
	}
	
	@SideOnly(Side.CLIENT)
	protected void spawnFishParticles() {
		if(this.rand.nextInt(6) == 0) {
			ParticleArgs<?> args = ParticleArgs.get().withDataBuilder().setData(2, this).buildData();
				//args.withColor(1F, 0.65F, 0.25F, 0.75F);
				args.withScale(1.5F + rand.nextFloat() * 1.5F);
			BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.TRANSLUCENT_NEAREST_NEIGHBOR, BLParticles.FISH_VORTEX.create(this.world, this.posX + rand.nextFloat() * 4F, this.posY, this.posZ + rand.nextFloat() * 4F, args));
		}
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
	}

}
