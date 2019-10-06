package thebetweenlands.common.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;

public class TileEntityPresent extends TileEntityLootInventory {
	public TileEntityPresent() {
		super(3, "container.bl.present");
	}
}
