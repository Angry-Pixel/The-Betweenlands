package thebetweenlands.common.network.serverbound;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thebetweenlands.common.inventory.container.runeweavingtable.ContainerRuneWeavingTable;
import thebetweenlands.common.network.MessageBase;

public class MessageLinkRuneWeavingTableRune extends MessageBase {
	private int inputRune, input, outputRune, output;

	public MessageLinkRuneWeavingTableRune() { }

	public MessageLinkRuneWeavingTableRune(int inputRune, int input, int outputRune, int output) {
		this.inputRune = inputRune;
		this.input = input;
		this.outputRune = outputRune;
		this.output = output;
	}

	@Override
	public void serialize(PacketBuffer buf) throws IOException {
		buf.writeVarInt(this.inputRune);
		buf.writeVarInt(this.input);
		buf.writeVarInt(this.outputRune);
		buf.writeVarInt(this.output);
	}

	@Override
	public void deserialize(PacketBuffer buf) throws IOException {
		this.inputRune = buf.readVarInt();
		this.input = buf.readVarInt();
		this.outputRune = buf.readVarInt();
		this.output = buf.readVarInt();
	}

	@Override
	public IMessage process(MessageContext ctx) {
		if(this.inputRune > 0 && this.input >= 0 && this.outputRune < this.inputRune && this.outputRune >= 0 && this.output >= 0 && ctx.getServerHandler() != null) {
			EntityPlayer player = ctx.getServerHandler().player;
			if(player.openContainer instanceof ContainerRuneWeavingTable) {
				ContainerRuneWeavingTable container = (ContainerRuneWeavingTable) player.openContainer;
				container.link(this.inputRune, this.input, this.outputRune, this.output);
			}
		}
		return null;
	}
}
