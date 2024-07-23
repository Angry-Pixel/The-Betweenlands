package thebetweenlands.common.network;

import java.util.List;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thebetweenlands.api.network.IGenericDataAccessorAccess;
import thebetweenlands.api.storage.ILocalStorage;
import thebetweenlands.api.storage.ILocalStorageHandler;
import thebetweenlands.api.storage.IWorldStorage;
import thebetweenlands.api.storage.StorageID;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.network.datamanager.GenericDataAccessor;
import thebetweenlands.common.registries.StorageRegistry;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.WorldStorageImpl;

public class SyncLocalStorageDataPacket implements CustomPacketPayload {

	private final ResourceLocation type;
	private final CompoundTag idNbt;
	private final List<IGenericDataAccessorAccess.IDataEntry<?>> dataManagerEntries;

	public static final Type<SyncLocalStorageDataPacket> TYPE = new Type<>(TheBetweenlands.prefix("sync_local_storage_data"));
	public static final StreamCodec<RegistryFriendlyByteBuf, SyncLocalStorageDataPacket> STREAM_CODEC = CustomPacketPayload.codec(SyncLocalStorageDataPacket::write, SyncLocalStorageDataPacket::new);

	public SyncLocalStorageDataPacket(ILocalStorage localStorage, boolean sendAll) {
		this.type = StorageRegistry.getStorageId(localStorage.getClass());
		localStorage.getID().writeToNBT(this.idNbt = new CompoundTag());
		IGenericDataAccessorAccess dataManager = localStorage.getDataManager();
		if (sendAll) {
			this.dataManagerEntries = dataManager.getAll();
			dataManager.setClean();
		} else {
			this.dataManagerEntries = dataManager.getDirty();
		}
	}

	public SyncLocalStorageDataPacket(RegistryFriendlyByteBuf buf) {
		this.type = buf.readResourceLocation();
		this.idNbt = buf.readNbt();
		this.dataManagerEntries = GenericDataAccessor.readEntries(buf);
	}

	public void write(RegistryFriendlyByteBuf buf) {
		buf.writeResourceLocation(this.type);
		buf.writeNbt(this.idNbt);
		GenericDataAccessor.writeEntries(this.dataManagerEntries, buf);
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(SyncLocalStorageDataPacket packet, IPayloadContext context) {
		context.enqueueWork(() -> {
			Level level = context.player().level();
			StorageID id = StorageID.readFromNBT(packet.idNbt);

			IWorldStorage worldStorage = BetweenlandsWorldStorage.get(level);
			if (worldStorage != null) {
				ILocalStorageHandler storageHandler = worldStorage.getLocalStorageHandler();

				ILocalStorage storage = storageHandler.getLocalStorage(id);

				if (storage != null && storage.getClass() == StorageRegistry.getStorageType(packet.type)) {
					IGenericDataAccessorAccess dataManager = storage.getDataManager();
					if (dataManager != null) {
						dataManager.setValuesFromPacket(packet.dataManagerEntries);
					}
				}
			}
		});
	}
}