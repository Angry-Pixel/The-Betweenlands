package thebetweenlands.message;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import thebetweenlands.message.base.AbstractMessage;

public class MessageSyncWeather extends AbstractMessage<MessageSyncWeather> {
	public static boolean hasDenseFog = false;
	
	private boolean denseFog;
	
	public MessageSyncWeather() {}
	
	public MessageSyncWeather(boolean denseFog) {
		this.denseFog = denseFog;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		hasDenseFog = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeBoolean(this.denseFog);
	}

	@Override
	public void onClientMessage(MessageSyncWeather message, EntityPlayer player) {
		
	}

	@Override
	public void onServerMessage(MessageSyncWeather message, EntityPlayer player) {
		
	}
}
