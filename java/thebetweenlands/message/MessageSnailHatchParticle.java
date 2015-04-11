package thebetweenlands.message;

import cpw.mods.fml.client.FMLClientHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityBreakingFX;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.message.base.AbstractMessage;

public class MessageSnailHatchParticle extends AbstractMessage<MessageSnailHatchParticle>
{
    public float posX;
    public float posY;
    public float posZ;

    public MessageSnailHatchParticle()
    {

    }

    public MessageSnailHatchParticle(float x, float y, float z)
    {
        posX = x;
        posY = y;
        posZ = z;
    }

    public void onClientMessage(MessageSnailHatchParticle message, EntityPlayer player)
    {
        World world = FMLClientHandler.instance().getWorldClient();
        EffectRenderer eff = Minecraft.getMinecraft().effectRenderer;
        if (world != null && world.isRemote)
        {
        	for (int count = 0; count <= 50; ++count)
        		eff.addEffect(new EntityBreakingFX(world, message.posX + (world.rand.nextDouble() - 0.5D) * 0.35F, message.posY + world.rand.nextDouble() * 0.175F, message.posZ + (world.rand.nextDouble() - 0.5D) * 0.35F, Items.slime_ball));
        }
    }

    public void onServerMessage(MessageSnailHatchParticle message, EntityPlayer player)
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
