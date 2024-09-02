package thebetweenlands.common.network.clientbound;

import net.minecraft.nbt.CompoundTag;
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

public record BlockGuardDataPacket(String id, CompoundTag data) implements CustomPacketPayload {

	public static final Type<BlockGuardDataPacket> TYPE = new Type<>(TheBetweenlands.prefix("block_guard_data"));
	public static final StreamCodec<RegistryFriendlyByteBuf, BlockGuardDataPacket> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.STRING_UTF8, BlockGuardDataPacket::id,
		ByteBufCodecs.COMPOUND_TAG, BlockGuardDataPacket::data,
		BlockGuardDataPacket::new);


	public BlockGuardDataPacket(LocationStorage location) {
		this(location.getID().getStringID(), location.writeGuardNBT(new CompoundTag()));
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(BlockGuardDataPacket packet, IPayloadContext context) {
		context.enqueueWork(() -> {
			BetweenlandsWorldStorage worldStorage = BetweenlandsWorldStorage.get(context.player().level());
			if (worldStorage != null) {
				ILocalStorage storage = worldStorage.getLocalStorageHandler().getLocalStorage(StorageID.fromString(packet.id()));
				if (storage instanceof LocationGuarded location) {
					location.readGuardNBT(packet.data());
				}
			}
		});
	}
}