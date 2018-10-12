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
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.structure.BlockBeamRelay;
import thebetweenlands.common.block.structure.BlockDiagonalEnergyBarrier;
import thebetweenlands.common.network.clientbound.PacketParticle;
import thebetweenlands.common.network.clientbound.PacketParticle.ParticleType;

public class TileEntityBeamRelay extends TileEntity implements ITickable {
	public boolean active;
	public boolean showRenderBox;
	float xPos, yPos, zPos;
	float xNeg, yNeg, zNeg;
	public boolean in_down, in_up, in_north, in_south, in_west, in_east;


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
			setAABBWithModifiers();
			if (active)
				activateBlock();
			else
				deactivateBlock();
		}
	}
	
	private void sendParticleMessage(BlockPos pos, BlockPos targetPos, EnumFacing facing) {
		int dim = world.provider.getDimension();
		float distance = 0;
		
		switch (facing) {
		case DOWN:
			distance = yNeg;
			break;
		case EAST:
			distance = xPos;
			break;
		case NORTH:
			distance = zNeg;
			break;
		case SOUTH:
			distance = zPos;
			break;
		case UP:
			distance = yPos;
			break;
		case WEST:
			distance = xNeg;
			break;
		default:
			break;
		}
		targetPos = pos.offset(facing, (int) distance);
		Vec3d  vector = new Vec3d((targetPos.getX() + 0.5D) - (pos.getX() + 0.5D), (targetPos.getY() + 0.5D) - (pos.getY() + 0.5D), (targetPos.getZ() + 0.5D) - (pos.getZ() + 0.5D));
		vector = vector.normalize();
		for(float i = 0; i < distance; i+= 0.125F)
		{
			TheBetweenlands.networkWrapper.sendToAll(new PacketParticle(ParticleType.FLAME, pos.getX() + 0.5F + ((float)vector.x * i),  pos.getY()+ 0.5F + ((float)vector.y * i), pos.getZ()+ 0.5F + ((float)vector.z * i)));
		}
		//TheBetweenlands.networkWrapper.sendToAllAround(new MessageBeamFX(posX, posY, posZ, getDataManager().get(value) / 1.75f, this.getRed(), this.getGreen(), this.getBlue()), new NetworkRegistry.TargetPoint(dim, this.pos.getX() + 0.5D, this.pos.getY() + 0.5D, this.pos.getZ() + 0.5D, 64D));
	}

	public void activateBlock() {
		IBlockState state = getWorld().getBlockState(getPos());
		EnumFacing facing = state.getValue(BlockBeamRelay.FACING);
		List<AxisAlignedBB> list = getWorld().getCollisionBoxes(null, getAABBWithModifiers());
		for (int i = 0; i < list.size(); i++) {
			AxisAlignedBB targetbox = list.get(i);
			if (targetbox != null) {
				BlockPos targetPos = new BlockPos(targetbox.minX, targetbox.minY, targetbox.minZ);
				IBlockState stateofTarget = getWorld().getBlockState(targetPos);
				if(getWorld().getTotalWorldTime()%10 == 0)
					sendParticleMessage(getPos(), targetPos, facing);
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
		EnumFacing facing = state.getValue(BlockBeamRelay.FACING);
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

	public void setTargetIncomingBeam(EnumFacing facing, boolean state) {
		switch (facing) {
		case DOWN:
			in_down = state;
			break;
		case EAST:
			in_east = state;
			break;
		case NORTH:
			in_north = state;
			break;
		case SOUTH:
			in_south = state;
			break;
		case UP:
			in_up = state;
			break;
		case WEST:
			in_west = state;
			break;
		default:
			break;
		}
		
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}

	public void setActive(boolean isActive) {
		active = isActive;
		showRenderBox = false;
		getWorld().notifyBlockUpdate(getPos(), getWorld().getBlockState(getPos()), getWorld().getBlockState(getPos()), 3);
	}

	public void setAABBWithModifiers() {
		IBlockState state = getWorld().getBlockState(getPos());
		EnumFacing facing = state.getValue(BlockBeamRelay.FACING);

		int distance;
		for (distance = 1; distance < 14; distance++) {
			IBlockState state2 = getWorld().getBlockState(getPos().offset(facing, distance));
			if (state2 != Blocks.AIR.getDefaultState() && !(state2.getBlock() instanceof BlockDiagonalEnergyBarrier))
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

	public boolean isGettingBeamed() {
		return in_up ? true : in_down ? true : in_north ? true : in_south ? true : in_west ? true : in_east ? true : false ;
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
		nbt.setBoolean("in_down", in_down);
		nbt.setBoolean("in_up", in_up);
		nbt.setBoolean("in_north", in_north);
		nbt.setBoolean("in_south", in_south);
		nbt.setBoolean("in_west)", in_west);
		nbt.setBoolean("in_east", in_east);
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
		in_down = nbt.getBoolean("in_down");
		in_up = nbt.getBoolean("in_up");
		in_north = nbt.getBoolean("in_north");
		in_south = nbt.getBoolean("in_south");
		in_west = nbt.getBoolean("in_west)");
		in_east = nbt.getBoolean("in_east");
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
