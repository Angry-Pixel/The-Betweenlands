package thebetweenlands.common.network.serverbound;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thebetweenlands.common.inventory.container.ContainerFishTrimmingTable;
import thebetweenlands.common.network.MessageBase;

public class MessageButcherFish extends MessageBase {
	public MessageButcherFish() { }

	@Override
	public void serialize(PacketBuffer buf) { }

	@Override
	public void deserialize(PacketBuffer buf) { }

	@Override
	public IMessage process(MessageContext ctx) {
		if(ctx.getServerHandler() != null) {
			final EntityPlayerMP player = ctx.getServerHandler().player;

			Container container = player.openContainer;

			if(container instanceof ContainerFishTrimmingTable) {
				((ContainerFishTrimmingTable) container).chop(player);
			}
		}
		return null;
	}
}
