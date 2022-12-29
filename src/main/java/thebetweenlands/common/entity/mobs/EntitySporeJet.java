package thebetweenlands.common.entity.mobs;


import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.BatchedParticleRenderer;
import thebetweenlands.client.render.particle.DefaultParticleBatches;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.common.registries.ItemRegistry;

public class EntitySporeJet extends Entity {

	public EntitySporeJet(World world) {
		super(world);
		setSize(1.6F, 3.0F);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (!getEntityWorld().isRemote) {
			if (ticksExisted > 15)
				setDead();
		} else {
			if(this.ticksExisted == 1) {
				this.spawnSporeJetParticles();
			}
		}
	}

	@SideOnly(Side.CLIENT)
	private void spawnSporeJetParticles() {
		for (double yy = this.posY + 0.625F; yy < this.posY + 0.625F + 2D; yy += 0.5D) {
			double d0 = this.posX - 0.075F;
			double d1 = yy;
			double d2 = this.posZ - 0.075F;
			double d3 = this.posX + 0.075F;
			double d4 = this.posZ + 0.075F;
			double d5 = this.posX;
			double d6 = yy + 0.25F;
			double d7 = this.posZ;
			BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.TRANSLUCENT_GLOWING_NEAREST_NEIGHBOR, BLParticles.PUZZLE_BEAM.create(world, d0, d1, d2, ParticleArgs.get().withMotion(0.0125f * (rand.nextFloat() - 0.5f), 0.0125f * (rand.nextFloat() - 0.5f), 0.0125f * (rand.nextFloat() - 0.5f)).withColor(105F, 70F, 40F, 1F).withScale(1.5F).withData(100)));
			BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.TRANSLUCENT_GLOWING_NEAREST_NEIGHBOR, BLParticles.PUZZLE_BEAM.create(world, d0, d1, d4, ParticleArgs.get().withMotion(0.0125f * (rand.nextFloat() - 0.5f), 0.0125f * (rand.nextFloat() - 0.5f), 0.0125f * (rand.nextFloat() - 0.5f)).withColor(105F, 70F, 40F, 1F).withScale(1.5F).withData(100)));
			BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.TRANSLUCENT_GLOWING_NEAREST_NEIGHBOR, BLParticles.PUZZLE_BEAM.create(world, d3, d1, d2, ParticleArgs.get().withMotion(0.0125f * (rand.nextFloat() - 0.5f), 0.0125f * (rand.nextFloat() - 0.5f), 0.0125f * (rand.nextFloat() - 0.5f)).withColor(105F, 70F, 40F, 1F).withScale(1.5F).withData(100)));
			BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.TRANSLUCENT_GLOWING_NEAREST_NEIGHBOR, BLParticles.PUZZLE_BEAM.create(world, d3, d1, d4, ParticleArgs.get().withMotion(0.0125f * (rand.nextFloat() - 0.5f), 0.0125f * (rand.nextFloat() - 0.5f), 0.0125f * (rand.nextFloat() - 0.5f)).withColor(105F, 70F, 40F, 1F).withScale(1.5F).withData(100)));
			BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.TRANSLUCENT_GLOWING_NEAREST_NEIGHBOR, BLParticles.PUZZLE_BEAM.create(world, d0, d1, d2, ParticleArgs.get().withMotion(0.0125f * (rand.nextFloat() - 0.5f), 0.0125f * (rand.nextFloat() - 0.5f), 0.0125f * (rand.nextFloat() - 0.5f)).withColor(105F, 70F, 40F, 1F).withScale(1.5F).withData(100)));
			BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.TRANSLUCENT_GLOWING_NEAREST_NEIGHBOR, BLParticles.PUZZLE_BEAM.create(world, d5, d6, d7, ParticleArgs.get().withMotion(0.0125f * (rand.nextFloat() - 0.5f), 0.0125f * (rand.nextFloat() - 0.5f), 0.0125f * (rand.nextFloat() - 0.5f)).withColor(105F, 70F, 40F, 1F).withScale(1.5F).withData(100)));
			BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.TRANSLUCENT_GLOWING_NEAREST_NEIGHBOR, BLParticles.PUZZLE_BEAM.create(world, d0, d1, d2, ParticleArgs.get().withMotion(0.0125f * (rand.nextFloat() - 0.5f), 0.0125f * (rand.nextFloat() - 0.5f), 0.0125f * (rand.nextFloat() - 0.5f)).withColor(105F, 70F, 40F, 1F).withScale(1.5F).withData(100)));
		}
	}
	
	@Override
	public boolean getIsInvulnerable() {
		return true;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float damage) {
		return false;
	}

	@Override
	public void onCollideWithPlayer(EntityPlayer player) {
		if (!getEntityWorld().isRemote) {
			if(!isWearingSilkMask(player)) {
				if (player.getEntityBoundingBox().maxY >= getEntityBoundingBox().minY && player.getEntityBoundingBox().minY <= getEntityBoundingBox().maxY)
					if (player.getEntityBoundingBox().maxX >= getEntityBoundingBox().minX && player.getEntityBoundingBox().minX <= getEntityBoundingBox().maxX)
						if (player.getEntityBoundingBox().maxZ >= getEntityBoundingBox().minZ && player.getEntityBoundingBox().minZ <= getEntityBoundingBox().maxZ) {
							//((EntityLivingBase) player).addPotionEffect(new PotionEffect(MobEffects.POISON, 5 * 20, 0));
							ItemStack stack = player.getHeldItemMainhand();
							if (!stack.isEmpty())
								player.dropItem(true);
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
	protected void entityInit() {
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
	}

}