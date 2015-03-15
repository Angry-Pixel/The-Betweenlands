package thebetweenlands.message;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import thebetweenlands.entities.property.EntityPropertiesDecay;
import thebetweenlands.message.base.AbstractMessage;

public class MessageSyncPlayerDecay extends AbstractMessage<MessageSyncPlayerDecay>
{
    public int playerDecay;

    public MessageSyncPlayerDecay()
    {

    }

    public MessageSyncPlayerDecay(int decay)
    {
        playerDecay = decay;
    }

    public void onClientMessage(MessageSyncPlayerDecay message, EntityPlayer player)
    {
        ((EntityPropertiesDecay) player.getExtendedProperties(EntityPropertiesDecay.getId())).decayLevel = message.playerDecay;
    }

    public void onServerMessage(MessageSyncPlayerDecay message, EntityPlayer player)
    {
        ((EntityPropertiesDecay) player.getExtendedProperties(EntityPropertiesDecay.getId())).decayLevel = message.playerDecay;
    }

    public void fromBytes(ByteBuf buf)
    {
        playerDecay = buf.readInt();
    }

    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(playerDecay);
    }
}
