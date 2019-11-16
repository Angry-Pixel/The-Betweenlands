package thebetweenlands.common.network.serverbound;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thebetweenlands.common.inventory.container.runechainaltar.ContainerRuneChainAltar;
import thebetweenlands.common.network.MessageBase;

public class MessageSetRuneChainAltarPage extends MessageBase {
	private int page;

	public MessageSetRuneChainAltarPage() { }

	public MessageSetRuneChainAltarPage(int page) {
		this.page = page;
	}

	@Override
	public void serialize(PacketBuffer buf) {
		buf.writeInt(this.page);
	}

	@Override
	public void deserialize(PacketBuffer buf) {
		this.page = buf.readInt();
	}

	@Override
	public IMessage process(MessageContext ctx) {
		if(this.page >= 0 && ctx.getServerHandler() != null) {
			EntityPlayer player = ctx.getServerHandler().player;
			if(player.openContainer instanceof ContainerRuneChainAltar) {
				ContainerRuneChainAltar container = (ContainerRuneChainAltar) player.openContainer;
				if(this.page < container.getPages()) {
					container.setCurrentPage(this.page);
				}
			}
		}
		return null;
	}
}
