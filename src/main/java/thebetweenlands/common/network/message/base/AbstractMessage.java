package thebetweenlands.common.network.message.base;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.TheBetweenlands;

public abstract class AbstractMessage<REQ extends AbstractMessage> implements IMessage, IMessageHandler<REQ, IMessage> {
	public IMessage onMessage(REQ message, MessageContext ctx) {
		if (ctx.side.isClient()) {
			onMessageClientSide(message, TheBetweenlands.proxy.getClientPlayer());
		} else {
			onMessageServerSide(message, ctx.getServerHandler().playerEntity);
		}
		return null;
	}

	@SideOnly(Side.CLIENT)
	public abstract void onMessageClientSide(REQ message, EntityPlayer player);

	public abstract void onMessageServerSide(REQ message, EntityPlayer player);
}
