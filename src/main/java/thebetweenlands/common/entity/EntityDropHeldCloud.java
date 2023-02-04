package thebetweenlands.common.entity;


import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.BatchedParticleRenderer;
import thebetweenlands.client.render.particle.DefaultParticleBatches;
import thebetweenlands.client.render.particle.ParticleFactory;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.client.render.particle.entity.ParticleGasCloud;
import thebetweenlands.common.registries.ItemRegistry;

public class EntityDropHeldCloud extends Entity {
	
	private static final DataParameter<Float> AOE_SIZE_XZ = EntityDataManager.createKey(EntityDropHeldCloud.class, DataSerializers.FLOAT);
	private static final DataParameter<Float> AOE_SIZE_Y = EntityDataManager.createKey(EntityDropHeldCloud.class, DataSerializers.FLOAT);

	public EntityDropHeldCloud(World world) {
		super(world);
		setSize(2F, 0.5F);
	}

	@Override
	protected void entityInit() {
		dataManager.register(AOE_SIZE_XZ, 2F);
		dataManager.register(AOE_SIZE_Y, 0.5F);
	}

	@Override
	public void onUpdate() {
		// super.onUpdate();

		if (!getEntityWorld().isRemote) {
			if (getEntityWorld().getTotalWorldTime() % 5 == 0)
				checkAreaOfEffect();
			if (getAOESizeXZ() > 0.5F)
				setAOESizeXZ(getAOESizeXZ() - 0.005F);
			if (getAOESizeXZ() <= 0.5F)
				setDead();
		}

		setBoundingBoxSize();

		if (getEntityWorld().isRemote)
			spawnCloudParticle();
	}

	@Nullable
	protected void checkAreaOfEffect() {
		Entity entity = null;
		if (!getEntityWorld().isRemote && getEntityWorld().getDifficulty() != EnumDifficulty.PEACEFUL) {
			List<EntityPlayer> list = getEntityWorld().getEntitiesWithinAABB(EntityPlayer.class, getEntityBoundingBox());
			for (int entityCount = 0; entityCount < list.size(); entityCount++) {
				entity = list.get(entityCount);
				if (entity != null)
					if (entity instanceof EntityPlayer) {
						EntityPlayer player = (EntityPlayer) entity;
						if(!isWearingSilkMask(player)) {
							if(!player.isSpectator() && !player.isCreative()) {
								ItemStack stack = player.getHeldItemMainhand();
								if (!stack.isEmpty())
									player.dropItem(true);
							}
						}
					}
				}
			}
	}

    public boolean isWearingSilkMask(EntityLivingBase entity) {
    	if(entity instanceof EntityPlayer) {
        	ItemStack helmet = ((EntityPlayer)entity).getItemStackFromSlot(EntityEquipmentSlot.HEAD);
        	if(!helmet.isEmpty() && helmet.getItem() == ItemRegistry.SILK_MASK) {
        		return true;
        	}
        }
    	return false;
    }

	@Override
    public void notifyDataManagerChange(DataParameter<?> key) {
        if (AOE_SIZE_XZ.equals(key)) 
            setAOESizeXZ(getAOESizeXZ());
        if (AOE_SIZE_Y.equals(key)) 
            setAOESizeY(getAOESizeY());
        super.notifyDataManagerChange(key);
    }

	protected void setBoundingBoxSize() {
		AxisAlignedBB axisalignedbb = new AxisAlignedBB(posX - getAOESizeXZ() * 0.5D, posY, posZ - getAOESizeXZ() * 0.5D, posX + getAOESizeXZ() * 0.5D, posY + getAOESizeY(), posZ + getAOESizeXZ() * 0.5D);
		setEntityBoundingBox(axisalignedbb);
	}

	private void setAOESizeXZ(float aoeSizeXZ) {
		dataManager.set(AOE_SIZE_XZ, aoeSizeXZ);
	}

	public float getAOESizeXZ() {
		return dataManager.get(AOE_SIZE_XZ);
	}

	private void setAOESizeY(float aoeSizeY) {
		dataManager.set(AOE_SIZE_Y, aoeSizeY);
	}

	public float getAOESizeY() {
		return dataManager.get(AOE_SIZE_Y);
	}

	@Override
    public boolean canBePushed() {
        return false;
    }

	@Override
    public boolean canBeCollidedWith() {
        return false;
    }

	@Override
	public void addVelocity(double x, double y, double z) {
		motionX = 0;
		motionY += y;
		motionZ = 0;
	}

