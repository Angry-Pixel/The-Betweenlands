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

public class TileEntityDecayPitGroundChain extends TileEntity implements ITickable {

	public int animationTicksChain = 0;
	public int animationTicksChainPrev = 0;
	public int LENGTH = 5; //hard-coded for now
	public boolean IS_MOVING = false;
	public boolean IS_SLOW = false;
	public boolean IS_RAISING = false;

	@Override
	public void update() {
		animationTicksChainPrev = animationTicksChain;
		if (isMoving()) {
			if(isSlow())
				animationTicksChain++;
			else
				animationTicksChain += 8;
		}

		if (getEntityCollidedWithChains(getHangingLengthCollision(0.625F, 5F, 0.625F)) != null)
			checkCollisions(getEntityCollidedWithChains(getHangingLengthCollision(0.625F, 5F, 0.625F)));

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

	public AxisAlignedBB getHangingLengthCollision(double x, double y, double z) {
		return new AxisAlignedBB(getPos().getX() + 0.5D - x * 0.5D, getPos().getY(), getPos().getZ() + 0.5D + - z * 0.5D, getPos().getX() + 0.5D + x * 0.5D, getPos().getY() + y, getPos().getZ() + 0.5D + z * 0.5D);
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

	public void setRaising(boolean raising) {
		IS_RAISING = raising;
	}

	public boolean isRaising() {
		return IS_RAISING;
	}

	public void setLength(int length) {
		LENGTH = length;
	}

	public int getLength() {
		return LENGTH;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setInteger("animationTicksChain", animationTicksChain);
		nbt.setInteger("animationTicksChainPrev", animationTicksChainPrev);
		nbt.setInteger("length", LENGTH);
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		animationTicksChain = nbt.getInteger("animationTicksChain");
		animationTicksChainPrev = nbt.getInteger("animationTicksChainPrev");
		LENGTH = nbt.getInteger("length");
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
