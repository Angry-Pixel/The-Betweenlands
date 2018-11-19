package thebetweenlands.common.network.serverbound;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thebetweenlands.common.inventory.container.runechainaltar.ContainerRuneChainAltar;
import thebetweenlands.common.network.MessageBase;

public class MessageUnlinkRuneChainAltarRune extends MessageBase {
	private int inputRune, input;

	public MessageUnlinkRuneChainAltarRune() { }

	public MessageUnlinkRuneChainAltarRune(int inputRune, int input) {
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
			if(player.openContainer instanceof ContainerRuneChainAltar) {
				ContainerRuneChainAltar container = (ContainerRuneChainAltar) player.openContainer;
				container.unlink(this.inputRune, this.input);
			}
		}
		return null;
	}
}
