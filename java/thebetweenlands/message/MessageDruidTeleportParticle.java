package thebetweenlands.message;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.message.base.AbstractMessage;
import cpw.mods.fml.client.FMLClientHandler;

public class MessageDruidTeleportParticle extends AbstractMessage<MessageDruidTeleportParticle>
{
    public float posX;
    public float posY;
    public float posZ;

    public MessageDruidTeleportParticle()
    {

    }

    public MessageDruidTeleportParticle(float x, float y, float z)
    {
        posX = x;
        posY = y;
        posZ = z;
    }

    public void onClientMessage(MessageDruidTeleportParticle message, EntityPlayer player)
    {
        World world = FMLClientHandler.instance().getWorldClient();

        if (world != null && world.isRemote)
        {
            for (int a = 0; a < 360; a += 4)
            {
                double ang = a * Math.PI / 180D;
                TheBetweenlands.proxy.spawnCustomParticle("smoke", world, message.posX - MathHelper.sin((float) ang) * 0.25D, message.posY, message.posZ + MathHelper.cos((float) ang) * 0.25D, -MathHelper.sin((float) ang) * 0.1D, 0.01D, MathHelper.cos((float) ang) * 0.1, 0);
                TheBetweenlands.proxy.spawnCustomParticle("smoke", world, message.posX -MathHelper.sin((float) ang) * 0.25D, message.posY + 0.5D, message.posZ + MathHelper.cos((float) ang) * 0.25D, -MathHelper.sin((float) ang) * 0.1D, 0.01D, MathHelper.cos((float) ang) * 0.1, 0);
                TheBetweenlands.proxy.spawnCustomParticle("smoke", world, message.posX -MathHelper.sin((float) ang) * 0.25D, message.posY + 1D, message.posZ + MathHelper.cos((float) ang) * 0.25D, -MathHelper.sin((float) ang) * 0.1D, 0.01D, MathHelper.cos((float) ang) * 0.1, 0);
            }
        }
    }

    public void onServerMessage(MessageDruidTeleportParticle message, EntityPlayer player)
    {

    }

    public void fromBytes(ByteBuf buf)
    {
        posX = buf.readFloat();
        posY = buf.readFloat();
        posZ = buf.readFloat();
    }

    public void toBytes(ByteBuf buf)
    {
        buf.writeFloat(posX);
        buf.writeFloat(posY);
        buf.writeFloat(posZ);
    }
}
