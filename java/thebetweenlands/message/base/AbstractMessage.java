package thebetweenlands.message.base;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.entity.player.EntityPlayer;
import thebetweenlands.TheBetweenlands;

public abstract class AbstractMessage<REQ extends AbstractMessage> implements IMessage, IMessageHandler<REQ, IMessage>
{
    public IMessage onMessage(REQ message, MessageContext ctx)
    {
        if (ctx.side.isClient()) onClientMessage(message, TheBetweenlands.proxy.getClientPlayer());
        else onServerMessage(message, ctx.getServerHandler().playerEntity);
        return null;
    }

    public abstract void onClientMessage(REQ message, EntityPlayer player);

    public abstract void onServerMessage(REQ message, EntityPlayer player);
}
