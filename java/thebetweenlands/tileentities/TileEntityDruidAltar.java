package thebetweenlands.tileentities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.util.AxisAlignedBB;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityDruidAltar extends TileEntityBasicInventory  {
	
	@SideOnly(Side.CLIENT)
	public float rotation;
	@SideOnly(Side.CLIENT)
	public float renderRotation;
	@SideOnly(Side.CLIENT)
	private static final float ROTATION_SPEED = 2.0F;
	
	public TileEntityDruidAltar() {
		super(5, "druidAltar");
	}
	
	@Override
	public void updateEntity() {
		if (worldObj.isRemote) {
			rotation += ROTATION_SPEED;
			if (rotation >= 360.0F) {
				rotation -= 360.0F;
				renderRotation -= 360.0F;
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		return AxisAlignedBB.getBoundingBox(xCoord - 1, yCoord, zCoord - 1, xCoord + 2, yCoord + 3, zCoord + 2);
	}
	
	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound tag = new NBTTagCompound();
		writeToNBT(tag);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, tag);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
		if (packet.func_148853_f() == 0)
			readFromNBT(packet.func_148857_g());
	}

}
