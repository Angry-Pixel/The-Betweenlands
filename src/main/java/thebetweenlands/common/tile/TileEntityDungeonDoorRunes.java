package thebetweenlands.common.tile;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.block.structure.BlockDungeonDoorRunes;
import thebetweenlands.common.registries.BlockRegistry;

public class TileEntityDungeonDoorRunes extends TileEntity implements ITickable {

	public int top_code = -1, mid_code = -1, bottom_code = -1;
	public int top_state = 0, mid_state = 0, bottom_state = 0;
	public int top_state_prev = 0, mid_state_prev = 0, bottom_state_prev = 0;
	public int top_rotate = 0, mid_rotate = 0, bottom_rotate = 0;
	public int lastTickTopRotate = 0, lastTickMidRotate = 0, lastTickBottomRotate = 0;
	public int renderTicks = 0;
	public boolean mimic = false;
	public boolean animate_open = false;
	public boolean break_blocks = false;
	public int slate_1_rotate = 0, slate_2_rotate = 0, slate_3_rotate = 0;
	public int last_tick_slate_1_rotate = 0, last_tick_slate_2_rotate = 0, last_tick_slate_3_rotate = 0;

	public TileEntityDungeonDoorRunes() {
		super();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		return new AxisAlignedBB(getPos()).grow(1D);
	}

	public void sinkingParticles() {
		IBlockState state = getWorld().getBlockState(getPos());
		EnumFacing facing = state.getValue(BlockDungeonDoorRunes.FACING);
		if (facing == EnumFacing.WEST || facing == EnumFacing.EAST) {
			for (int z = -1; z <= 1; z++)
				if (getWorld().isRemote)
					spawnSinkingParticles(getPos().add(0, -1, z));
		}

		if (facing == EnumFacing.NORTH || facing == EnumFacing.SOUTH) {
			for (int x = -1; x <= 1; x++)
				if (getWorld().isRemote)
					spawnSinkingParticles(getPos().add(x, -1, 0));
		}	
	}

	private void spawnSinkingParticles(BlockPos pos) {
		if (getWorld().isRemote) {
			double px = pos.getX() + 0.5D;
			double py = pos.getY() + 0.0625D;
			double pz = pos.getZ() + 0.5D;
			for (int i = 0, amount = 5 + getWorld().rand.nextInt(5); i < amount; i++) {
				double ox = getWorld().rand.nextDouble() * 0.1F - 0.05F;
				double oz = getWorld().rand.nextDouble() * 0.1F - 0.05F;
				double motionX = getWorld().rand.nextDouble() * 0.2F - 0.1F;
				double motionY = getWorld().rand.nextDouble() * 0.1F + 0.075F;
				double motionZ = getWorld().rand.nextDouble() * 0.2F - 0.1F;
				world.spawnParticle(EnumParticleTypes.BLOCK_DUST, px + ox, py, pz + oz, motionX, motionY, motionZ, Block.getStateId(BlockRegistry.MUD_TILES.getDefaultState()));
			}
		}
	}

	@Override
	public void update() {
		renderTicks++;

		lastTickTopRotate = top_rotate;
		lastTickMidRotate = mid_rotate;
		lastTickBottomRotate = bottom_rotate;

		last_tick_slate_1_rotate = slate_1_rotate;
		last_tick_slate_2_rotate = slate_2_rotate;
		last_tick_slate_3_rotate = slate_3_rotate;

		if (top_state_prev != top_state) {
			top_rotate += 4;
			if (top_rotate > 90) {
				lastTickTopRotate = top_rotate = 0;
				top_state_prev = top_state;
			}
		}

		if (mid_state_prev != mid_state) {
			mid_rotate += 4;
			if (mid_rotate > 90) {
				lastTickMidRotate = mid_rotate = 0;
				mid_state_prev = mid_state;
			}
		}

		if (bottom_state_prev != bottom_state) {
			bottom_rotate += 4;
			if (bottom_rotate > 90) {
				lastTickBottomRotate = bottom_rotate = 0;
				bottom_state_prev = bottom_state;
			}
		}

		if (animate_open) {
			slate_1_rotate += 4;
			slate_2_rotate += 3;
			slate_3_rotate += 3;
			int limit = 180;
			if (mimic)
				limit = 90;
			if(!mimic)
				sinkingParticles();
			if (slate_1_rotate > limit)
				last_tick_slate_1_rotate = slate_1_rotate = limit;
			if (slate_2_rotate > limit)
				last_tick_slate_2_rotate = slate_2_rotate = limit;
			if (slate_3_rotate > limit) {
				last_tick_slate_3_rotate = slate_3_rotate = limit;
				if (!getWorld().isRemote) {
					break_blocks = true;
					animate_open = false;
					getWorld().notifyBlockUpdate(getPos(), getWorld().getBlockState(getPos()), getWorld().getBlockState(getPos()), 3);
				}
			}
		}

		if (!getWorld().isRemote) {
			IBlockState state = getWorld().getBlockState(getPos());
			EnumFacing facing = state.getValue(BlockDungeonDoorRunes.FACING);
			if (top_state_prev == top_code && mid_state_prev == mid_code && bottom_state_prev == bottom_code) {
				animate_open = true;
				getWorld().notifyBlockUpdate(getPos(), getWorld().getBlockState(getPos()), getWorld().getBlockState(getPos()), 3);
			}

			if (break_blocks) {
				if (!mimic)
					breakAllDoorBlocks(state, facing, true, false);
				else {
					breakAllDoorBlocks(state, facing, false, false);
					BlockPos offsetPos = getPos().offset(facing);
					EntityChicken entity = new EntityChicken(getWorld()); // door golem here :P
					entity.setLocationAndAngles(offsetPos.getX() + 0.5D, offsetPos.getY(), offsetPos.getZ() + 0.5D, 0.0F, 0.0F);
					getWorld().spawnEntity(entity);
				}
			}

			if (getWorld().getTotalWorldTime() % 5 == 0)
				checkComplete(state, facing);
		}
	}

