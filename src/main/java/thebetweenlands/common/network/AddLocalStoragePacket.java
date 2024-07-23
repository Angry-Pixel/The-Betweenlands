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

import javax.annotation.Nullable;

public class AddLocalStoragePacket implements CustomPacketPayload {

	private final ResourceLocation type;
	@Nullable
	private final CompoundTag idTag;
	@Nullable
	private final CompoundTag tag;
	@Nullable
	private List<IGenericDataAccessorAccess.IDataEntry<?>> dataManagerEntries;


	public static final Type<AddLocalStoragePacket> TYPE = new Type<>(TheBetweenlands.prefix("add_local_storage"));

	public static final StreamCodec<RegistryFriendlyByteBuf, AddLocalStoragePacket> STREAM_CODEC = CustomPacketPayload.codec(AddLocalStoragePacket::write, AddLocalStoragePacket::new);

	public AddLocalStoragePacket(ILocalStorage localStorage) {
		this.type = StorageRegistry.getStorageId(localStorage.getClass());
		localStorage.getID().writeToNBT(this.idTag = new CompoundTag());
		IGenericDataAccessorAccess dataManager = localStorage.getDataManager();
		if (dataManager != null) {
			this.dataManagerEntries = dataManager.getAll();
		}
		localStorage.writeInitialPacket(this.tag = new CompoundTag());
	}

	public AddLocalStoragePacket(RegistryFriendlyByteBuf buf) {
		this.type = buf.readResourceLocation();
		this.idTag = buf.readNbt();
		this.tag = buf.readNbt();

		if (buf.readBoolean()) {
			this.dataManagerEntries = GenericDataAccessor.readEntries(buf);
		}
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public void write(RegistryFriendlyByteBuf buf) {
		buf.writeResourceLocation(this.type);

		buf.writeNbt(this.idTag);

		buf.writeNbt(this.tag);

		buf.writeBoolean(this.dataManagerEntries != null);
		if (this.dataManagerEntries != null) {
			GenericDataAccessor.writeEntries(this.dataManagerEntries, buf);
		}
	}

	public static void handle(AddLocalStoragePacket packet, IPayloadContext context) {
		context.enqueueWork(() -> {
			Level level = context.player().level();
			StorageID id = StorageID.readFromNBT(packet.idTag);

			IWorldStorage worldStorage = BetweenlandsWorldStorage.get(level);
			if (worldStorage != null) {
				ILocalStorageHandler storageHandler = worldStorage.getLocalStorageHandler();

				ILocalStorage loadedStorage = storageHandler.getLocalStorage(id);
				if (loadedStorage != null) {
					storageHandler.removeLocalStorage(loadedStorage);
				}

				ILocalStorage newStorage = storageHandler.createLocalStorage(packet.type, id, null);

				newStorage.readInitialPacket(packet.tag);

				if (packet.dataManagerEntries != null) {
					IGenericDataAccessorAccess dataManager = newStorage.getDataManager();
					if (dataManager != null) {
						dataManager.setValuesFromPacket(packet.dataManagerEntries);
					}
				}

				storageHandler.addLocalStorage(newStorage);
			}
		});
	}
}