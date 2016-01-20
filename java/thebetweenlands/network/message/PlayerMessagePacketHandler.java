package thebetweenlands.network.message;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;
import thebetweenlands.network.packet.server.PlayerMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PlayerMessagePacketHandler implements IMessageHandler<PlayerMessage, IMessage> {

	@Override
	public IMessage onMessage(PlayerMessage message, MessageContext ctx) {
		EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
		if (player.getEntityId() == message.entityID) {
			switch (message.messageID) {
			case 0: //plenty of id's here for any other player specific messages I guess ;)
				player.addChatComponentMessage(new ChatComponentText(StatCollector.translateToLocal("chat.bedSpawnSet")));
				break;
			}
		}
		return null;
	}
}