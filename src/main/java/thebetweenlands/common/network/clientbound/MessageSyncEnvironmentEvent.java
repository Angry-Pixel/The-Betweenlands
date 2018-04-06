package thebetweenlands.common.network.clientbound;

import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.environment.IEnvironmentEvent;
import thebetweenlands.common.network.MessageBase;
import thebetweenlands.common.world.event.BLEnvironmentEventRegistry;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;

public class MessageSyncEnvironmentEvent extends MessageBase {
	private IEnvironmentEvent event;
	private ResourceLocation eventName;
	private boolean active;
	private NBTTagCompound nbt;

	public MessageSyncEnvironmentEvent() {}

	public MessageSyncEnvironmentEvent(IEnvironmentEvent eevent) {
		this.event = eevent;
		this.eventName = eevent.getEventName();
		this.active = eevent.isActive();
		this.nbt = new NBTTagCompound();
		eevent.sendEventPacket(this.nbt);
	}

	@Override
	public void serialize(PacketBuffer buffer) {
		buffer.writeString(this.eventName.toString());
		buffer.writeBoolean(this.active);
		buffer.writeCompoundTag(this.nbt);
	}

	@Override
	public void deserialize(PacketBuffer buffer) {
		this.eventName = new ResourceLocation(buffer.readString(128));
		this.active = buffer.readBoolean();
		try {
			this.nbt = buffer.readCompoundTag();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
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
		if(this.nbt != null) {
			World world = Minecraft.getMinecraft().world;
			if(world != null) {
				BetweenlandsWorldStorage storage = BetweenlandsWorldStorage.forWorld(world);
				if(storage != null) {
					BLEnvironmentEventRegistry eeRegistry = storage.getEnvironmentEventRegistry();
					IEnvironmentEvent eevent = eeRegistry.forName(this.eventName);
					if(eevent != null) {
						eevent.loadEventPacket(this.nbt);
						eevent.setActive(this.active, false);
						eevent.setLoaded();
					}
				}
			}
		}
	}
}
