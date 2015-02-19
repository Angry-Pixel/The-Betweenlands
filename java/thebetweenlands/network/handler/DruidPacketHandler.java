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
				TheBetweenlands.proxy.spawnCustomParticle("smoke", world, message.posX -MathHelper.sin((float) ang) * 0.25D, message.posY, message.posZ + MathHelper.cos((float) ang) * 0.25D, -MathHelper.sin((float) ang) * 0.1D, 0.01D, MathHelper.cos((float) ang) * 0.1, 0);
				TheBetweenlands.proxy.spawnCustomParticle("smoke", world, message.posX -MathHelper.sin((float) ang) * 0.25D, message.posY + 0.5D, message.posZ + MathHelper.cos((float) ang) * 0.25D, -MathHelper.sin((float) ang) * 0.1D, 0.01D, MathHelper.cos((float) ang) * 0.1, 0);
				TheBetweenlands.proxy.spawnCustomParticle("smoke", world, message.posX -MathHelper.sin((float) ang) * 0.25D, message.posY + 1D, message.posZ + MathHelper.cos((float) ang) * 0.25D, -MathHelper.sin((float) ang) * 0.1D, 0.01D, MathHelper.cos((float) ang) * 0.1, 0);
			
			}
		return null;
	}
}