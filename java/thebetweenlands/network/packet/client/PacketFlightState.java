package thebetweenlands.network.packet.client;

import io.netty.buffer.ByteBuf;
import thebetweenlands.network.base.Packet;

public class PacketFlightState extends Packet {
	private boolean state;

	public PacketFlightState() {}

	public PacketFlightState(boolean state) {
		this.state = state;
	}

	@Override
	public void deserialize(ByteBuf buffer) {
		this.state = buffer.readBoolean();
	}

	@Override
	public void serialize(ByteBuf buffer) {
		buffer.writeBoolean(this.state);
	}

	public boolean isFlying() {
		return this.state;
	}
}
