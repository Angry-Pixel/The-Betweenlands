package thebetweenlands.common.entity.mobs;


import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import thebetweenlands.api.capability.IDecayCapability;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.herblore.elixir.ElixirEffectRegistry;
import thebetweenlands.common.network.clientbound.PacketParticle;
import thebetweenlands.common.network.clientbound.PacketParticle.ParticleType;
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
			if (ticksExisted == 1) {
				//3 times the poop!
				TheBetweenlands.networkWrapper.sendToAll(new PacketParticle(ParticleType.SLUDGE_JET, (float) posX, (float)posY, (float)posZ, 0F));
				TheBetweenlands.networkWrapper.sendToAll(new PacketParticle(ParticleType.SLUDGE_JET, (float) posX, (float)posY + 0.125F, (float)posZ, 0F));
				TheBetweenlands.networkWrapper.sendToAll(new PacketParticle(ParticleType.SLUDGE_JET, (float) posX, (float)posY + 0.25F, (float)posZ, 0F));
			}
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
			if (player.getEntityBoundingBox().maxY >= getEntityBoundingBox().minY && player.getEntityBoundingBox().minY <= getEntityBoundingBox().maxY)
				if (player.getEntityBoundingBox().maxX >= getEntityBoundingBox().minX && player.getEntityBoundingBox().minX <= getEntityBoundingBox().maxX)
					if (player.getEntityBoundingBox().maxZ >= getEntityBoundingBox().minZ && player.getEntityBoundingBox().minZ <= getEntityBoundingBox().maxZ) {
						if (player.isPotionActive(ElixirEffectRegistry.EFFECT_DECAY.getPotionEffect()))
							player.addPotionEffect(ElixirEffectRegistry.EFFECT_DECAY.createEffect(60, 3));
						IDecayCapability cap = player.getCapability(CapabilityRegistry.CAPABILITY_DECAY, null);
						if (cap != null)
							cap.getDecayStats().addDecayAcceleration(0.1F);
					}
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