package thebetweenlands.common.network;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thebetweenlands.api.storage.IChunkStorage;
import thebetweenlands.api.storage.IWorldStorage;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.WorldStorageImpl;
import thebetweenlands.util.ExtraCodecs;

public record SyncLocalStorageReferencesPacket(CompoundTag tag, ChunkPos pos) implements CustomPacketPayload {

	public static final Type<SyncLocalStorageReferencesPacket> TYPE = new Type<>(TheBetweenlands.prefix("sync_local_storage_refs"));
	public static final StreamCodec<RegistryFriendlyByteBuf, SyncLocalStorageReferencesPacket> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.COMPOUND_TAG, SyncLocalStorageReferencesPacket::tag,
		ExtraCodecs.CHUNK_POS_CODEC, SyncLocalStorageReferencesPacket::pos,
		SyncLocalStorageReferencesPacket::new);

	public SyncLocalStorageReferencesPacket(IChunkStorage storage) {
		this(storage.writeLocalStorageReferences(new CompoundTag()), storage.getChunk().getPos());
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(SyncLocalStorageReferencesPacket packet, IPayloadContext context) {
		context.enqueueWork(() -> {
			Level level = context.player().level();
			ChunkAccess chunk = level.getChunkSource().getChunkNow(packet.pos().x, packet.pos().z);
			if (chunk != null) {
				IWorldStorage worldStorage = BetweenlandsWorldStorage.get(level);
				if (worldStorage != null) {
					IChunkStorage chunkStorage = worldStorage.getChunkStorage(chunk);
					if (chunkStorage != null) {
						chunkStorage.readLocalStorageReferences(packet.tag);
					}
				}
			}
		});
	}
}