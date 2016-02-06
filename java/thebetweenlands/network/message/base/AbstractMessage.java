package thebetweenlands.network.message.base;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import thebetweenlands.TheBetweenlands;

public abstract class AbstractMessage<REQ extends AbstractMessage> implements IMessage, IMessageHandler<REQ, IMessage>
{
    public IMessage onMessage(REQ message, MessageContext ctx)
    {
        if (ctx.side.isClient()) onMessageClientSide(message, TheBetweenlands.proxy.getClientPlayer());
        else onMessageServerSide(message, ctx.getServerHandler().playerEntity);
        return null;
    }

    @SideOnly(Side.CLIENT)
    public abstract void onMessageClientSide(REQ message, EntityPlayer player);

    public abstract void onMessageServerSide(REQ message, EntityPlayer player);
}
