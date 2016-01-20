package thebetweenlands.network.packet.server;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.network.simpleimpl.IMessage;

public class PlayerMessage implements IMessage {

	public int entityID, messageID;

	public PlayerMessage() {
	}

	public PlayerMessage(EntityPlayer player, int messageID) {
		entityID = player.getEntityId();
		this.messageID = messageID;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(entityID);
		buf.writeInt(messageID);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		entityID = buf.readInt();
		messageID = buf.readInt();
	}
}
