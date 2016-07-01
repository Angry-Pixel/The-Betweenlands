package thebetweenlands.common.network.base;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public interface IPacket {
    void deserialize(ByteBuf buffer);

    void serialize(ByteBuf buffer);

    void setContext(MessageContext ctx);
}
