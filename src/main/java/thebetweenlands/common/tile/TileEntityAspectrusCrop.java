package thebetweenlands.common.tile;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.aspect.Aspect;

public class TileEntityAspectrusCrop extends TileEntity implements ITickable {
	protected Aspect seedAspect = null;
	protected boolean hasSource = false;
	
	public int glowTicks = 0;

	public void setAspect(@Nullable Aspect aspect) {
		this.seedAspect = aspect;
		IBlockState state = this.world.getBlockState(this.pos);
		this.world.notifyBlockUpdate(this.pos, state, state, 3);
		this.markDirty();
	}

	
	@Nullable
	public Aspect getAspect() {
		return this.seedAspect;
	}
	
	public void setHasSource(boolean source) {
		this.hasSource = source;
		this.markDirty();
	}
	
	public boolean hasSource() {
		return this.hasSource;
	}
	
	@Override
	public void setWorld(World worldIn) {
		super.setWorld(worldIn);
		this.glowTicks = worldIn.rand.nextInt(200);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public double getMaxRenderDistanceSquared() {
		return 4096.0D * 6.0D;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		return new AxisAlignedBB(this.pos.getX() - 0.5D, this.pos.getY() - 0.5D, this.pos.getZ() - 0.5D, this.pos.getX() + 1.5D, this.pos.getY() + 1.5D, this.pos.getZ() + 1.5D);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		if(this.seedAspect != null) {
			this.seedAspect.writeToNBT(nbt);
		}
		nbt.setBoolean("hasSource", this.hasSource);
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		this.seedAspect = Aspect.readFromNBT(nbt);
		this.hasSource = nbt.getBoolean("hasSource");
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		if(this.seedAspect != null) {
			this.seedAspect.writeToNBT(nbt);
		}
		return new SPacketUpdateTileEntity(this.getPos(), 1, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		this.seedAspect = Aspect.readFromNBT(pkt.getNbtCompound());
		this.world.markBlockRangeForRenderUpdate(this.pos, this.pos);
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		NBTTagCompound nbt = super.getUpdateTag();
		if(this.seedAspect != null) {
			this.seedAspect.writeToNBT(nbt);
		}
		return nbt;
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
		//Use vanilla behaviour to prevent TE from resetting when changing state
		return oldState.getBlock() != newSate.getBlock();
	}

	@Override
	public void update() {
		this.glowTicks++;
	}
}
