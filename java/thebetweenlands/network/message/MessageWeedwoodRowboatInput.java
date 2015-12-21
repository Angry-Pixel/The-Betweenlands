package thebetweenlands.network.message;

import thebetweenlands.entities.rowboat.EntityWeedwoodRowboat;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageWeedwoodRowboatInput implements IMessage, IMessageHandler<MessageWeedwoodRowboatInput, IMessage> {
	private boolean oarStrokeLeft;

	private boolean oarStrokeRight;

	public MessageWeedwoodRowboatInput() {}

	public MessageWeedwoodRowboatInput(boolean oarStrokeLeft, boolean oarStrokeRight) {
		this.oarStrokeLeft = oarStrokeLeft;
		this.oarStrokeRight = oarStrokeRight;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		byte bitset = 0;
		bitset |= oarStrokeLeft ? 1 : 0;
		bitset |= oarStrokeRight ? 2 : 0;
		buf.writeByte(bitset);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		byte bitset = buf.readByte();
		oarStrokeLeft = (bitset & 1) == 1;
		oarStrokeRight = (bitset & 2) == 2;
	}

	@Override
	public IMessage onMessage(MessageWeedwoodRowboatInput message, MessageContext ctx) {
		return message.onMessage(ctx);
	}

	private IMessage onMessage(MessageContext ctx) {
		EntityPlayer player = ctx.getServerHandler().playerEntity;
		if (player.ridingEntity instanceof EntityWeedwoodRowboat) {
			EntityWeedwoodRowboat rowboat = ((EntityWeedwoodRowboat) player.ridingEntity); 
			rowboat.updateControls(oarStrokeLeft, oarStrokeRight);
		}
		return null;
	}
}
