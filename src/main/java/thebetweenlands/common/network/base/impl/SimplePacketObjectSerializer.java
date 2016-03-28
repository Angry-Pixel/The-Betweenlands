package thebetweenlands.common.network.base.impl;

import java.nio.charset.Charset;
import java.util.HashMap;

import io.netty.buffer.ByteBuf;
import thebetweenlands.common.network.base.IPacket;
import thebetweenlands.common.network.base.IPacketObjectSerializer;

/**
 * Serializes the packets by name. Server and client both have to have the same packet class in the same location.
 * Packets don't have to be registered.
 */
public final class SimplePacketObjectSerializer implements IPacketObjectSerializer {
	private final HashMap<String, IPacket> packetObjMap = new HashMap<String, IPacket>();

	@Override
	public IPacket deserializePacketObj(ByteBuf buffer) throws Exception {
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
	public void serializePacketObj(IPacket pkt, ByteBuf buffer) {
		String pktClassName = pkt.getClass().getName();
		byte[] strBytes = pktClassName.getBytes(Charset.forName("UTF-8"));
		buffer.writeInt(strBytes.length);
		buffer.writeBytes(strBytes);
	}
}
