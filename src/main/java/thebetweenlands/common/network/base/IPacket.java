package thebetweenlands.common.network.base;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Requires a default or no-arg constructor in order to work!
 */
public interface IPacket {
	public void deserialize(ByteBuf buffer);
	public void serialize(ByteBuf buffer);
	public void setContext(MessageContext ctx);
}
