package thebetweenlands.network.message;

import io.netty.buffer.ByteBuf;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import net.minecraft.entity.player.EntityPlayer;
import thebetweenlands.network.message.base.AbstractMessage;
import thebetweenlands.world.events.EnvironmentEvent;
import thebetweenlands.world.events.EnvironmentEventRegistry;

public class MessageSyncEnvironmentEvent extends AbstractMessage<MessageSyncEnvironmentEvent> {
	private EnvironmentEvent event;
	private String eventName;
	private boolean active;
	
	public MessageSyncEnvironmentEvent() {}
	
	public MessageSyncEnvironmentEvent(EnvironmentEvent eevent) {
		this.event = eevent;
		this.eventName = eevent.getEventName();
		this.active = eevent.isActive();
	}
	
	@Override
	public void fromBytes(ByteBuf buffer) {
		int strLen = buffer.readInt();
		byte[] strBytes = new byte[strLen];
		buffer.readBytes(strBytes);
		String eventName;
		try {
			eventName = new String(strBytes, "UTF-8");
			this.eventName = eventName;
			this.active = buffer.readBoolean();
			EnvironmentEvent eevent = EnvironmentEventRegistry.forName(this.eventName);
			if(eevent != null) {
				eevent.loadEventPacket(buffer);
			}
		} catch (UnsupportedEncodingException e) {
			System.err.println("Failed to sync environment event");
			e.printStackTrace();
		}
	}

	@Override
	public void toBytes(ByteBuf buffer) {
		byte[] strBytes = this.eventName.getBytes(Charset.forName("UTF-8"));
		buffer.writeInt(strBytes.length);
		buffer.writeBytes(strBytes);
		buffer.writeBoolean(this.active);
		this.event.sendEventPacket(buffer);
	}

	@Override
	public void onClientMessage(MessageSyncEnvironmentEvent message, EntityPlayer player) {
		EnvironmentEvent eevent = EnvironmentEventRegistry.forName(this.eventName);
		if(eevent != null) {
			eevent.setActive(this.active);
		}
	}

	@Override
	public void onServerMessage(MessageSyncEnvironmentEvent message, EntityPlayer player) { }
}
