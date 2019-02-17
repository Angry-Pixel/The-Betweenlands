package thebetweenlands.common.network.clientbound;

import javax.xml.ws.handler.MessageContext;

import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import thebetweenlands.common.handler.AttackDamageHandler;
import thebetweenlands.common.network.MessageEntity;

public class MessagePowerRingParticles extends MessageEntity {
	public MessagePowerRingParticles() { }

	public MessagePowerRingParticles(Entity entity) {
		this.addEntity(entity);
	}

	@Override
	public IMessage process(MessageContext ctx) {
		super.process(ctx);

		if(ctx.side == Dist.CLIENT) {
			this.handle();
		}

		return null;
	}

	@OnlyIn(Dist.CLIENT)
	private void handle() {
		Entity entityHit = this.getEntity(0);
		if(entityHit != null) {
			AttackDamageHandler.spawnPowerRingParticles(entityHit);
		}
	}
}
