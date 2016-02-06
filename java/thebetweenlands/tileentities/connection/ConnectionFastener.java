package thebetweenlands.tileentities.connection;

import thebetweenlands.connection.ConnectionType;
import thebetweenlands.tileentities.TileEntityConnectionFastener;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import thebetweenlands.blocks.lanterns.BlockConnectionFastener;
import thebetweenlands.utils.vectormath.Point3f;

public class ConnectionFastener extends Connection {
	private int toX;

	private int toY;

	private int toZ;

	public ConnectionFastener(ConnectionType type, TileEntityConnectionFastener fairyLightsFastener, World worldObj) {
		super(type, fairyLightsFastener, worldObj);
	}

	public ConnectionFastener(ConnectionType type, TileEntityConnectionFastener fairyLightsFastener, World worldObj, int toX, int toY, int toZ, boolean isOrigin,
							  NBTTagCompound tagCompound) {
		super(type, fairyLightsFastener, worldObj, isOrigin, tagCompound);
		this.toX = toX;
		this.toY = toY;
		this.toZ = toZ;
	}

	@Override
	public Point3f getTo() {
		Block toBlock = worldObj.getBlock(toX, toY, toZ);
		if (!(toBlock instanceof BlockConnectionFastener)) {
			return null;
		}
		Point3f point = ((BlockConnectionFastener) toBlock).getOffsetForData(worldObj.getBlockMetadata(toX, toY, toZ), 0.125F);
		point.x += toX;
		point.y += toY;
		point.z += toZ;
		return point;
	}

	@Override
	public int getToX() {
		return toX;
	}

	@Override
	public int getToY() {
		return toY;
	}

	@Override
	public int getToZ() {
		return toZ;
	}

	@Override
	public boolean shouldDisconnect() {
		return worldObj.blockExists(toX, toY, toZ) && !(worldObj.getTileEntity(toX, toY, toZ) instanceof TileEntityConnectionFastener);
	}

	@Override
	public void writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setInteger("x", toX);
		compound.setInteger("y", toY);
		compound.setInteger("z", toZ);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		toX = compound.getInteger("x");
		toY = compound.getInteger("y");
		toZ = compound.getInteger("z");
	}

}
