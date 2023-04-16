package thebetweenlands.common.network.clientbound;

import java.io.IOException;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.network.IGenericDataManagerAccess;
import thebetweenlands.api.storage.ILocalStorage;
import thebetweenlands.api.storage.ILocalStorageHandler;
import thebetweenlands.api.storage.IWorldStorage;
import thebetweenlands.api.storage.StorageID;
import thebetweenlands.common.network.MessageBase;
import thebetweenlands.common.network.datamanager.GenericDataManager;
import thebetweenlands.common.registries.StorageRegistry;
import thebetweenlands.common.world.storage.WorldStorageImpl;

public class MessageSyncLocalStorageData extends MessageBase {
	private ResourceLocation type;
	private NBTTagCompound idNbt;
	private List<IGenericDataManagerAccess.IDataEntry<?>> dataManagerEntries;

	public MessageSyncLocalStorageData() {}

	public MessageSyncLocalStorageData(ILocalStorage localStorage, boolean sendAll) {
		this.type = StorageRegistry.getStorageId(localStorage.getClass());
		localStorage.getID().writeToNBT(this.idNbt = new NBTTagCompound());
		IGenericDataManagerAccess dataManager = localStorage.getDataManager();
		if (sendAll) {
			this.dataManagerEntries = dataManager.getAll();
			dataManager.setClean();
		} else {
			this.dataManagerEntries = dataManager.getDirty();
		}
	}

	@Override
	public void serialize(PacketBuffer buf) throws IOException {
		buf.writeString(this.type.toString());
		buf.writeCompoundTag(this.idNbt);
		GenericDataManager.writeEntries(this.dataManagerEntries, buf);
	}

	@Override
	public void deserialize(PacketBuffer buf) throws IOException {
		this.type = new ResourceLocation(buf.readString(128));
		this.idNbt = buf.readCompoundTag();
		this.dataManagerEntries = GenericDataManager.readEntries(buf);
	}

	@Override
	public IMessage process(MessageContext ctx) {
		if(ctx.side == Side.CLIENT) {
			this.handle();
		}
		return null;
	}

	@SideOnly(Side.CLIENT)
	private void handle() {
		World world = Minecraft.getMinecraft().world;
		if(world != null && this.dataManagerEntries != null) {
			StorageID id = StorageID.readFromNBT(this.idNbt);

			IWorldStorage worldStorage = WorldStorageImpl.getCapability(world);
			ILocalStorageHandler storageHandler = worldStorage.getLocalStorageHandler();

			ILocalStorage storage = storageHandler.getLocalStorage(id);

			if(storage != null && storage.getClass() == StorageRegistry.getStorageType(this.type)) {
				IGenericDataManagerAccess dataManager = storage.getDataManager();
				if(dataManager != null) {
					dataManager.setValuesFromPacket(this.dataManagerEntries);
				}
			}
		}
	}
}