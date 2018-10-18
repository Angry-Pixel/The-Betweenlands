package thebetweenlands.common.network;

import java.io.IOException;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public abstract class MessageBase implements IMessage {
	@Override
	public final void toBytes(ByteBuf buf) {
		try {
			serialize(new PacketBuffer(buf));
		} catch(IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public final void fromBytes(ByteBuf buf) {
		try {
			deserialize(new PacketBuffer(buf));
		} catch(IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	public abstract void serialize(PacketBuffer buf) throws IOException;

	public abstract void deserialize(PacketBuffer buf) throws IOException;

	public abstract IMessage process(MessageContext ctx);
}
