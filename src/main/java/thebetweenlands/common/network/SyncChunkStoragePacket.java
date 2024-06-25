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
import thebetweenlands.common.world.storage.WorldStorageImpl;
import thebetweenlands.util.ExtraCodecs;

public record SyncChunkStoragePacket(CompoundTag tag, ChunkPos pos) implements CustomPacketPayload {

	public static final Type<SyncChunkStoragePacket> TYPE = new Type<>(TheBetweenlands.prefix("sync_chunk_storage"));
	public static final StreamCodec<RegistryFriendlyByteBuf, SyncChunkStoragePacket> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.COMPOUND_TAG, SyncChunkStoragePacket::tag,
		ExtraCodecs.CHUNK_POS_CODEC, SyncChunkStoragePacket::pos,
		SyncChunkStoragePacket::new);

	public SyncChunkStoragePacket(IChunkStorage storage) {
		this(storage.writeToNBT(new CompoundTag(), true), storage.getChunk().getPos());
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(SyncChunkStoragePacket packet, IPayloadContext context) {
		context.enqueueWork(() -> {
			Level level = context.player().level();
			ChunkAccess chunk = level.getChunk(packet.pos().x, packet.pos().z);
			if(chunk != null) {
				IWorldStorage worldStorage = WorldStorageImpl.getAttachment(level);
				IChunkStorage chunkStorage = worldStorage.getChunkStorage(chunk);
				chunkStorage.readFromNBT(packet.tag, true);
			}
		});
	}
}