	@Override
	public boolean getIsInvulnerable() {
		return true;
	}

	@Override
	public void onKillCommand() {
		this.setDead();
	}
	
	@SideOnly(Side.CLIENT)
	private void spawnCloudParticle() {
		double x = this.posX + (this.world.rand.nextFloat() - 0.5F) / 2.0F;
		double y = this.posY + 0.1D;
		double z = this.posZ + (this.world.rand.nextFloat() - 0.5F) / 2.0F;
		double mx = (this.world.rand.nextFloat() - 0.5F) / 16.0F;
		double my = (this.world.rand.nextFloat() - 0.5F) / 16.0F * 0.1F;
		double mz = (this.world.rand.nextFloat() - 0.5F) / 16.0F;
		int[] color = {100, 100, 0, 255};

		ParticleGasCloud hazeParticle = (ParticleGasCloud) BLParticles.GAS_CLOUD
				.create(this.world, x, y, z, ParticleFactory.ParticleArgs.get()
						.withData(null)
						.withMotion(mx, my, mz)
						.withColor(color[0] / 255.0F, color[1] / 255.0F, color[2] / 255.0F, color[3] / 255.0F)
						.withScale(8f));
		
		BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.GAS_CLOUDS_HEAT_HAZE, hazeParticle);
		
		ParticleGasCloud particle = (ParticleGasCloud) BLParticles.GAS_CLOUD
				.create(this.world, x, y, z, ParticleFactory.ParticleArgs.get()
						.withData(null)
						.withMotion(mx, my, mz)
						.withColor(color[0] / 255.0F, color[1] / 255.0F, color[2] / 255.0F, color[3] / 255.0F)
						.withScale(4f));

		BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.GAS_CLOUDS_TEXTURED, particle);
		
			double d0 = x - 0.0625F;
			double d1 = y;
			double d2 = z - 0.0625F;
			double d3 = x + 0.0625F;
			double d4 = z + 0.0625F;
			double d5 = x;
			double d6 = y + 0.25F;
			double d7 = z;
			BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.TRANSLUCENT_GLOWING_NEAREST_NEIGHBOR, BLParticles.PUZZLE_BEAM.create(world, d0, d1, d2, ParticleArgs.get().withColor(105F, 70F, 40F, 1F).withScale(1.5F).withMotion(mx, my, mz).withData(100)));
			BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.TRANSLUCENT_GLOWING_NEAREST_NEIGHBOR, BLParticles.PUZZLE_BEAM.create(world, d0, d1, d4, ParticleArgs.get().withColor(105F, 70F, 40F, 1F).withScale(1.5F).withMotion(mx, my, mz).withData(100)));
			BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.TRANSLUCENT_GLOWING_NEAREST_NEIGHBOR, BLParticles.PUZZLE_BEAM.create(world, d3, d1, d2, ParticleArgs.get().withColor(105F, 70F, 40F, 1F).withScale(1.5F).withMotion(mx, my, mz).withData(100)));
			BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.TRANSLUCENT_GLOWING_NEAREST_NEIGHBOR, BLParticles.PUZZLE_BEAM.create(world, d3, d1, d4, ParticleArgs.get().withColor(105F, 70F, 40F, 1F).withScale(1.5F).withMotion(mx, my, mz).withData(100)));
			BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.TRANSLUCENT_GLOWING_NEAREST_NEIGHBOR, BLParticles.PUZZLE_BEAM.create(world, d0, d1, d2, ParticleArgs.get().withColor(105F, 70F, 40F, 1F).withScale(1.5F).withMotion(mx, my, mz).withData(100)));
			BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.TRANSLUCENT_GLOWING_NEAREST_NEIGHBOR, BLParticles.PUZZLE_BEAM.create(world, d5, d6, d7, ParticleArgs.get().withColor(105F, 70F, 40F, 1F).withScale(1.5F).withMotion(mx, my, mz).withData(100)));
			BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.TRANSLUCENT_GLOWING_NEAREST_NEIGHBOR, BLParticles.PUZZLE_BEAM.create(world, d0, d1, d2, ParticleArgs.get().withColor(105F, 70F, 40F, 1F).withScale(1.5F).withMotion(mx, my, mz).withData(100)));

	}

	@Override
	public boolean shouldRenderInPass(int pass) {
		return pass == 1;
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
		// TODO Auto-generated method stub
		
	}
}