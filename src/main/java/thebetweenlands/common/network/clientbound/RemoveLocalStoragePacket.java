package thebetweenlands.common.network.clientbound;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thebetweenlands.api.storage.ILocalStorage;
import thebetweenlands.api.storage.ILocalStorageHandler;
import thebetweenlands.api.storage.IWorldStorage;
import thebetweenlands.api.storage.StorageID;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;

public record RemoveLocalStoragePacket(StorageID id) implements CustomPacketPayload {

	public static final Type<RemoveLocalStoragePacket> TYPE = new Type<>(TheBetweenlands.prefix("remove_local_storage"));

	public static final StreamCodec<RegistryFriendlyByteBuf, RemoveLocalStoragePacket> STREAM_CODEC = StreamCodec.composite(
		StorageID.STREAM_CODEC, RemoveLocalStoragePacket::id,
		RemoveLocalStoragePacket::new);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(RemoveLocalStoragePacket packet, IPayloadContext context) {
		IWorldStorage worldStorage = BetweenlandsWorldStorage.get(context.player().level());
		if (worldStorage != null) {
			ILocalStorageHandler localStorageHandler = worldStorage.getLocalStorageHandler();
			ILocalStorage loadedStorage = localStorageHandler.getLocalStorage(packet.id());
			if (loadedStorage != null) {
				localStorageHandler.removeLocalStorage(loadedStorage);
			}
		}
	}
}