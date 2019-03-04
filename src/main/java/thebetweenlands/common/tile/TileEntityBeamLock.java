package thebetweenlands.common.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.common.block.structure.BlockBeamLock;

public class TileEntityBeamLock extends TileEntity implements ITickable {
	public boolean active;

	public TileEntityBeamLock() {
		super();
	}

	@Override
	public void update() {
		if (getWorld().getBlockState(getPos()).getBlock() != null) {
			if (getWorld().getBlockState(getPos()).getValue(BlockBeamLock.POWERED)) {
				if (!active)
					setActive(true);
			}

			if (!getWorld().getBlockState(getPos()).getValue(BlockBeamLock.POWERED)) {
				if (active)
					setActive(false);
			}
		}

		if (!getWorld().isRemote) {
			if (active)
				activateBlock();
			else
				deactivateBlock();
		}
	}

	public void activateBlock() {
		if (!getWorld().isRemote) {
			System.out.println("Lock activated! - Do opening thing here!");
		}

	}

	public void deactivateBlock() {
		if (!getWorld().isRemote) {
			System.out.println("Lock NOT activated!");
		}
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}

	public void setActive(boolean isActive) {
		active = isActive;
		getWorld().notifyBlockUpdate(getPos(), getWorld().getBlockState(getPos()), getWorld().getBlockState(getPos()), 3);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setBoolean("active", active);
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		active = nbt.getBoolean("active");
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

}
