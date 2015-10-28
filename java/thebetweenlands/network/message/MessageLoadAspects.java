package thebetweenlands.network.message;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import thebetweenlands.herblore.aspects.AspectRecipes;
import thebetweenlands.network.message.base.AbstractMessage;

public class MessageLoadAspects extends AbstractMessage<MessageLoadAspects> {
	public long aspectsSeed;

	public MessageLoadAspects() { }

	public MessageLoadAspects(long seed) {
		this.aspectsSeed = seed;
	}

	public void onClientMessage(MessageLoadAspects message, EntityPlayer player) {
		AspectRecipes.REGISTRY.loadAspects(message.aspectsSeed);
	}

	public void onServerMessage(MessageLoadAspects message, EntityPlayer player) { }

	public void fromBytes(ByteBuf buf) {
		this.aspectsSeed = buf.readLong();
	}

	public void toBytes(ByteBuf buf) {
		buf.writeLong(this.aspectsSeed);
	}
}
