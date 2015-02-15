package thebetweenlands.network.handler;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import thebetweenlands.network.packet.AltarParticleMessage;
import thebetweenlands.tileentities.TileEntityDruidAltar;
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
			TileEntity te = world.getTileEntity((int)message.posX, (int)message.posY, (int)message.posZ);
			if(te instanceof TileEntityDruidAltar) {
				TileEntityDruidAltar teda = (TileEntityDruidAltar) te;
				if(message.craftingProgress == -1) {
					world.playSound((int)message.posX, (int)message.posY, (int)message.posZ, "thebetweenlands:druidchant", 1.0F, 1.0F, false);
				} else {
					((TileEntityDruidAltar) te).craftingProgress = message.craftingProgress;
				}
			}
		}
		return null;
	}
}