	private void checkComplete(IBlockState state, EnumFacing facing) {
		if (facing == EnumFacing.WEST || facing == EnumFacing.EAST) {
			for (int z = -1; z <= 1; z++)
				for (int y = -1; y <= 1; y++)
					if(getPos().add(0, y, z) != getPos()) {
						if (!(getWorld().getBlockState(getPos().add(0, y, z)).getBlock() instanceof BlockDungeonDoorRunes))
							breakAllDoorBlocks(state, facing, false, true);
					}
		}

		if (facing == EnumFacing.NORTH || facing == EnumFacing.SOUTH) {
			for (int x = -1; x <= 1; x++)
				for (int y = -1; y <= 1; y++)
					if(getPos().add(x, y, 0) != getPos()) {
						if (!(getWorld().getBlockState(getPos().add(x, y, 0)).getBlock() instanceof BlockDungeonDoorRunes))
							breakAllDoorBlocks(state, facing, false, true);
					}
		}
	}

	public void breakAllDoorBlocks(IBlockState state, EnumFacing facing,  boolean breakFloorBelow, boolean particles) {
		if (facing == EnumFacing.WEST || facing == EnumFacing.EAST) {
			for (int z = -1; z <= 1; z++)
				for (int y = breakFloorBelow ? -2 : -1; y <= 1; y++)
					if (particles)
						getWorld().destroyBlock(getPos().add(0, y, z), false);
					else {
						getWorld().setBlockState(getPos().add(0, y, z), Blocks.AIR.getDefaultState(), 3);
						getWorld().removeTileEntity(getPos());
					}
		}

		if (facing == EnumFacing.NORTH || facing == EnumFacing.SOUTH) {
			for (int x = -1; x <= 1; x++)
				for (int y = breakFloorBelow ? -2 : -1; y <= 1; y++)
					if (particles)
						getWorld().destroyBlock(getPos().add(x, y, 0), false);
					else {
						getWorld().setBlockState(getPos().add(x, y, 0), Blocks.AIR.getDefaultState(), 3);
						getWorld().removeTileEntity(getPos());
					}
		}
	}

	public void cycleTopState() {
		top_state_prev = top_state;
		top_state++;
		if (top_state > 7)
			top_state = 0;
	}

	public void cycleMidState() {
		mid_state_prev = mid_state;
		mid_state++;
		if (mid_state > 7)
			mid_state = 0;
	}

	public void cycleBottomState() {
		bottom_state_prev = bottom_state;
		bottom_state++;
		if (bottom_state > 7)
			bottom_state = 0;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		top_code = nbt.getInteger("top_code");
		mid_code = nbt.getInteger("mid_code");
		bottom_code = nbt.getInteger("bottom_code");
		top_state = nbt.getInteger("top_state");
		mid_state = nbt.getInteger("mid_state");
		bottom_state = nbt.getInteger("bottom_state");
		top_state_prev = nbt.getInteger("top_state_prev");
		mid_state_prev = nbt.getInteger("mid_state_prev");
		bottom_state_prev = nbt.getInteger("bottom_state_prev");
		mimic = nbt.getBoolean("mimic");
		animate_open = nbt.getBoolean("animate_open");
		break_blocks = nbt.getBoolean("break_blocks");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setInteger("top_code", top_code);
		nbt.setInteger("mid_code", mid_code);
		nbt.setInteger("bottom_code", bottom_code);
		nbt.setInteger("top_state", top_state);
		nbt.setInteger("mid_state", mid_state);
		nbt.setInteger("bottom_state", bottom_state);
		nbt.setInteger("top_state_prev", top_state_prev);
		nbt.setInteger("mid_state_prev", mid_state_prev);
		nbt.setInteger("bottom_state_prev", bottom_state_prev);
		nbt.setBoolean("mimic", mimic);
		nbt.setBoolean("animate_open", animate_open);
		nbt.setBoolean("break_blocks", break_blocks);
		return nbt;
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
