package thebetweenlands.utils.network;

import io.netty.buffer.ByteBuf;

/**
 * Requires a default or no-arg constructor in order to work!
 */
public interface IPacket {
	public void deserialize(ByteBuf buffer);
	public void serialize(ByteBuf buffer);
}
