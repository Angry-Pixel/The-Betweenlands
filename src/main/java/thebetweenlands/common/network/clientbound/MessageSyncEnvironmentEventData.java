package thebetweenlands.common.network.clientbound;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.environment.IEnvironmentEvent;
import thebetweenlands.api.network.IGenericDataManagerAccess;
import thebetweenlands.common.TheBetweenlands;
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
		TheBetweenlands.logger.info("------- Sync Write --------");
		
		buffer.writeString(this.eventName.toString());
		
		TheBetweenlands.logger.info("Event: " + this.eventName.toString());
		
		GenericDataManager.writeEntries(this.dataManagerEntries, buffer);
		
		TheBetweenlands.logger.info("------- End Sync Write --------");
	}

	@Override
	public void deserialize(PacketBuffer buffer) throws IOException {
		TheBetweenlands.logger.info("------- Sync Read --------");
		
		this.eventName = new ResourceLocation(buffer.readString(128));
		
		TheBetweenlands.logger.info("Event: " + this.eventName);
		
		this.dataManagerEntries = GenericDataManager.readEntries(buffer);
		
		TheBetweenlands.logger.info("------- End Sync Read --------");
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
		if(this.eventName != null && this.dataManagerEntries != null) {
			World world = Minecraft.getMinecraft().world;
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
