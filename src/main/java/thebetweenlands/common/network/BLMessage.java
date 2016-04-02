package thebetweenlands.common.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public abstract class BLMessage implements IMessage {
	@Override
	public final void toBytes(ByteBuf buf) {
		serialize(new PacketBuffer(buf));
	}

	@Override
	public final void fromBytes(ByteBuf buf) {
		deserialize(new PacketBuffer(buf));
	}

	public abstract void serialize(PacketBuffer buf);

	public abstract void deserialize(PacketBuffer buf);

	public abstract IMessage process(MessageContext ctx);
}
