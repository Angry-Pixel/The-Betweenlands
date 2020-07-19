package thebetweenlands.common.network.serverbound;

import java.io.IOException;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thebetweenlands.common.entity.draeton.EntityDraeton;
import thebetweenlands.common.network.MessageEntity;

public class MessagePurgeDraetonBurner extends MessageEntity {
	public MessagePurgeDraetonBurner() {

	}

	public MessagePurgeDraetonBurner(EntityDraeton carriage) {
		this.addEntity(carriage);
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

		EntityPlayer player = ctx.getServerHandler().player;

		Entity entity = this.getEntity(0);
		if(entity instanceof EntityDraeton) {
			EntityDraeton carriage = (EntityDraeton) entity;

			if(carriage.getControllingPassenger() == player) {
				carriage.setBurnerFuel(0);
			}
		}

		return null;
	}
}
