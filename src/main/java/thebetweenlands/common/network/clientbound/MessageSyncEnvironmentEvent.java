package thebetweenlands.common.network.clientbound;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.network.MessageBase;
import thebetweenlands.common.world.WorldProviderBetweenlands;
import thebetweenlands.common.world.event.EnvironmentEvent;
import thebetweenlands.common.world.event.EnvironmentEventRegistry;

public class MessageSyncEnvironmentEvent extends MessageBase {
	private EnvironmentEvent event;
	private String eventName;
	private boolean active;
	private PacketBuffer receivedBuffer;

	public MessageSyncEnvironmentEvent() {}

	public MessageSyncEnvironmentEvent(EnvironmentEvent eevent) {
		this.event = eevent;
		this.eventName = eevent.getEventName();
		this.active = eevent.isActive();
	}

	@Override
	public void serialize(PacketBuffer buffer) {
		buffer.writeString(this.eventName);
		buffer.writeBoolean(this.active);
		PacketBuffer pkt = new PacketBuffer(buffer);
		this.event.sendEventPacket(pkt);
	}

	@Override
	public void deserialize(PacketBuffer buffer) {
		this.receivedBuffer = buffer;
		this.eventName = buffer.readString(128);
		this.active = buffer.readBoolean();
	}

	@Override
	public IMessage process(MessageContext ctx) {
		if(ctx.side == Side.CLIENT) {
			this.handleMessage();
		}
		return null;
	}

	@SideOnly(Side.CLIENT)
	private void handleMessage() {
		World world = Minecraft.getMinecraft().world;
		if(world.provider instanceof WorldProviderBetweenlands) {
			WorldProviderBetweenlands provider = (WorldProviderBetweenlands)world.provider;
			EnvironmentEventRegistry eeRegistry = provider.getWorldData().getEnvironmentEventRegistry();
			EnvironmentEvent eevent = eeRegistry.forName(this.eventName);
			if(eevent != null) {
				eevent.loadEventPacket(new PacketBuffer(this.receivedBuffer));
				eevent.setActive(this.active, false);
				eevent.setLoaded();
			}
		}
	}
}
