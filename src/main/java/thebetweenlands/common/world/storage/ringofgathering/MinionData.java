package thebetweenlands.common.world.storage.ringofgathering;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

import com.mojang.datafixers.util.Either;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.HolderLookup.RegistryLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
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
	 * Minion UUID (which is not necessarily the same as the minion's entity UUID)
	 */
	private UUID minionUuid;
	/**
	 * Used to identify duplicate minions
	 */
	private long minionGeneration;

	/**
	 * Last dimension that the entity was in before unloading or dying
	 */
	private Either<ResourceKey<Level>, String> lastSeenDimension;
	/**
	 * Last chunk that the entity was in before unloading or dying
	 */
	private ChunkPos lastSeenChunk;
	
	/**
	 * Entity NBT immediately before unloading or dying
	 */
	private CompoundTag lastSeenNBT;
	
	public MinionData(MinionStatus minionStatus, ResourceKey<EntityType<?>> entityType, ResourceKey<Level> lastSeenDimension, ChunkPos lastSeenChunk, CompoundTag lastSeenNBT) {
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
	
	public Either<ResourceKey<Level>, String> getLastSeenDimension() {
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
		tag.putUUID("minion_uuid", this.minionUuid);
		tag.putLong("minion_generation", this.minionGeneration);
		
		tag.putString("last_seen_dimension", keyToString(this.lastSeenDimension));
		tag.putLong("last_seen_chunk", this.lastSeenChunk.toLong());
		tag.put("last_seen_nbt", this.lastSeenNBT);
		
		return tag;
	}

	@Override
	public void deserializeNBT(Provider provider, CompoundTag tag) {
		this.minionStatus = MinionStatus.byNameOrElse(tag.getString("status"), MinionStatus.MISSING);
		this.entityType = getKey(provider, Registries.ENTITY_TYPE, tag.getString("entity_type"));
		this.minionUuid = tag.getUUID("minion_uuid");
		this.minionGeneration = tag.getLong("minion_generation");
		
		this.lastSeenDimension = getKey(provider, Registries.DIMENSION, tag.getString("last_seen_dimension"));
		this.lastSeenChunk = new ChunkPos(tag.getLong("last_seen_chunk"));
		this.lastSeenNBT = tag.getCompound("last_seen_nbt");
	}
	
	/**
	 * Reads a string into a registry key if there's something registered to that key.
	 * If the string is invalid or doesn't reference a valid holder, then no registry key is returned.
	 * @param <T>
	 * @param provider
	 * @param registryKey
	 * @param string
	 * @return
	 */
	public static <T> Either<ResourceKey<T>, String> getKey(Provider provider, ResourceKey<? extends Registry<T>> registryKey, String string) {
		// Try to parse the string as a ResourceLocation
		ResourceLocation location = ResourceLocation.tryParse(string);
		if (location == null) {
			return Either.right(string);
		}
		
		// Try to lookup the relevant registry
		Optional<RegistryLookup<T>> entityRegistry = provider.lookup(registryKey);
		if (entityRegistry.isEmpty()) {
			return Either.right(string);
		}
		
		RegistryLookup<T> lookup = entityRegistry.get();
		
		// Check if there's a holder associated with this key
		ResourceKey<T> key = ResourceKey.create(registryKey, location);
		
		Optional<Holder.Reference<T>> holder = lookup.get(key);
		
		if (holder.isPresent()) {
			// holder.get().key() in case the name has changed
			return Either.left(holder.get().key());
		} else {
			return Either.right(string);
		}
	}
}
