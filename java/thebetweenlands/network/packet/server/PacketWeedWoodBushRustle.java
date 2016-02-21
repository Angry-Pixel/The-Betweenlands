package thebetweenlands.network.packet.server;

import io.netty.buffer.ByteBuf;
import thebetweenlands.network.base.Packet;

public class PacketWeedWoodBushRustle extends Packet {
	private int x, y, z;
	private float strength;

	public PacketWeedWoodBushRustle() {}

	public PacketWeedWoodBushRustle(int x, int y, int z, float strength) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.strength = strength;
	}

	@Override
	public void serialize(ByteBuf buffer) {
		buffer.writeInt(x);
		buffer.writeInt(y);
		buffer.writeInt(z);
		buffer.writeFloat(strength);
	}

	@Override
	public void deserialize(ByteBuf buffer) {
		x = buffer.readInt();
		y = buffer.readInt();
		z = buffer.readInt();
		strength = buffer.readFloat();
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
	
	public float getStrength() {
		return strength;
	}
}
