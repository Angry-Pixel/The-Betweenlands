package thebetweenlands.network.base.impl;

import io.netty.buffer.ByteBuf;

import java.nio.charset.Charset;
import java.util.HashMap;

import thebetweenlands.network.base.IPacket;
import thebetweenlands.network.base.IPacketSerializer;

/**
 * Serializes the packets by name. Server and client both have to have the same packet class in the same location.
 */
public final class SimplePacketSerializer implements IPacketSerializer {
	private final HashMap<String, IPacket> packetObjMap = new HashMap<String, IPacket>();

	@Override
	public IPacket deserialize(ByteBuf buffer) throws Exception {
		int strLen = buffer.readInt();
		byte[] strBytes = new byte[strLen];
		buffer.readBytes(strBytes);
		String pktClassName = new String(strBytes, "UTF-8");
		IPacket pkt = this.packetObjMap.get(pktClassName);
		if(pkt == null) {
			Class<? extends IPacket> pktClass = (Class<? extends IPacket>) Class.forName(pktClassName);
			pkt = pktClass.newInstance();
			this.packetObjMap.put(pktClassName, pkt);
		}
		return pkt;
	}

	@Override
	public void serialize(IPacket pkt, ByteBuf buffer) {
		String pktClassName = pkt.getClass().getName();
		byte[] strBytes = pktClassName.getBytes(Charset.forName("UTF-8"));
		buffer.writeInt(strBytes.length);
		buffer.writeBytes(strBytes);
	}
}
