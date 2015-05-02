package thebetweenlands.network.base;

import io.netty.buffer.ByteBuf;

public interface IPacketSerializer {
	public IPacket deserialize(ByteBuf buffer) throws Exception;
	public void serialize(IPacket pkt, ByteBuf buffer);
}
