package thebetweenlands.common.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thebetweenlands.api.storage.ILocalStorage;
import thebetweenlands.api.storage.StorageID;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.location.LocationGuarded;
import thebetweenlands.common.world.storage.location.LocationStorage;

public record ClearBlockGuardPacket(String id) implements CustomPacketPayload {

	public static final Type<ClearBlockGuardPacket> TYPE = new Type<>(TheBetweenlands.prefix("clear_block_guard"));
	public static final StreamCodec<RegistryFriendlyByteBuf, ClearBlockGuardPacket> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.STRING_UTF8, ClearBlockGuardPacket::id, ClearBlockGuardPacket::new);

	public ClearBlockGuardPacket(LocationStorage location) {
		this(location.getID().getStringID());
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(ClearBlockGuardPacket packet, IPayloadContext context) {
		context.enqueueWork(() -> {
			BetweenlandsWorldStorage worldStorage = BetweenlandsWorldStorage.forWorld(context.player().level());
			ILocalStorage storage = worldStorage.getLocalStorageHandler().getLocalStorage(StorageID.fromString(packet.id()));
			if (storage instanceof LocationGuarded location) {
				if (location.getGuard() != null) {
					location.getGuard().clear(context.player().level());
				}
			}
		});
	}
}