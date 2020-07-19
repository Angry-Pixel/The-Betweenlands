package thebetweenlands.common.network.serverbound;

import java.io.IOException;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import thebetweenlands.common.entity.mobs.EntityChiromawTame;
import thebetweenlands.common.network.MessageEntity;

public class MessageChiromawDoubleJump extends MessageEntity {
	public MessageChiromawDoubleJump() {
		
	}
	
	public MessageChiromawDoubleJump(EntityChiromawTame chiromaw) {
		this.addEntity(chiromaw);
	}
	
	@Override
	public void serialize(PacketBuffer buf) {
		super.serialize(buf);
	}

	@Override
	public void deserialize(PacketBuffer buf) throws IOException {
		super.deserialize(buf);
	}

	@Override
	public IMessage process(MessageContext ctx) {
		super.process(ctx);

		if(ctx.side == Side.SERVER) {
			Entity entity = this.getEntity(0);

			if(entity instanceof EntityChiromawTame) {
				EntityPlayer player = ctx.getServerHandler().player;

				if(player.getPassengers().contains(entity)) {
					((EntityChiromawTame) entity).performDoubleJump(player);
				}
			}
		}

		return null;
	}
}
