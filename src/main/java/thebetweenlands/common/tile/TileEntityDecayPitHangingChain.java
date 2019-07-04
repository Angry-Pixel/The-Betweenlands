package thebetweenlands.common.tile;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;

public class TileEntityDecayPitHangingChain extends TileEntity implements ITickable {

	public int animationTicksChain = 0;
	public int animationTicksChainPrev = 0;
	public int PROGRESS = 0;
	public final float MOVE_UNIT = 0.0078125F; // unit of movement 
	public boolean IS_MOVING = false;
	public boolean IS_SLOW = false;

	@Override
	public void update() {
		animationTicksChainPrev = animationTicksChain;

		if (isMoving()) {
			if (isSlow())
				animationTicksChain++;
			else
				animationTicksChain += 8;

		}

		if (getEntityCollidedWithChains(getHangingLengthCollision(1, 0, 2F + getProgress() * MOVE_UNIT)) != null)
			checkCollisions(getEntityCollidedWithChains(getHangingLengthCollision(1, 0, 2F + getProgress() * MOVE_UNIT)));

		if (getEntityCollidedWithChains(getHangingLengthCollision(-1, 0, 2F + getProgress() * MOVE_UNIT)) != null)
			checkCollisions(getEntityCollidedWithChains(getHangingLengthCollision(-1, 0, 2F + getProgress() * MOVE_UNIT)));

		if (getEntityCollidedWithChains(getHangingLengthCollision(0, 1, 2F + getProgress() * MOVE_UNIT)) != null)
			checkCollisions(getEntityCollidedWithChains(getHangingLengthCollision(0, 1, 2F + getProgress() * MOVE_UNIT)));

		if (getEntityCollidedWithChains(getHangingLengthCollision(0, -1, 2F + getProgress() * MOVE_UNIT)) != null)
			checkCollisions(getEntityCollidedWithChains(getHangingLengthCollision(0, -1, 2F + getProgress() * MOVE_UNIT)));

		if (animationTicksChainPrev >= 128) {
			animationTicksChain = animationTicksChainPrev = 0;
			setMoving(false);
		}

	}

	public List<Entity> getEntityCollidedWithChains(AxisAlignedBB chainBox) {
		return getWorld().<Entity>getEntitiesWithinAABB(Entity.class, chainBox);
    }

	private void checkCollisions(List<Entity> list) {
		for (Entity entity : list) {
			if (entity instanceof EntityArrow) { // just arrows for now
				Entity arrow = ((EntityArrow) entity);
				arrow.setPositionAndUpdate(arrow.prevPosX, arrow.prevPosY, arrow.prevPosZ);
				arrow.motionX *= -0.10000000149011612D;
				arrow.motionY *= -0.10000000149011612D;
				arrow.motionZ *= -0.10000000149011612D;
				arrow.rotationYaw += 180.0F;
				arrow.prevRotationYaw += 180.0F;
				getWorld().playSound((EntityPlayer) null, arrow.posX, arrow.posY, arrow.posZ, SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.BLOCKS, 0.5F, 3F);
				// this.ticksInAir = 0;
			}
		}
	}

	public AxisAlignedBB getHangingLengthCollision(double offX, double offZ, float extended) {
		return new AxisAlignedBB( getPos().getX() + offX + 0.1875D, getPos().getY() - extended,  getPos().getZ() + offZ +0.1875D, getPos().getX() + offX + 0.8125D, getPos().getY(), getPos().getZ() + offZ +0.8125D);
	}

	public void setProgress(int progress) {
		PROGRESS = progress;
	}

	public int getProgress() {
		return PROGRESS;
	}

	public void setMoving(boolean moving) {
		IS_MOVING = moving;
	}

	public boolean isMoving() {
		return IS_MOVING;
	}

	public void setSlow(boolean slow) {
		IS_SLOW = slow;
	}

	public boolean isSlow() {
		return IS_SLOW;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setInteger("animationTicksChain", animationTicksChain);
		nbt.setInteger("animationTicksChainPrev", animationTicksChainPrev);
		nbt.setInteger("progress", PROGRESS);
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		animationTicksChain = nbt.getInteger("animationTicksChain");
		animationTicksChainPrev = nbt.getInteger("animationTicksChainPrev");
		PROGRESS = nbt.getInteger("progress");
	}

	@Override
    public NBTTagCompound getUpdateTag() {
		NBTTagCompound nbt = new NBTTagCompound();
        return writeToNBT(nbt);
    }

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		writeToNBT(nbt);
		return new SPacketUpdateTileEntity(getPos(), 0, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
		readFromNBT(packet.getNbtCompound());
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return INFINITE_EXTENT_AABB;
	}
}
