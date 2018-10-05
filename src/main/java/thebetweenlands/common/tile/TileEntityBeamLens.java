package thebetweenlands.common.tile;

import java.util.List;

import net.minecraft.block.Block;
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
import thebetweenlands.common.block.structure.BlockBeamLens;
import thebetweenlands.common.block.structure.BlockEnergyBarrier;

public class TileEntityBeamLens extends TileEntity implements ITickable {

	public boolean active;
	public boolean showRenderBox;
	float xPos, yPos, zPos;
	float xNeg, yNeg, zNeg;

	public TileEntityBeamLens() {
		super();
	}

	@Override
	public void update() {
		if (getWorld().getBlockState(getPos()).getBlock() != null) {
			if (getWorld().getBlockState(getPos()).getValue(BlockBeamLens.POWERED)) {
				if (!active)
					setActive(true);
				activateBlock();
			}
		
		if (!getWorld().getBlockState(getPos()).getValue(BlockBeamLens.POWERED)) {
			if (active)
				setActive(false);
			deactivateBlock();
		} 
	}
		
		if (!getWorld().isRemote)
			setAABBWithModifiers();
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
		EnumFacing facing = state.getValue(BlockBeamLens.FACING);

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

	@SuppressWarnings("unchecked")
	protected Block activateBlock() {
		//IBlockState state = getWorld().getBlockState(getPos());
		//EnumFacing facing = state.getValue(BlockBeamLens.FACING);
		List<AxisAlignedBB> list = getWorld().getCollisionBoxes(null, getAABBWithModifiers());
		for (int i = 0; i < list.size(); i++) {
			AxisAlignedBB targetbox = list.get(i);
			if (targetbox != null) {
				IBlockState stateofTarget = getWorld().getBlockState(new BlockPos(targetbox.minX, targetbox.minY, targetbox.minZ));
				if (stateofTarget.getBlock() instanceof BlockBeamLens) {
					if (!getWorld().getBlockState(new BlockPos(targetbox.minX, targetbox.minY, targetbox.minZ)).getValue(BlockBeamLens.POWERED)) {
						System.out.println("Yup!");
						stateofTarget = stateofTarget.cycleProperty(BlockBeamLens.POWERED);
						getWorld().setBlockState(new BlockPos(targetbox.minX, targetbox.minY, targetbox.minZ), stateofTarget, 3);
					}
				}
			}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	protected Block deactivateBlock() {
		//IBlockState state = getWorld().getBlockState(getPos());
		//EnumFacing facing = state.getValue(BlockBeamLens.FACING);
		List<AxisAlignedBB> list = getWorld().getCollisionBoxes(null, getAABBWithModifiers());
		for (int i = 0; i < list.size(); i++) {
			AxisAlignedBB targetbox = list.get(i);
			if (targetbox != null) {
				IBlockState stateofTarget = getWorld().getBlockState(new BlockPos(targetbox.minX, targetbox.minY, targetbox.minZ));
				if (stateofTarget.getBlock() instanceof BlockBeamLens) {
					if (getWorld().getBlockState(new BlockPos(targetbox.minX, targetbox.minY, targetbox.minZ)).getValue(BlockBeamLens.POWERED)) {
						System.out.println("Nope!");
						stateofTarget = stateofTarget.cycleProperty(BlockBeamLens.POWERED);
						getWorld().setBlockState(new BlockPos(targetbox.minX, targetbox.minY, targetbox.minZ), stateofTarget, 3);
					}
				}
			}
		}
		return null;
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

	public void onContentsChanged() {
		if (this != null && !getWorld().isRemote) {
			final IBlockState state = getWorld().getBlockState(getPos());
			setAABBWithModifiers();
			getWorld().notifyBlockUpdate(getPos(), state, state, 8);
			markDirty();
		}
	}
}
