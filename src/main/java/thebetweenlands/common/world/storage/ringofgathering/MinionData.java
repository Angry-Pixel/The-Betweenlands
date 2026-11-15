package thebetweenlands.common.world.storage.ringofgathering;

import java.util.Optional;
import java.util.function.Function;

import com.mojang.datafixers.util.Either;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.HolderLookup.RegistryLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.dimension.DimensionType;
import net.neoforged.neoforge.common.util.INBTSerializable;

public class MinionData implements INBTSerializable<CompoundTag> {
	private static final Function<ResourceKey<?>, String> KEY_TOSTRING = key -> key.location().toString();
	
	private static String keyToString(Either<? extends ResourceKey<?>, String> key) {
		return key.map(KEY_TOSTRING, Function.identity());
	}
	
	/**
	 * Minion's current status
	 */
	private MinionStatus minionStatus;
	/**
	 * Entity type of the minion
	 */
	private Either<ResourceKey<EntityType<?>>, String> entityType;
	/**
	 * Used to identify duplicate minions
	 */
	private int minionGeneration;

	/**
	 * Last dimension that the entity was in before unloading or dying
	 */
	private Either<ResourceKey<DimensionType>, String> lastSeenDimension;
	/**
	 * Last chunk that the entity was in before unloading or dying
	 */
	private ChunkPos lastSeenChunk;
	
	/**
	 * Entity NBT immediately before unloading or dying
	 */
	private CompoundTag lastSeenNBT;
	
	public MinionData(MinionStatus minionStatus, ResourceKey<EntityType<?>> entityType, ResourceKey<DimensionType> lastSeenDimension, ChunkPos lastSeenChunk, CompoundTag lastSeenNBT) {
		this.minionStatus = minionStatus;
		this.entityType = Either.left(entityType);
		this.minionGeneration = 0;
		this.lastSeenDimension = Either.left(lastSeenDimension);
		this.lastSeenChunk = lastSeenChunk;
		this.lastSeenNBT = lastSeenNBT;
	}

	public MinionStatus minionStatus() {
		return this.minionStatus;
	}

	public Either<ResourceKey<EntityType<?>>, String> entityType() {
		return this.entityType;
	}
	
	public Either<ResourceKey<DimensionType>, String> getLastSeenDimension() {
		return this.lastSeenDimension;
	}
	
	public ChunkPos getLastSeenChunkPos() {
		return this.lastSeenChunk;
	}
	
	public CompoundTag getLastSeenNBT() {
		return this.lastSeenNBT;
	}
	
	public void updateData(Entity entity) {
		
	}
	
	@Override
	public CompoundTag serializeNBT(Provider provider) {
		CompoundTag tag = new CompoundTag(5);
		
		tag.putString("status", this.minionStatus.name());
		tag.putString("entity_type", keyToString(this.entityType));
		
		tag.putString("last_seen_dimension", keyToString(this.lastSeenDimension));
		tag.putLong("last_seen_chunk", this.lastSeenChunk.toLong());
		tag.put("last_seen_nbt", this.lastSeenNBT);
		
		return tag;
	}

	@Override
	public void deserializeNBT(Provider provider, CompoundTag tag) {
		this.minionStatus = MinionStatus.byNameOrElse(tag.getString("status"), MinionStatus.MISSING);
		
	}
	
}
