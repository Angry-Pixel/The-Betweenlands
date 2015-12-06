package thebetweenlands.network.message;

import java.io.IOException;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import thebetweenlands.herblore.aspects.AspectRecipes;
import thebetweenlands.network.message.base.AbstractMessage;

public class MessageLoadAspects extends AbstractMessage<MessageLoadAspects> {
	public NBTTagCompound aspectData;

	public MessageLoadAspects() { }

	public MessageLoadAspects(NBTTagCompound aspectData) {
		this.aspectData = aspectData;
	}

	public void onMessageClientSide(MessageLoadAspects message, EntityPlayer player) {
		AspectRecipes.REGISTRY.loadStaticAspects(message.aspectData);
	}

	public void onMessageServerSide(MessageLoadAspects message, EntityPlayer player) { }

	public void fromBytes(ByteBuf buf) {
		PacketBuffer packetBuffer = new PacketBuffer(buf);
		try {
			this.aspectData = packetBuffer.readNBTTagCompoundFromBuffer();
		} catch (IOException e) {
			//System.err.println("Failed loading aspects from packet");
			e.printStackTrace();
		}
	}

	public void toBytes(ByteBuf buf) {
		PacketBuffer packetBuffer = new PacketBuffer(buf);
		try {
			packetBuffer.writeNBTTagCompoundToBuffer(this.aspectData);
		} catch (IOException e) {
			//System.err.println("Failed writing aspects to packet");
			e.printStackTrace();
		}
	}
}
