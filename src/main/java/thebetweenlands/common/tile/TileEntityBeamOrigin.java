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
import thebetweenlands.common.block.structure.BlockBeamOrigin;
import thebetweenlands.common.block.structure.BlockBeamRelay;
import thebetweenlands.common.block.structure.BlockDiagonalEnergyBarrier;

public class TileEntityBeamOrigin extends TileEntity implements ITickable {

	public boolean active;
	public boolean showRenderBox;
	float xPos, yPos, zPos;
	float xNeg, yNeg, zNeg;

	public TileEntityBeamOrigin() {
		super();
	}

	@Override
	public void update() {
		if (getWorld().getBlockState(getPos()).getBlock() != null) {
			if (getWorld().getBlockState(getPos()).getValue(BlockBeamOrigin.POWERED)) {
				if (!active)
					setActive(true);
			}

			if (!getWorld().getBlockState(getPos()).getValue(BlockBeamOrigin.POWERED)) {
				if (active)
					setActive(false);
			}
		}

		if (!getWorld().isRemote) {
			setAABBWithModifiers();
			if (active)
				activateBlock();
			else
				deactivateBlock();
		}
	}

	public void activateBlock() {
		IBlockState state = getWorld().getBlockState(getPos());
		EnumFacing facing = state.getValue(BlockBeamOrigin.FACING);
		List<AxisAlignedBB> list = getWorld().getCollisionBoxes(null, getAABBWithModifiers());
		for (int i = 0; i < list.size(); i++) {
			AxisAlignedBB targetbox = list.get(i);
			if (targetbox != null) {
				BlockPos targetPos = new BlockPos(targetbox.minX, targetbox.minY, targetbox.minZ);
				IBlockState stateofTarget = getWorld().getBlockState(targetPos);
				if (stateofTarget.getBlock() instanceof BlockBeamRelay) {
					if (getWorld().getTileEntity(targetPos) instanceof TileEntityBeamRelay) {
						TileEntityBeamRelay targetTile = (TileEntityBeamRelay) getWorld().getTileEntity(targetPos);
						targetTile.setTargetIncomingBeam(facing.getOpposite(), true);
						if (!getWorld().getBlockState(targetPos).getValue(BlockBeamRelay.POWERED)) {
							stateofTarget = stateofTarget.cycleProperty(BlockBeamRelay.POWERED);
							getWorld().setBlockState(targetPos, stateofTarget, 3);
						}
					}
				}
			}
		}
	}

	public void deactivateBlock() {
		IBlockState state = getWorld().getBlockState(getPos());
		EnumFacing facing = state.getValue(BlockBeamOrigin.FACING);
		List<AxisAlignedBB> list = getWorld().getCollisionBoxes(null, getAABBWithModifiers());
		for (int i = 0; i < list.size(); i++) {
			AxisAlignedBB targetbox = list.get(i);
			if (targetbox != null) {
				BlockPos targetPos = new BlockPos(targetbox.minX, targetbox.minY, targetbox.minZ);
				IBlockState stateofTarget = getWorld().getBlockState(targetPos);
				if (stateofTarget.getBlock() instanceof BlockBeamRelay) {
					if (getWorld().getTileEntity(targetPos) instanceof TileEntityBeamRelay) {
						TileEntityBeamRelay targetTile = (TileEntityBeamRelay) getWorld().getTileEntity(targetPos);
						targetTile.setTargetIncomingBeam(facing.getOpposite(), false);
						if (!targetTile.isGettingBeamed())
							if (getWorld().getBlockState(targetPos).getValue(BlockBeamRelay.POWERED)) {
								stateofTarget = stateofTarget.cycleProperty(BlockBeamRelay.POWERED);
								getWorld().setBlockState(targetPos, stateofTarget, 3);
							}
					}
				}
			}
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

	public void setAABBWithModifiers() {
		IBlockState state = getWorld().getBlockState(getPos());
		EnumFacing facing = state.getValue(BlockBeamOrigin.FACING);
		int distance;
		for (distance = 1; distance < 12; distance++) {
			IBlockState state2 = getWorld().getBlockState(getPos().offset(facing, distance));
			if (state2 != Blocks.AIR.getDefaultState() && !(state2.getBlock() instanceof BlockDiagonalEnergyBarrier))
				break;
		}

		if (facing == EnumFacing.UP) {
			yPos = distance;
			yNeg = -1F;
			xPos = -0.375F;
			xNeg = -0.375F;
			zPos = -0.375F;
			zNeg = -0.375F;
		}
		if (facing == EnumFacing.DOWN) {
			yNeg = distance;
			yPos = -1F;
			xPos = -0.375F;
			xNeg = -0.375F;
			zPos = -0.375F;
			zNeg = -0.375F;
		}
		if (facing == EnumFacing.WEST) {
			xNeg = distance;
			xPos = -1F;
			zPos = -0.375F;
			zNeg = -0.375F;
			yPos = -0.375F;
			yNeg = -0.375F;
		}
		if (facing == EnumFacing.EAST) {
			xPos = distance;
			xNeg = -1F;
			zPos = -0.375F;
			zNeg = -0.375F;
			yPos = -0.375F;
			yNeg = -0.375F;
		}
		if (facing == EnumFacing.NORTH) {
			zNeg = distance;
			zPos = -1F;
			xPos = -0.375F;
			xNeg = -0.375F;
			yPos = -0.375F;
			yNeg = -0.375F;
		}
		if (facing == EnumFacing.SOUTH) {
			zPos = distance;
			zNeg = -1F;
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
