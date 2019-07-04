package thebetweenlands.common.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityDecayPitHangingChain extends TileEntity implements ITickable {

	public int animationTicksChain = 0;
	public int animationTicksChainPrev = 0;
	public int PROGRESS = 0;
	public final float MOVE_UNIT = 0.0078125F; // unit of movement 
	public boolean IS_RAISING = false;
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

		getHangingLength(2F + getProgress() * MOVE_UNIT);

		if (animationTicksChainPrev >= 128) {
			animationTicksChain = animationTicksChainPrev = 0;
			setMoving(false);
		}

	}

	public AxisAlignedBB getHangingLength(float extended) {
		return new AxisAlignedBB(0.1875D, - extended,  0.1875D, 0.8125D, 0D, 0.8125D);
	}

	public void setProgress(int progress) {
		PROGRESS = progress;
	}

	public int getProgress() {
		return PROGRESS;
	}

	public void setRaising(boolean raising) {
		IS_RAISING = raising;
	}

	public boolean isRaising() {
		return IS_RAISING;
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

	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getAABBForRender() {
		return getHangingLength(2F + getProgress() * MOVE_UNIT);
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return INFINITE_EXTENT_AABB;
	}
}
