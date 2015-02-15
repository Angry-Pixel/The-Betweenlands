package thebetweenlands.network.handler;

import net.minecraft.world.World;
import thebetweenlands.network.packet.AltarParticleMessage;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class AltarPacketHandler implements IMessageHandler<AltarParticleMessage, IMessage> {

	@Override
	@SideOnly(Side.CLIENT)
	public IMessage onMessage(AltarParticleMessage message, MessageContext ctx) {
		World world = FMLClientHandler.instance().getWorldClient();
		if (world == null)
			return null;
		else if (world.isRemote) {
			//these are the ints being sent
			System.out.println("X: " + message.posX + " Y: "+ message.posY + " Z: "+ message.posZ + " Counter: "+ message.craftingProgress);
			}
		return null;
	}
}