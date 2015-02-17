package thebetweenlands.network.handler;

import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.network.packet.DruidTeleportParticleMessage;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class DruidPacketHandler implements IMessageHandler<DruidTeleportParticleMessage, IMessage> {

	@Override
	@SideOnly(Side.CLIENT)
	public IMessage onMessage(DruidTeleportParticleMessage message, MessageContext ctx) {
		World world = FMLClientHandler.instance().getWorldClient();
		if (world == null)
			return null;
		else if (world.isRemote)
			for (int a = 0; a < 360; a += 4) {
				double ang = a * Math.PI / 180D;
				TheBetweenlands.proxy.spawnCustomParticle("smoke", world, message.posX + 0.5D -MathHelper.sin((float) ang) * 1.5, message.posY + 0.5D, message.posZ + 0.5D + MathHelper.cos((float) ang) * 1.5,  0, 0D, 0);
				TheBetweenlands.proxy.spawnCustomParticle("smoke", world, message.posX + 0.5D -MathHelper.sin((float) ang) * 1.5, message.posY + 0.5D + MathHelper.cos((float) ang) * 1.5, message.posZ + 0.5D, 0, 0D, 0);
				TheBetweenlands.proxy.spawnCustomParticle("smoke", world, message.posX + 0.5D, message.posY + 0.5D -MathHelper.sin((float) ang) * 1.5 , message.posZ + 0.5D + MathHelper.cos((float) ang) * 1.5,  0, 0D, 0);
			}
		return null;
	}
}