package thebetweenlands.common.entity.mobs;


import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.network.clientbound.PacketParticle;
import thebetweenlands.common.network.clientbound.PacketParticle.ParticleType;

public class EntitySporeJet extends EntityLiving {

	public EntitySporeJet(World world) {
		super(world);
		setSize(2F, 2.5F);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (!getEntityWorld().isRemote) {
			if (ticksExisted > 20)
				setDead();
			if (ticksExisted == 1)
				TheBetweenlands.networkWrapper.sendToAll(new PacketParticle(ParticleType.SPORE_JET, (float) posX, (float)posY +0.5F, (float)posZ, 0F));
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
	protected void collideWithEntity(Entity entity) {
		if (!getEntityWorld().isRemote) {
			if (entity instanceof EntityPlayer) {
				((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.POISON, 5 * 20, 0));
				EntityPlayer player = (EntityPlayer) entity;
				ItemStack is = player.getHeldItemMainhand();
				if (!is.isEmpty())
					player.dropItem(true);
			}
		}
		setDead();
		super.collideWithEntity(entity);
	}

}