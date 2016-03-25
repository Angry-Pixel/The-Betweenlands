package thebetweenlands.network.packet.client;

import io.netty.buffer.ByteBuf;
import thebetweenlands.network.base.Packet;

public class PacketRingInput extends Packet {
	private boolean pressed;

	public PacketRingInput() {}

	public PacketRingInput(boolean pressed) {
		this.pressed = pressed;
	}

	@Override
	public void deserialize(ByteBuf buffer) {
		this.pressed = buffer.readBoolean();
	}

	@Override
	public void serialize(ByteBuf buffer) {
		buffer.writeBoolean(this.pressed);
	}

	public boolean isPressed() {
		return this.pressed;
	}
}
