package thebetweenlands.network.packets;

import io.netty.buffer.ByteBuf;
import thebetweenlands.network.base.IPacket;
import thebetweenlands.tileentities.TileEntityAnimator;

public class PacketAnimatorProgress implements IPacket {
	public PacketAnimatorProgress() { }
	
	public int x, y, z, progress;
	
	public PacketAnimatorProgress(TileEntityAnimator tile) {
		this.x = tile.xCoord;
		this.y = tile.yCoord;
		this.z = tile.zCoord;
		this.progress = tile.progress;
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
