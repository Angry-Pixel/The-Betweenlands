package thebetweenlands.common.network.serverbound;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thebetweenlands.common.entity.EntityGalleryFrame;
import thebetweenlands.common.network.MessageBase;

public class MessageSetGalleryUrl extends MessageBase {
	private int entityId;
	private String url;

	public MessageSetGalleryUrl() {

	}

	public MessageSetGalleryUrl(EntityGalleryFrame frame, String url) {
		this.entityId = frame.getEntityId();
		this.url = url;
	}

	@Override
	public void serialize(PacketBuffer buf) {
		buf.writeVarInt(this.entityId);
		buf.writeString(this.url);
	}

	@Override
	public void deserialize(PacketBuffer buf) {
		this.entityId = buf.readVarInt();
		this.url = buf.readString(256);
	}

	@Override
	public IMessage process(MessageContext ctx) {
		if(ctx.getServerHandler() != null) {
			EntityPlayer player = ctx.getServerHandler().player;
			Entity targetEntity = player.world.getEntityByID(this.entityId);
			if(targetEntity instanceof EntityGalleryFrame && player.getDistance(targetEntity) < 6.0D && this.url.length() > 0 && this.url.length() <= 256) {
				((EntityGalleryFrame) targetEntity).setUrl(this.url);
			}
		}
		return null;
	}
}
