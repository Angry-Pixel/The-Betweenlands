package thebetweenlands.common.network.serverbound;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thebetweenlands.common.inventory.container.runeweavingtable.ContainerRuneWeavingTable;
import thebetweenlands.common.network.MessageBase;

public class MessageUnlinkRuneWeavingTableRune extends MessageBase {
	private int inputRune, input;

	public MessageUnlinkRuneWeavingTableRune() { }

	public MessageUnlinkRuneWeavingTableRune(int inputRune, int input) {
		this.inputRune = inputRune;
		this.input = input;
	}

	@Override
	public void serialize(PacketBuffer buf) throws IOException {
		buf.writeVarInt(this.inputRune);
		buf.writeVarInt(this.input);
	}

	@Override
	public void deserialize(PacketBuffer buf) throws IOException {
		this.inputRune = buf.readVarInt();
		this.input = buf.readVarInt();
	}

	@Override
	public IMessage process(MessageContext ctx) {
		if(this.inputRune >= 0 && this.input >= 0 && ctx.getServerHandler() != null) {
			EntityPlayer player = ctx.getServerHandler().player;
			if(player.openContainer instanceof ContainerRuneWeavingTable) {
				ContainerRuneWeavingTable container = (ContainerRuneWeavingTable) player.openContainer;
				container.unlink(this.inputRune, this.input);
			}
		}
		return null;
	}
}
