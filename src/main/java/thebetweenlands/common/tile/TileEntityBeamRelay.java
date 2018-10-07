package thebetweenlands.common.tile;

import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.block.structure.BlockBeamRelay;
import thebetweenlands.common.block.structure.BlockEnergyBarrier;
import thebetweenlands.util.AABBDerpHelper;

public class TileEntityBeamRelay extends TileEntity implements ITickable {
	public boolean active;
	public boolean showRenderBox;
	float xPos, yPos, zPos;
	float xNeg, yNeg, zNeg;

	public TileEntityBeamRelay() {
		super();
	}

	@Override
	public void update() {
		if (getWorld().getBlockState(getPos()).getBlock() != null) {
			if (getWorld().getBlockState(getPos()).getValue(BlockBeamRelay.POWERED)) {
				if (!active)
					setActive(true);
			}

			if (!getWorld().getBlockState(getPos()).getValue(BlockBeamRelay.POWERED)) {
				if (active)
					setActive(false);
			}
		}

		if (!getWorld().isRemote) {
			checkBlockPower();
			//if (active)
			//	setAABBWithModifiers();
		}
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}

	public void setActive(boolean isActive) {
		active = isActive;
		showRenderBox = isActive;
		getWorld().notifyBlockUpdate(getPos(), getWorld().getBlockState(getPos()), getWorld().getBlockState(getPos()), 3);
	}
/*
	public void setAABBWithModifiers() {
		IBlockState state = getWorld().getBlockState(getPos());
		EnumFacing facing = state.getValue(BlockBeamRelay.FACING);

		int distance;
		for (distance = 1; distance < 12; distance++) {
			IBlockState state2 = getWorld().getBlockState(getPos().offset(facing, distance));
			if (state2 != Blocks.AIR.getDefaultState() && !(state2.getBlock() instanceof BlockEnergyBarrier))
				break;
		}

		if (facing == EnumFacing.UP) {
			yPos = distance;
			yNeg = -1;
			xPos = -0.375F;
			xNeg = -0.375F;
			zPos = -0.375F;
			zNeg = -0.375F;
		}
		if (facing == EnumFacing.DOWN) {
			yNeg = distance;
			yPos = -1;
			xPos = -0.375F;
			xNeg = -0.375F;
			zPos = -0.375F;
			zNeg = -0.375F;
		}
		if (facing == EnumFacing.WEST) {
			xNeg = distance;
			xPos = -1;
			zPos = -0.375F;
			zNeg = -0.375F;
			yPos = -0.375F;
			yNeg = -0.375F;
		}
		if (facing == EnumFacing.EAST) {
			xPos = distance;
			xNeg = -1;
			zPos = -0.375F;
			zNeg = -0.375F;
			yPos = -0.375F;
			yNeg = -0.375F;
		}
		if (facing == EnumFacing.NORTH) {
			zNeg = distance;
			zPos = -1;
			xPos = -0.375F;
			xNeg = -0.375F;
			yPos = -0.375F;
			yNeg = -0.375F;
		}
		if (facing == EnumFacing.SOUTH) {
			zPos = distance;
			zNeg = -1;
			xPos = -0.375F;
			xNeg = -0.375F;
			yPos = -0.375F;
			yNeg = -0.375F;
		}
		getWorld().notifyBlockUpdate(getPos(), state, state, 8);
	}

	public AxisAlignedBB getAABBWithModifiers() {
		return new AxisAlignedBB(getPos().getX() - xNeg, getPos().getY() - yNeg, getPos().getZ() - zNeg, getPos().getX() + 1D + xPos, getPos().getY() + 1D + yPos, getPos().getZ() + 1D + zPos);
	}

	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getAABBForRender() {
		return new AxisAlignedBB(- xNeg, - yNeg, - zNeg, 1D + xPos, 1D + yPos, 1D + zPos);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		return new AxisAlignedBB(getPos().getX() - xNeg, getPos().getY() - yNeg, getPos().getZ() - zNeg, getPos().getX() + 1D + xPos, getPos().getY() + 1D + yPos, getPos().getZ() + 1D + zPos);
	}
*/
	public void checkBlockPower() {
		IBlockState state = getWorld().getBlockState(getPos());
		AxisAlignedBB thisBox = state.getCollisionBoundingBox(getWorld(), getPos()).offset(getPos());
		// EnumFacing facing = state.getValue(BlockBeamRelay.FACING); 
		List<AxisAlignedBB> list = getWorld().getCollisionBoxes(null, thisBox);
		for (int i = 0; i < list.size(); i++) {
			AxisAlignedBB targetbox = list.get(i);
			/*System.out.println("Target Box" + targetbox);
				if (targetbox != null || !(targetbox instanceof AABBDerpHelper)) {
					if (state.getBlock() instanceof BlockBeamRelay) {
						if (state.getValue(BlockBeamRelay.POWERED)) {
							System.out.println("nope!");
							state = state.cycleProperty(BlockBeamRelay.POWERED);
							getWorld().setBlockState(getPos(), state, 3);
						}
					}
				}
*/
				if (targetbox != null && targetbox instanceof AABBDerpHelper) {
					if (state.getBlock() instanceof BlockBeamRelay) {
						if (!state.getValue(BlockBeamRelay.POWERED)) {
							System.out.println("yup!");
							state = state.cycleProperty(BlockBeamRelay.POWERED);
							getWorld().setBlockState(getPos(), state, 3);

						}
					}
				}
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setBoolean("active", active);
		nbt.setBoolean("showRenderBox", showRenderBox);
		nbt.setFloat("xPos", xPos);
		nbt.setFloat("yPos", yPos);
		nbt.setFloat("zPos", zPos);
		nbt.setFloat("xNeg", xNeg);
		nbt.setFloat("yNeg", yNeg);
		nbt.setFloat("zNeg", zNeg);
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		active = nbt.getBoolean("active");
		showRenderBox = nbt.getBoolean("showRenderBox");
		xPos = nbt.getFloat("xPos");
		yPos = nbt.getFloat("yPos");
		zPos = nbt.getFloat("zPos");
		xNeg = nbt.getFloat("xNeg");
		yNeg = nbt.getFloat("yNeg");
		zNeg = nbt.getFloat("zNeg");
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
