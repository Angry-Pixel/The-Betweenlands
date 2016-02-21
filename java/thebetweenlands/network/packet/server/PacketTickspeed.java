package thebetweenlands.network.packet.server;

import io.netty.buffer.ByteBuf;
import thebetweenlands.network.base.Packet;

public class PacketTickspeed extends Packet {
	private float ticksPerSecond;

	public PacketTickspeed() {
	}

	public PacketTickspeed(float ticksPerSecond) {
		this.ticksPerSecond = ticksPerSecond;
	}

	@Override
	public void deserialize(ByteBuf buffer) {
		ticksPerSecond = buffer.readFloat();
	}

	@Override
	public void serialize(ByteBuf buffer) {
		buffer.writeFloat(ticksPerSecond);
	}

	public float getTicksPerSecond() {
		return ticksPerSecond;
	}
}
