package thebetweenlands.common.network.clientbound;

import java.io.IOException;
import java.util.List;

import javax.xml.ws.handler.MessageContext;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import thebetweenlands.api.environment.IEnvironmentEvent;
import thebetweenlands.api.network.IGenericDataManagerAccess;
import thebetweenlands.common.network.MessageBase;
import thebetweenlands.common.network.datamanager.GenericDataManager;
import thebetweenlands.common.world.event.BLEnvironmentEventRegistry;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;

public class MessageSyncEnvironmentEventData extends MessageBase {
	private ResourceLocation eventName;
	private List<IGenericDataManagerAccess.IDataEntry<?>> dataManagerEntries;

	public MessageSyncEnvironmentEventData() {}

	public MessageSyncEnvironmentEventData(IEnvironmentEvent eevent, boolean sendAll) {
		this.eventName = eevent.getEventName();
		IGenericDataManagerAccess dataManager = eevent.getDataManager();
		if (sendAll) {
			this.dataManagerEntries = dataManager.getAll();
			dataManager.setClean();
		} else {
			this.dataManagerEntries = dataManager.getDirty();
		}
	}

	@Override
	public void serialize(PacketBuffer buffer) throws IOException {
		buffer.writeString(this.eventName.toString());
		GenericDataManager.writeEntries(this.dataManagerEntries, buffer);
	}

	@Override
	public void deserialize(PacketBuffer buffer) throws IOException {
		this.eventName = new ResourceLocation(buffer.readString(128));
		this.dataManagerEntries = GenericDataManager.readEntries(buffer);
	}

	@Override
	public IMessage process(MessageContext ctx) {
		if(ctx.side == Dist.CLIENT) {
			this.handleMessage();
		}
		return null;
	}

	@OnlyIn(Dist.CLIENT)
	private void handleMessage() {
		if(this.eventName != null && this.dataManagerEntries != null) {
			World world = Minecraft.getInstance().world;
			if(world != null) {
				BetweenlandsWorldStorage storage = BetweenlandsWorldStorage.forWorld(world);
				if(storage != null) {
					BLEnvironmentEventRegistry eeRegistry = storage.getEnvironmentEventRegistry();
					IEnvironmentEvent eevent = eeRegistry.forName(this.eventName);
					if(eevent != null) {
						IGenericDataManagerAccess dataManager = eevent.getDataManager();
						if(dataManager != null) {
							dataManager.setValuesFromPacket(this.dataManagerEntries);
						}
						if(!eevent.isLoaded()) {
							eevent.setLoaded();
						}
					}
				}
			}
		}
	}
}
