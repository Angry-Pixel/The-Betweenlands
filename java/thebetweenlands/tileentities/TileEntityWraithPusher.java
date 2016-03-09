package thebetweenlands.tileentities;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import thebetweenlands.client.particle.BLParticle;
import thebetweenlands.utils.AnimationMathHelper;

public class TileEntityWraithPusher extends TileEntity {

	public int animationTicks;
	public boolean active;
	AnimationMathHelper headShake = new AnimationMathHelper();
	public float moveProgress;
	@Override
    public boolean canUpdate() {
        return true;
    }

	@Override
	public void updateEntity() {
		if (!worldObj.isRemote) {
			findEnemyToAttack();
			if (active) {
				activateBlock();
				if (animationTicks == 0)
					worldObj.playSoundEffect(xCoord, yCoord, zCoord, "mob.ghast.scream", 0.25F, 1.25F);
				if (animationTicks <= 24)
					animationTicks++;
				if (animationTicks == 24)
					setActive(false);
			}
			if (!active)
				if (animationTicks >= 1)
					animationTicks--;
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
		moveProgress = 1 + headShake.swing(4, 1F, false);
		if (worldObj.isRemote)
			if(!active && animationTicks %8 > 0)
				spawnParticles();
	}

	private void spawnParticles() {
		int meta = getBlockMetadata();
		float x = 0, z = 0;
		if(meta == 4)
			x = -1F;
		if(meta == 5)
			x = 1F;
		if(meta == 2)
			z = -1F;
		if(meta == 3)
			z = 1F;

		float xx = (float) xCoord  + 0.5F + x;
		float yy = (float) yCoord + 0.5F;
		float zz = (float) zCoord + 0.5F + z;
		float randomOffset = worldObj.rand.nextFloat() * 0.6F - 0.3F;
		BLParticle.SMOKE.spawn(worldObj, (double) (xx - randomOffset), (double) (yy + randomOffset), (double) (zz + randomOffset), 0.0D, 0.0D, 0.0D, 0);
		BLParticle.SMOKE.spawn(worldObj, (double) (xx + randomOffset), (double) (yy - randomOffset), (double) (zz + randomOffset), 0.0D, 0.0D, 0.0D, 0);
		BLParticle.SMOKE.spawn(worldObj, (double) (xx + randomOffset), (double) (yy + randomOffset), (double) (zz - randomOffset), 0.0D, 0.0D, 0.0D, 0);
		BLParticle.SMOKE.spawn(worldObj, (double) (xx + randomOffset), (double) (yy - randomOffset), (double) (zz + randomOffset), 0.0D, 0.0D, 0.0D, 0);
	}

	public void setActive(boolean isActive) {
		active = isActive;
	}

	@SuppressWarnings("unchecked")
	protected Entity findEnemyToAttack() {
		List<EntityLivingBase> list = worldObj.getEntitiesWithinAABB(EntityLivingBase.class, AxisAlignedBB.getBoundingBox(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D, xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D).expand(2D, 1D, 2D));
			for (int i = 0; i < list.size(); i++) {
				Entity entity = list.get(i);
				if (entity != null)
					if (entity instanceof EntityPlayer)
						if (!active && animationTicks == 0)
							setActive(true);
			}
		return null;
	}

	@SuppressWarnings("unchecked")
	protected Entity activateBlock() {
		int meta = getBlockMetadata();
		float x = 0, z = 0;
		if(meta == 4)
			x = -1F/16 * animationTicks;
		if(meta == 5)
			x = 1F/16 * animationTicks;
		if(meta == 2)
			z = -1F/16 * animationTicks;
		if(meta == 3)
			z = 1F/16 * animationTicks;
		List<EntityLivingBase> list = worldObj.getEntitiesWithinAABB(EntityLivingBase.class, AxisAlignedBB.getBoundingBox(xCoord + x, yCoord, zCoord + z, xCoord + 1D + x, yCoord + 1D, zCoord + 1D + z));
		if (animationTicks >= 1)
			for (int i = 0; i < list.size(); i++) {
				Entity entity = list.get(i);
				if (entity != null)
					if (entity instanceof EntityPlayer)
						((EntityLivingBase) entity).attackEntityFrom(DamageSource.generic, 2);
			}
		return null;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setInteger("animationTicks", animationTicks);
		nbt.setBoolean("active", active);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		animationTicks = nbt.getInteger("animationTicks");
		active = nbt.getBoolean("active");
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("animationTicks", animationTicks);
		nbt.setBoolean("active", active);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
		animationTicks = packet.func_148857_g().getInteger("animationTicks");
		active = packet.func_148857_g().getBoolean("active");
	}
}