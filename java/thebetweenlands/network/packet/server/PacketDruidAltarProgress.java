package thebetweenlands.network.packet.server;

import io.netty.buffer.ByteBuf;
import thebetweenlands.network.base.IPacket;
import thebetweenlands.tileentities.TileEntityDruidAltar;

public class PacketDruidAltarProgress implements IPacket {
	public PacketDruidAltarProgress() { }
	
	public int x, y, z, progress;
	
	public PacketDruidAltarProgress(TileEntityDruidAltar tile) {
		this.x = tile.xCoord;
		this.y = tile.yCoord;
		this.z = tile.zCoord;
		this.progress = tile.craftingProgress;
	}
	
	public PacketDruidAltarProgress(TileEntityDruidAltar tile, int progress) {
		this.x = tile.xCoord;
		this.y = tile.yCoord;
		this.z = tile.zCoord;
		this.progress = progress;
	}
	
	@Override
	public void deserialize(ByteBuf buffer) {
		this.x = buffer.readInt();
		this.y = buffer.readInt();
		this.z = buffer.readInt();
		this.progress = buffer.readInt();
	}

	@Override
	public void serialize(ByteBuf buffer) {
		buffer.writeInt(this.x);
		buffer.writeInt(this.y);
		buffer.writeInt(this.z);
		buffer.writeInt(this.progress);
	}
}
