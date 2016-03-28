package thebetweenlands.common.network.base.impl;

import java.util.HashMap;

import io.netty.buffer.ByteBuf;
import thebetweenlands.common.network.base.IPacket;
import thebetweenlands.common.network.base.IPacketObjectSerializer;

/**
 * Serializes the packets by ID. Server and client both have to have the same packet ID map.
 * Packets have to be registeres with an ID.
 */
public final class IDPacketObjectSerializer implements IPacketObjectSerializer {
	private final HashMap<Byte, IPacket> packetObjMap = new HashMap<Byte, IPacket>();
	private final HashMap<Class<? extends IPacket>, Byte> packetIDMap = new HashMap<Class<? extends IPacket>, Byte>();

	@Override
	public IPacket deserializePacketObj(ByteBuf buffer) throws Exception {
		byte pktID = buffer.readByte();
		IPacket pkt = this.packetObjMap.get(pktID);
		if(pkt == null) {
			throw new RuntimeException("Tried to deserialize a packet object with an unknown ID!");
		}
		return pkt;
	}

	@Override
	public void serializePacketObj(IPacket pkt, ByteBuf buffer) {
		Byte pktID = this.packetIDMap.get(pkt.getClass());
		if(pktID == null) {
			throw new RuntimeException("Tried to serialize a packet object with an unknown ID!");
		}
		buffer.writeByte(pktID.byteValue());
	}

	public void registerPacket(Class<? extends IPacket> pkt, byte ID) {
		if(this.packetObjMap.get(ID) != null) {
			throw new RuntimeException("The ID " + ID + " has already been used");
		}
		try {
			this.packetObjMap.put(ID, pkt.newInstance());
			this.packetIDMap.put(pkt, ID);
		} catch(Exception ex) {
			throw new RuntimeException("Failed to register packet", ex);
		}
	}

	public void registerPacket(Class<? extends IPacket> pkt) {
		for(byte id = 0; id < 255; id++) {
			if(this.packetObjMap.get(id) == null) {
				try {
					this.packetObjMap.put(id, pkt.newInstance());
					this.packetIDMap.put(pkt, id);
				} catch(Exception ex) {
					throw new RuntimeException("Failed to register packet", ex);
				}
				return;
			}
		}
		throw new RuntimeException("No packet IDs left");
	}
}
