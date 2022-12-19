package thebetweenlands.common.entity.mobs;


import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.capability.IDecayCapability;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.common.herblore.elixir.ElixirEffectRegistry;
import thebetweenlands.common.registries.CapabilityRegistry;

public class EntitySludgeJet extends Entity {

	public EntitySludgeJet(World world) {
		super(world);
		setSize(1F, 2.5F);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (!getEntityWorld().isRemote) {
			if (ticksExisted > 20)
				setDead();
		} else {
			if(this.ticksExisted == 1) {
				this.spawnSludgeJetParticles(0.0f);
				this.spawnSludgeJetParticles(0.125f);
				this.spawnSludgeJetParticles(0.25f);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	private void spawnSludgeJetParticles(float yOffset) {
		for (double yy = this.posY + yOffset; yy < this.posY + yOffset + 2.5D; yy += 0.5D) {
			double d0 = this.posX - 0.075F;
			double d1 = yy;
			double d2 = this.posZ - 0.075F;
			double d3 = this.posX + 0.075F;
			double d4 = this.posZ + 0.075F;
			double d5 = this.posX;
			double d6 = yy + 0.25F;
			double d7 = this.posZ + yOffset;
			double d8 = this.posY;

			BLParticles.TAR_BEAST_DRIP.spawn(world, d0, d1, d4, ParticleArgs.get().withMotion(10f * (yy - d8) * (rand.nextFloat() - 0.5f), 4f * (yy - d8), 10f * (yy - d8) * (rand.nextFloat() - 0.5f)).withScale(2.5F).withData(100)).setRBGColorF(0.4118F, 0.2745F, 0.1568F);
			BLParticles.TAR_BEAST_DRIP.spawn(world, d3, d1, d2, ParticleArgs.get().withMotion(10f * (yy - d8) * (rand.nextFloat() - 0.5f), 4f * (yy - d8), 10f * (yy - d8) * (rand.nextFloat() - 0.5f)).withScale(2.5F).withData(100)).setRBGColorF(0.4118F, 0.2745F, 0.1568F);
			BLParticles.TAR_BEAST_DRIP.spawn(world, d3, d1, d4, ParticleArgs.get().withMotion(10f * (yy - d8) * (rand.nextFloat() - 0.5f), 4f * (yy - d8), 10f * (yy - d8) * (rand.nextFloat() - 0.5f)).withScale(2.5F).withData(100)).setRBGColorF(0.4118F, 0.2745F, 0.1568F);
			BLParticles.TAR_BEAST_DRIP.spawn(world, d0, d1, d2, ParticleArgs.get().withMotion(10f * (yy - d8) * (rand.nextFloat() - 0.5f), 4f * (yy - d8), 10f * (yy - d8) * (rand.nextFloat() - 0.5f)).withScale(2.5F).withData(100)).setRBGColorF(0.4118F, 0.2745F, 0.1568F);
			BLParticles.TAR_BEAST_DRIP.spawn(world, d5, d6, d7, ParticleArgs.get().withMotion(10f * (yy - d8) * (rand.nextFloat() - 0.5f), 4f * (yy - d8), 10f * (yy - d8) * (rand.nextFloat() - 0.5f)).withScale(2.5F).withData(100)).setRBGColorF(0.4118F, 0.2745F, 0.1568F);
			BLParticles.TAR_BEAST_DRIP.spawn(world, d0, d1, d2, ParticleArgs.get().withMotion(10f * (yy - d8) * (rand.nextFloat() - 0.5f), 4f * (yy - d8), 10f * (yy - d8) * (rand.nextFloat() - 0.5f)).withScale(2.5F).withData(100)).setRBGColorF(0.4118F, 0.2745F, 0.1568F);
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

		if (player.getEntityBoundingBox().maxY >= getEntityBoundingBox().minY && player.getEntityBoundingBox().minY <= getEntityBoundingBox().maxY)
			if (player.getEntityBoundingBox().maxX >= getEntityBoundingBox().minX && player.getEntityBoundingBox().minX <= getEntityBoundingBox().maxX)
				if (player.getEntityBoundingBox().maxZ >= getEntityBoundingBox().minZ && player.getEntityBoundingBox().minZ <= getEntityBoundingBox().maxZ) {
					if (!getEntityWorld().isRemote) {
						if (player.isPotionActive(ElixirEffectRegistry.EFFECT_DECAY.getPotionEffect()))
							player.addPotionEffect(ElixirEffectRegistry.EFFECT_DECAY.createEffect(60, 3));
						IDecayCapability cap = player.getCapability(CapabilityRegistry.CAPABILITY_DECAY, null);
						if (cap != null)
							cap.getDecayStats().addDecayAcceleration(0.1F);

					}
					if (getEntityWorld().isRemote)
						player.addVelocity(0, 0.2D, 0);
				}
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