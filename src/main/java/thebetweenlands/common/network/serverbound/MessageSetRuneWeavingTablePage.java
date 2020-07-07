package thebetweenlands.common.network.serverbound;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thebetweenlands.common.inventory.container.runeweavingtable.ContainerRuneWeavingTable;
import thebetweenlands.common.network.MessageBase;

public class MessageSetRuneWeavingTablePage extends MessageBase {
	private int page;

	public MessageSetRuneWeavingTablePage() { }

	public MessageSetRuneWeavingTablePage(int page) {
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
			if(player.openContainer instanceof ContainerRuneWeavingTable) {
				ContainerRuneWeavingTable container = (ContainerRuneWeavingTable) player.openContainer;
				if(this.page < container.getPages()) {
					container.setCurrentPage(this.page);
				}
			}
		}
		return null;
	}
}
