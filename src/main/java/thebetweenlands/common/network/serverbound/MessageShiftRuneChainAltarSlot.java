package thebetweenlands.common.network.serverbound;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thebetweenlands.common.inventory.container.runechainaltar.ContainerRuneChainAltar;
import thebetweenlands.common.network.MessageBase;
import thebetweenlands.common.tile.TileEntityRuneChainAltar;

public class MessageShiftRuneChainAltarSlot extends MessageBase {
	private int slot;
	private boolean back;

	public MessageShiftRuneChainAltarSlot() { }

	public MessageShiftRuneChainAltarSlot(int slot, boolean back) {
		this.slot = slot;
		this.back = back;
	}

	@Override
	public void serialize(PacketBuffer buf) {
		buf.writeInt(this.slot);
		buf.writeBoolean(this.back);
	}

	@Override
	public void deserialize(PacketBuffer buf) {
		this.slot = buf.readInt();
		this.back = buf.readBoolean();
	}

	@Override
	public IMessage process(MessageContext ctx) {
		if(this.slot > TileEntityRuneChainAltar.NON_INPUT_SLOTS - 1 && ctx.getServerHandler() != null) {
			EntityPlayer player = ctx.getServerHandler().player;
			if(player.openContainer instanceof ContainerRuneChainAltar) {
				ContainerRuneChainAltar container = (ContainerRuneChainAltar) player.openContainer;
				if(this.slot < container.inventorySlots.size()) {
					container.shiftSlot(this.slot, this.back);
				}
			}
		}
		return null;
	}
}
