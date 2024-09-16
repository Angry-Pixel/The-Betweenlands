package thebetweenlands.common.component;

import javax.annotation.Nullable;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import thebetweenlands.common.TheBetweenlands;

public abstract class AttachmentHolderIdentifier<T extends AttachmentHolderIdentifier<T>> {
	public static enum IdentifierType {
		ENTITY(EntityIdentifier.STREAM_CODEC),
		LEVEL(LevelIdentifier.STREAM_CODEC),
		CHUNK(ChunkIdentifier.STREAM_CODEC),
		BLOCKENTITY(null);

		private final StreamCodec<RegistryFriendlyByteBuf, ? extends AttachmentHolderIdentifier<?>> streamCodec;
		private IdentifierType(StreamCodec<RegistryFriendlyByteBuf, ? extends AttachmentHolderIdentifier<?>> streamCodec) {
			this.streamCodec = streamCodec;
		}
		
		public StreamCodec<RegistryFriendlyByteBuf, ? extends AttachmentHolderIdentifier<?>> getCodec() {
			return this.streamCodec;
		}
	};
	
	private final IdentifierType type;
	
	public AttachmentHolderIdentifier(IdentifierType holderType) {
		this.type = holderType;
	}
	
	public IdentifierType getType() {
		return type;
	}
	
	public abstract StreamCodec<? super RegistryFriendlyByteBuf, T> getCodec();
	
	protected abstract T self();

	public abstract IAttachmentHolder toRealHolder(@Nullable Level fallbackLevel);
	
	public void encode(RegistryFriendlyByteBuf buffer) {
		buffer.writeEnum(this.getType());
		this.encodeContents(buffer);
	}

	public void encodeContents(RegistryFriendlyByteBuf buffer) {
		this.getCodec().encode(buffer, this.self());
	}
	
	public static AttachmentHolderIdentifier<?> decode(RegistryFriendlyByteBuf buffer) {
		IdentifierType type = buffer.readEnum(IdentifierType.class);
		return type.getCodec().decode(buffer);
	}

	// TODO
	public static AttachmentHolderIdentifier<?> of(IAttachmentHolder attachmentHolder) {
		if(attachmentHolder instanceof Entity entity)
			return new EntityIdentifier(entity.getId(), entity.level().dimension());
		if(attachmentHolder instanceof Level level)
			return new LevelIdentifier(level.dimension());
		if(attachmentHolder instanceof LevelChunk chunk)
			return new ChunkIdentifier(chunk.getPos(), chunk.getLevel().dimension());
		return null;
	};
	
	// Util method (they all need to reference the level)
	protected static Level getLevel(Level fallbackLevel, ResourceKey<Level> dimensionID) {
		return fallbackLevel != null && fallbackLevel.dimension().equals(dimensionID) ? fallbackLevel : TheBetweenlands.getLevelWorkaround(dimensionID);
	}

	public static class EntityIdentifier extends AttachmentHolderIdentifier<EntityIdentifier> {
		public static final StreamCodec<RegistryFriendlyByteBuf, EntityIdentifier> STREAM_CODEC = StreamCodec.composite(
				ByteBufCodecs.VAR_INT, EntityIdentifier::getEntityID,
				ResourceKey.streamCodec(Registries.DIMENSION), EntityIdentifier::getDimensionID,
				EntityIdentifier::new
			);
		
		private final int entityID;
		private final ResourceKey<Level> dimensionID;
		
		public EntityIdentifier(int entityID, ResourceKey<Level> dimensionID) {
			super(IdentifierType.ENTITY);
			this.entityID = entityID;
			this.dimensionID = dimensionID;
		}

		public int getEntityID() {
			return this.entityID;
		}

		public ResourceKey<Level> getDimensionID() {
			return this.dimensionID;
		}

		@Override
		public StreamCodec<? super RegistryFriendlyByteBuf, EntityIdentifier> getCodec() {
			return STREAM_CODEC;
		}

		@Override
		protected EntityIdentifier self() {
			return this;
		}

		
		@Override
		public IAttachmentHolder toRealHolder(Level fallbackLevel) {
			final Level level = getLevel(fallbackLevel, dimensionID);
			
			return level != null ? level.getEntity(entityID) : null;
		}
	}

	public static class LevelIdentifier extends AttachmentHolderIdentifier<LevelIdentifier> {
		public static final StreamCodec<RegistryFriendlyByteBuf, LevelIdentifier> STREAM_CODEC = StreamCodec.composite(
				ResourceKey.streamCodec(Registries.DIMENSION), LevelIdentifier::getDimensionID,
				LevelIdentifier::new
			);
		
		private final ResourceKey<Level> dimensionID;
		
		public LevelIdentifier(ResourceKey<Level> dimensionID) {
			super(IdentifierType.LEVEL);
			this.dimensionID = dimensionID;
		}

		public ResourceKey<Level> getDimensionID() {
			return this.dimensionID;
		}

		@Override
		public StreamCodec<? super RegistryFriendlyByteBuf, LevelIdentifier> getCodec() {
			return STREAM_CODEC;
		}

		@Override
		protected LevelIdentifier self() {
			return this;
		}

		@Override
		public IAttachmentHolder toRealHolder(Level fallbackLevel) {
			return getLevel(fallbackLevel, dimensionID);
		}
	}


	public static class ChunkIdentifier extends AttachmentHolderIdentifier<ChunkIdentifier> {
		public static final StreamCodec<RegistryFriendlyByteBuf, ChunkIdentifier> STREAM_CODEC = StreamCodec.composite(
				ByteBufCodecs.INT, ChunkIdentifier::getChunkX,
				ByteBufCodecs.INT, ChunkIdentifier::getChunkZ,
				ResourceKey.streamCodec(Registries.DIMENSION), ChunkIdentifier::getDimensionID,
				ChunkIdentifier::new
			);

		private final int chunkX;
		private final int chunkZ;
		private final ResourceKey<Level> dimensionID;

		public ChunkIdentifier(ChunkPos pos, ResourceKey<Level> dimensionID) {
			super(IdentifierType.CHUNK);
			this.chunkX = pos.x;
			this.chunkZ = pos.z;
			this.dimensionID = dimensionID;
		}

		public ChunkIdentifier(int x, int z, ResourceKey<Level> dimensionID) {
			super(IdentifierType.CHUNK);
			this.chunkX = x;
			this.chunkZ = z;
			this.dimensionID = dimensionID;
		}

		public int getChunkX() {
			return this.chunkX;
		}
		
		public int getChunkZ() {
			return this.chunkZ;
		}
		
		public ChunkPos getChunkPos() {
			return new ChunkPos(this.chunkX, this.chunkZ);
		}

		public ResourceKey<Level> getDimensionID() {
			return this.dimensionID;
		}

		@Override
		public StreamCodec<? super RegistryFriendlyByteBuf, ChunkIdentifier> getCodec() {
			return STREAM_CODEC;
		}

		@Override
		protected ChunkIdentifier self() {
			return this;
		}

		@Override
		public IAttachmentHolder toRealHolder(Level fallbackLevel) {
			final Level level = getLevel(fallbackLevel, dimensionID);
			
			return level != null ? level.getChunk(this.chunkX, this.chunkZ) : null;
		}
	}

}
