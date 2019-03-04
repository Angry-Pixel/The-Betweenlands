package thebetweenlands.common.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.common.block.structure.BlockWaystone;
import thebetweenlands.util.StatePropertyHelper;

public class TileEntityWaystone extends TileEntity {
	private float rotation;

	public TileEntityWaystone() { }

	public TileEntityWaystone(float rotation) {
		this.rotation = rotation;
	}

	public void setRotation(float rotation) {
		this.rotation = rotation;
		this.markDirty();
	}

	public float getRotation() {
		return this.rotation;
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
		return oldState.getBlock() != newSate.getBlock(); //Sate ftw
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setFloat("rotation", this.rotation);
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		this.rotation = nbt.getFloat("rotation");
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setFloat("rotation", this.rotation);
		return new SPacketUpdateTileEntity(this.getPos(), 1, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		this.rotation = pkt.getNbtCompound().getFloat("rotation");
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		NBTTagCompound nbt = super.getUpdateTag();
		nbt.setFloat("rotation", this.rotation);
		return nbt;
	}

	@Override
	public void handleUpdateTag(NBTTagCompound nbt) {
		super.handleUpdateTag(nbt);
		this.rotation = nbt.getFloat("rotation");
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		AxisAlignedBB aabb = super.getRenderBoundingBox();

		if(StatePropertyHelper.getStatePropertySafely(this, BlockWaystone.class, BlockWaystone.ACTIVE, false)) {
			aabb = aabb.grow(5.5f);
		}

		aabb = aabb.expand(0, 2, 0);

		return aabb;
	}
}
