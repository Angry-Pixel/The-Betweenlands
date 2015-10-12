package thebetweenlands.network.packets;

import io.netty.buffer.ByteBuf;
import thebetweenlands.network.base.IPacket;

public class PacketWeedWoodBushRustle implements IPacket {
	private int x, y, z;

	public PacketWeedWoodBushRustle() {}

	public PacketWeedWoodBushRustle(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public void serialize(ByteBuf buffer) {
		buffer.writeInt(x);
		buffer.writeInt(y);
		buffer.writeInt(z);
	}

	@Override
	public void deserialize(ByteBuf buffer) {
		x = buffer.readInt();
		y = buffer.readInt();
		z = buffer.readInt();
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}
}
