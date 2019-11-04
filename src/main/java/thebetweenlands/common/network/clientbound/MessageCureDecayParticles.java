package thebetweenlands.common.network.clientbound;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemDye;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.network.MessageBase;

public class MessageCureDecayParticles extends MessageBase {
	private BlockPos pos;

	public MessageCureDecayParticles() { }

	public MessageCureDecayParticles(BlockPos pos) {
		this.pos = pos;
	}

	@Override
	public void deserialize(PacketBuffer buf) {
		this.pos = buf.readBlockPos();
	}

	@Override
	public void serialize(PacketBuffer buf) {
		buf.writeBlockPos(this.pos);
	}

	@Override
	public IMessage process(MessageContext ctx) {
		if(ctx.side == Side.CLIENT) {
			this.handle();
		}

		return null;
	}

	@SideOnly(Side.CLIENT)
	private void handle() {
		if(Minecraft.getMinecraft().world != null) {
			ItemDye.spawnBonemealParticles(Minecraft.getMinecraft().world, this.pos, 6);
		}
	}
}
