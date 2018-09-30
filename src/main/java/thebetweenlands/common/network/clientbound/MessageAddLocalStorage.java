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

public class MessageAddLocalStorage extends MessageBase {
	private ResourceLocation type;
	private NBTTagCompound idNbt;
	private NBTTagCompound nbt;
	private List<IGenericDataManagerAccess.IDataEntry<?>> dataManagerEntries;

	public MessageAddLocalStorage() {}

	public MessageAddLocalStorage(ILocalStorage localStorage) {
		this.type = StorageRegistry.getStorageId(localStorage.getClass());

		localStorage.getID().writeToNBT(this.idNbt = new NBTTagCompound());

		IGenericDataManagerAccess dataManager = localStorage.getDataManager();
		if(dataManager != null) {
			this.dataManagerEntries = dataManager.getAll();
		}

		localStorage.writeInitialPacket(this.nbt = new NBTTagCompound());
	}

	@Override
	public void serialize(PacketBuffer buf) throws IOException {
		buf.writeString(this.type.toString());

		buf.writeCompoundTag(this.idNbt);

		buf.writeCompoundTag(this.nbt);

		buf.writeBoolean(this.dataManagerEntries != null);
		if(this.dataManagerEntries != null) {
			GenericDataManager.writeEntries(this.dataManagerEntries, buf);
		}
	}

	@Override
	public void deserialize(PacketBuffer buf) throws IOException {
		this.type = new ResourceLocation(buf.readString(128));

		this.idNbt = buf.readCompoundTag();

		this.nbt = buf.readCompoundTag();

		if(buf.readBoolean()) {
			this.dataManagerEntries = GenericDataManager.readEntries(buf);
		}
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
		if(world != null) {
			StorageID id = StorageID.readFromNBT(this.idNbt);

			IWorldStorage worldStorage = WorldStorageImpl.getCapability(world);
			ILocalStorageHandler storageHandler = worldStorage.getLocalStorageHandler();

			ILocalStorage loadedStorage = storageHandler.getLocalStorage(id);
			if(loadedStorage != null) {
				storageHandler.removeLocalStorage(loadedStorage);
			}

			ILocalStorage newStorage = storageHandler.createLocalStorage(this.type, id, null);

			newStorage.readInitialPacket(this.nbt);

			if(this.dataManagerEntries != null) {
				IGenericDataManagerAccess dataManager = newStorage.getDataManager();
				if(dataManager != null) {
					dataManager.setValuesFromPacket(this.dataManagerEntries);
				}
			}

			storageHandler.addLocalStorage(newStorage);
		}
	}
}