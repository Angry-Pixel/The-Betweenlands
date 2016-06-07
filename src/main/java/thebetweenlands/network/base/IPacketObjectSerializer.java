package thebetweenlands.network.base;

import io.netty.buffer.ByteBuf;

public interface IPacketObjectSerializer {
    IPacket deserializePacketObj(ByteBuf buffer) throws Exception;

    void serializePacketObj(IPacket pkt, ByteBuf buffer);
}
