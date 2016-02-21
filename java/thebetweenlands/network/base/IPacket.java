package thebetweenlands.network.base;

import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

/**
 * Requires a default or no-arg constructor in order to work!
 */
public interface IPacket {
	public void deserialize(ByteBuf buffer);
	public void serialize(ByteBuf buffer);
	public void setContext(MessageContext ctx);
}
