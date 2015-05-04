package thebetweenlands.network.base;

import io.netty.buffer.ByteBuf;

public interface IPacketObjectSerializer {
	public IPacket deserializePacketObj(ByteBuf buffer) throws Exception;
	public void serializePacketObj(IPacket pkt, ByteBuf buffer);
}
