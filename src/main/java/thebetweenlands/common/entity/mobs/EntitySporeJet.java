package thebetweenlands.common.entity.mobs;


import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.network.clientbound.PacketParticle;
import thebetweenlands.common.network.clientbound.PacketParticle.ParticleType;

public class EntitySporeJet extends Entity {

	public EntitySporeJet(World world) {
		super(world);
		setSize(2F, 2.5F);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (!getEntityWorld().isRemote) {
			if (ticksExisted > 40)
				setDead();
			if (ticksExisted == 1)
				TheBetweenlands.networkWrapper.sendToAll(new PacketParticle(ParticleType.SPORE_JET, (float) posX, (float)posY + 0.625F, (float)posZ, 0F));
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
						//((EntityLivingBase) player).addPotionEffect(new PotionEffect(MobEffects.POISON, 5 * 20, 0));
						ItemStack stack = player.getHeldItemMainhand();
						if (!stack.isEmpty())
							player.dropItem(true);
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