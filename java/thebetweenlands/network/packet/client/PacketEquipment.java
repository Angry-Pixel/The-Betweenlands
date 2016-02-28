package thebetweenlands.network.packet.client;

import io.netty.buffer.ByteBuf;
import thebetweenlands.network.base.Packet;

public class PacketEquipment extends Packet {
	private int mode;
	private int slot;

	public PacketEquipment() {}

	/**
	 * Notifies the server that the player wants to equip or unequip an item.
	 * @param mode 0: Equip 1: Unequip
	 * @param slot in mode 0 inventory slot, in mode 1 equipment slot
	 */
	public PacketEquipment(int mode, int slot) {
		this.mode = mode;
		this.slot = slot;
	}

	@Override
	public void deserialize(ByteBuf buffer) {
		this.mode = buffer.readInt();
		this.slot = buffer.readInt();
	}

	@Override
	public void serialize(ByteBuf buffer) {
		buffer.writeInt(this.mode);
		buffer.writeInt(this.slot);
	}

	public int getMode() {
		return this.mode;
	}

	public int getSlot() {
		return this.slot;
	}
}
