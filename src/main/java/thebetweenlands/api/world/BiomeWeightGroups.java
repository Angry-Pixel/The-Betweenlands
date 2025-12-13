package thebetweenlands.api.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;

import javax.annotation.concurrent.NotThreadSafe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

/**
 * Groups biomes together so they can get special treatment by certain worldgen behaviours
 */
public record BiomeWeightGroups(List<HolderSet<Biome>> biomeGroups) {
	public static final BiomeWeightGroups EMPTY = new BiomeWeightGroups(List.of());

	// ============ Codecs ============
	
	public static final MapCodec<BiomeWeightGroups> MAP_CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
					Biome.LIST_CODEC.listOf().fieldOf("biome_groups").forGetter(BiomeWeightGroups::biomeGroups)
				).apply(instance, BiomeWeightGroups::new)
		);

	public static final Codec<BiomeWeightGroups> CODEC = Biome.LIST_CODEC.listOf().xmap(BiomeWeightGroups::new, BiomeWeightGroups::biomeGroups);

	// ================================
	
	
	
	// ============ Constructor ============
	
	public BiomeWeightGroups(List<HolderSet<Biome>> biomeGroups) {
		// Keep things immutable
		this.biomeGroups = List.copyOf(biomeGroups);
	}

	// =====================================

	
	
	// ============ Builder ============
	
	public static Builder builder(HolderGetter<Biome> biomeRegistry) {
		return new Builder(biomeRegistry);
	}
	
	@NotThreadSafe
	public static final class Builder {

		public static record BiomeGroup(Optional<String> stringId, OptionalInt indexId, List<Holder<Biome>> biomes) {
			public static boolean nonEmpty(BiomeGroup group) {
				return !group.isEmpty();
			}
			
			public BiomeGroup(String id) {
				this(Optional.of(Objects.requireNonNull(id)), OptionalInt.empty(), new ArrayList<>());
			}

			public BiomeGroup(int id) {
				this(Optional.empty(), OptionalInt.of(id), new ArrayList<>());
			}

			public boolean addBiome(Holder<Biome> biome) {
				return this.biomes.add(biome);
			}

			public boolean isEmpty() {
				return this.biomes.isEmpty();
			}
		}

		private final Map<String, BiomeGroup> namedBiomeGroups;
		private final List<BiomeGroup> biomeGroups;
		private final HolderGetter<Biome> biomeRegistry;
		
		public Builder(HolderGetter<Biome> biomeRegistry) {
			this.biomeRegistry = biomeRegistry;
			this.namedBiomeGroups = new HashMap<>();
			this.biomeGroups = new ArrayList<>();
		}

		// ======== Getting/Creating biome groups ========
		
		protected BiomeGroup createUnnamedBiomeGroup() {
			// Add a new biome group to the end of the list
			int id = this.biomeGroups.size();
			BiomeGroup biomeGroup = new BiomeGroup(id);
			this.biomeGroups.add(biomeGroup);
			return biomeGroup;
		}

		protected BiomeGroup getOrCreateUnnamedBiomeGroup(int index) {
			// Fill up the list
			while(this.biomeGroups.size() <= index) {
				this.createUnnamedBiomeGroup();
			}
			// Get the biome group
			return this.biomeGroups.get(index);
		}

		protected BiomeGroup getOrCreateNamedBiomeGroup(String name) {
			Objects.requireNonNull(name);
			// Create a new group if one doesn't exist by this name
			// Otherwise, return the existing group
			return this.namedBiomeGroups.computeIfAbsent(name, BiomeGroup::new);
		}

		// ======== Adding a new unnamed biome groups ========

		public BiomeGroup createBiomeGroup(ResourceKey<Biome> biome) {
			BiomeGroup biomeGroup = this.createUnnamedBiomeGroup();
			biomeGroup.addBiome(this.biomeRegistry.getOrThrow(biome));
			return biomeGroup;
		}

		public BiomeGroup createBiomeGroup(ResourceKey<Biome> biome1, ResourceKey<Biome> biome2) {
			BiomeGroup biomeGroup = this.createUnnamedBiomeGroup();
			biomeGroup.addBiome(this.biomeRegistry.getOrThrow(biome1));
			biomeGroup.addBiome(this.biomeRegistry.getOrThrow(biome2));
			return biomeGroup;
		}

		public BiomeGroup createBiomeGroup(ResourceKey<Biome> biome1, ResourceKey<Biome> biome2, ResourceKey<Biome> biome3) {
			BiomeGroup biomeGroup = this.createUnnamedBiomeGroup();
			biomeGroup.addBiome(this.biomeRegistry.getOrThrow(biome1));
			biomeGroup.addBiome(this.biomeRegistry.getOrThrow(biome2));
			biomeGroup.addBiome(this.biomeRegistry.getOrThrow(biome3));
			return biomeGroup;
		}

		public BiomeGroup createBiomeGroup(ResourceKey<?> ...biomes) {
			BiomeGroup biomeGroup = this.createUnnamedBiomeGroup();
			for(ResourceKey<?> key : biomes) {
				if(key.isFor(Registries.BIOME)) {
					@SuppressWarnings("unchecked")
					ResourceKey<Biome> biome = (ResourceKey<Biome>)key;
					biomeGroup.addBiome(this.biomeRegistry.getOrThrow(biome));
				}
			}
			return biomeGroup;
		}
		
		public BiomeGroup createBiomeGroup(Holder<Biome> biome) {
			BiomeGroup biomeGroup = this.createUnnamedBiomeGroup();
			biomeGroup.addBiome(biome);
			return biomeGroup;
		}

		public BiomeGroup createBiomeGroup(Holder<Biome> biome1, Holder<Biome> biome2) {
			BiomeGroup biomeGroup = this.createUnnamedBiomeGroup();
			biomeGroup.addBiome(biome1);
			biomeGroup.addBiome(biome2);
			return biomeGroup;
		}

		public BiomeGroup createBiomeGroup(Holder<Biome> biome1, Holder<Biome> biome2, Holder<Biome> biome3) {
			BiomeGroup biomeGroup = this.createUnnamedBiomeGroup();
			biomeGroup.addBiome(biome1);
			biomeGroup.addBiome(biome2);
			biomeGroup.addBiome(biome3);
			return biomeGroup;
		}

		public BiomeGroup createBiomeGroup(Holder<?> ...biomes) {
			BiomeGroup biomeGroup = this.createUnnamedBiomeGroup();
			for(Holder<?> holder : biomes) {
				if(holder.getKey().isFor(Registries.BIOME)) {
					@SuppressWarnings("unchecked")
					Holder<Biome> biome = (Holder<Biome>)holder;
					biomeGroup.addBiome(biome);
				}
			}
			return biomeGroup;
		}
		
		// -------- Currying Functions --------
		
		public Builder addBiomeGroup(ResourceKey<Biome> biome) {
			this.createBiomeGroup(biome);
			return this;
		}

		public Builder addBiomeGroup(ResourceKey<Biome> biome1, ResourceKey<Biome> biome2) {
			this.createBiomeGroup(biome1, biome2);
			return this;
		}

		public Builder addBiomeGroup(ResourceKey<Biome> biome1, ResourceKey<Biome> biome2, ResourceKey<Biome> biome3) {
			this.createBiomeGroup(biome1, biome2, biome3);
			return this;
		}

		public Builder addBiomeGroup(ResourceKey<?> ...biomes) {
			this.createBiomeGroup(biomes);
			return this;
		}
		
		public Builder addBiomeGroup(Holder<Biome> biome) {
			this.createBiomeGroup(biome);
			return this;
		}

		public Builder addBiomeGroup(Holder<Biome> biome1, Holder<Biome> biome2) {
			this.createBiomeGroup(biome1, biome2);
			return this;
		}

		public Builder addBiomeGroup(Holder<Biome> biome1, Holder<Biome> biome2, Holder<Biome> biome3) {
			this.createBiomeGroup(biome1, biome2, biome3);
			return this;
		}

		public Builder addBiomeGroup(Holder<?> ...biomes) {
			this.createBiomeGroup(biomes);
			return this;
		}

		// ======== Adding (to) an unnamed biome group by its index ========

		public BiomeGroup createBiomeGroup(int index, ResourceKey<Biome> biome) {
			BiomeGroup biomeGroup = this.getOrCreateUnnamedBiomeGroup(index);
			biomeGroup.addBiome(this.biomeRegistry.getOrThrow(biome));
			return biomeGroup;
		}

		public BiomeGroup createBiomeGroup(int index, ResourceKey<Biome> biome1, ResourceKey<Biome> biome2) {
			BiomeGroup biomeGroup = this.getOrCreateUnnamedBiomeGroup(index);
			biomeGroup.addBiome(this.biomeRegistry.getOrThrow(biome1));
			biomeGroup.addBiome(this.biomeRegistry.getOrThrow(biome2));
			return biomeGroup;
		}

		public BiomeGroup createBiomeGroup(int index, ResourceKey<Biome> biome1, ResourceKey<Biome> biome2, ResourceKey<Biome> biome3) {
			BiomeGroup biomeGroup = this.getOrCreateUnnamedBiomeGroup(index);
			biomeGroup.addBiome(this.biomeRegistry.getOrThrow(biome1));
			biomeGroup.addBiome(this.biomeRegistry.getOrThrow(biome2));
			biomeGroup.addBiome(this.biomeRegistry.getOrThrow(biome3));
			return biomeGroup;
		}

		public BiomeGroup createBiomeGroup(int index, ResourceKey<?> ...biomes) {
			BiomeGroup biomeGroup = this.getOrCreateUnnamedBiomeGroup(index);
			for(ResourceKey<?> key : biomes) {
				if(key.isFor(Registries.BIOME)) {
					@SuppressWarnings("unchecked")
					ResourceKey<Biome> biome = (ResourceKey<Biome>)key;
					biomeGroup.addBiome(this.biomeRegistry.getOrThrow(biome));
				}
			}
			return biomeGroup;
		}
		
		public BiomeGroup createBiomeGroup(int index, Holder<Biome> biome) {
			BiomeGroup biomeGroup = this.getOrCreateUnnamedBiomeGroup(index);
			biomeGroup.addBiome(biome);
			return biomeGroup;
		}

		public BiomeGroup createBiomeGroup(int index, Holder<Biome> biome1, Holder<Biome> biome2) {
			BiomeGroup biomeGroup = this.getOrCreateUnnamedBiomeGroup(index);
			biomeGroup.addBiome(biome1);
			biomeGroup.addBiome(biome2);
			return biomeGroup;
		}

		public BiomeGroup createBiomeGroup(int index, Holder<Biome> biome1, Holder<Biome> biome2, Holder<Biome> biome3) {
			BiomeGroup biomeGroup = this.getOrCreateUnnamedBiomeGroup(index);
			biomeGroup.addBiome(biome1);
			biomeGroup.addBiome(biome2);
			biomeGroup.addBiome(biome3);
			return biomeGroup;
		}

		public BiomeGroup createBiomeGroup(int index, Holder<?> ...biomes) {
			BiomeGroup biomeGroup = this.getOrCreateUnnamedBiomeGroup(index);
			for(Holder<?> holder : biomes) {
				if(holder.getKey().isFor(Registries.BIOME)) {
					@SuppressWarnings("unchecked")
					Holder<Biome> biome = (Holder<Biome>)holder;
					biomeGroup.addBiome(biome);
				}
			}
			return biomeGroup;
		}

		// -------- Currying Functions --------
		
		public Builder addBiomeGroup(int index, ResourceKey<Biome> biome) {
			this.createBiomeGroup(index, biome);
			return this;
		}

		public Builder addBiomeGroup(int index, ResourceKey<Biome> biome1, ResourceKey<Biome> biome2) {
			this.createBiomeGroup(index, biome1, biome2);
			return this;
		}

		public Builder addBiomeGroup(int index, ResourceKey<Biome> biome1, ResourceKey<Biome> biome2, ResourceKey<Biome> biome3) {
			this.createBiomeGroup(index, biome1, biome2, biome3);
			return this;
		}

		public Builder addBiomeGroup(int index, ResourceKey<?> ...biomes) {
			this.createBiomeGroup(index, biomes);
			return this;
		}
		
		public Builder addBiomeGroup(int index, Holder<Biome> biome) {
			this.createBiomeGroup(index, biome);
			return this;
		}

		public Builder addBiomeGroup(int index, Holder<Biome> biome1, Holder<Biome> biome2) {
			this.createBiomeGroup(index, biome1, biome2);
			return this;
		}

		public Builder addBiomeGroup(int index, Holder<Biome> biome1, Holder<Biome> biome2, Holder<Biome> biome3) {
			this.createBiomeGroup(index, biome1, biome2, biome3);
			return this;
		}

		public Builder addBiomeGroup(int index, Holder<?> ...biomes) {
			this.createBiomeGroup(index, biomes);
			return this;
		}

		// ======== Adding (to) a named biome group by its name ========

		public BiomeGroup createBiomeGroup(String name, ResourceKey<Biome> biome) {
			BiomeGroup biomeGroup = this.getOrCreateNamedBiomeGroup(name);
			biomeGroup.addBiome(this.biomeRegistry.getOrThrow(biome));
			return biomeGroup;
		}

		public BiomeGroup createBiomeGroup(String name, ResourceKey<Biome> biome1, ResourceKey<Biome> biome2) {
			BiomeGroup biomeGroup = this.getOrCreateNamedBiomeGroup(name);
			biomeGroup.addBiome(this.biomeRegistry.getOrThrow(biome1));
			biomeGroup.addBiome(this.biomeRegistry.getOrThrow(biome2));
			return biomeGroup;
		}

		public BiomeGroup createBiomeGroup(String name, ResourceKey<Biome> biome1, ResourceKey<Biome> biome2, ResourceKey<Biome> biome3) {
			BiomeGroup biomeGroup = this.getOrCreateNamedBiomeGroup(name);
			biomeGroup.addBiome(this.biomeRegistry.getOrThrow(biome1));
			biomeGroup.addBiome(this.biomeRegistry.getOrThrow(biome2));
			biomeGroup.addBiome(this.biomeRegistry.getOrThrow(biome3));
			return biomeGroup;
		}

		public BiomeGroup createBiomeGroup(String name, ResourceKey<?> ...biomes) {
			BiomeGroup biomeGroup = this.getOrCreateNamedBiomeGroup(name);
			for(ResourceKey<?> key : biomes) {
				if(key.isFor(Registries.BIOME)) {
					@SuppressWarnings("unchecked")
					ResourceKey<Biome> biome = (ResourceKey<Biome>)key;
					biomeGroup.addBiome(this.biomeRegistry.getOrThrow(biome));
				}
			}
			return biomeGroup;
		}
		
		public BiomeGroup createBiomeGroup(String name, Holder<Biome> biome) {
			BiomeGroup biomeGroup = this.getOrCreateNamedBiomeGroup(name);
			biomeGroup.addBiome(biome);
			return biomeGroup;
		}

		public BiomeGroup createBiomeGroup(String name, Holder<Biome> biome1, Holder<Biome> biome2) {
			BiomeGroup biomeGroup = this.getOrCreateNamedBiomeGroup(name);
			biomeGroup.addBiome(biome1);
			biomeGroup.addBiome(biome2);
			return biomeGroup;
		}

		public BiomeGroup createBiomeGroup(String name, Holder<Biome> biome1, Holder<Biome> biome2, Holder<Biome> biome3) {
			BiomeGroup biomeGroup = this.getOrCreateNamedBiomeGroup(name);
			biomeGroup.addBiome(biome1);
			biomeGroup.addBiome(biome2);
			biomeGroup.addBiome(biome3);
			return biomeGroup;
		}

		public BiomeGroup createBiomeGroup(String name, Holder<?> ...biomes) {
			BiomeGroup biomeGroup = this.getOrCreateNamedBiomeGroup(name);
			for(Holder<?> holder : biomes) {
				if(holder.getKey().isFor(Registries.BIOME)) {
					@SuppressWarnings("unchecked")
					Holder<Biome> biome = (Holder<Biome>)holder;
					biomeGroup.addBiome(biome);
				}
			}
			return biomeGroup;
		}

		// -------- Currying Functions --------
		
		public Builder addBiomeGroup(String name, ResourceKey<Biome> biome) {
			this.createBiomeGroup(name, biome);
			return this;
		}

		public Builder addBiomeGroup(String name, ResourceKey<Biome> biome1, ResourceKey<Biome> biome2) {
			this.createBiomeGroup(name, biome1, biome2);
			return this;
		}

		public Builder addBiomeGroup(String name, ResourceKey<Biome> biome1, ResourceKey<Biome> biome2, ResourceKey<Biome> biome3) {
			this.createBiomeGroup(name, biome1, biome2, biome3);
			return this;
		}

		public Builder addBiomeGroup(String name, ResourceKey<?> ...biomes) {
			this.createBiomeGroup(name, biomes);
			return this;
		}
		
		public Builder addBiomeGroup(String name, Holder<Biome> biome) {
			this.createBiomeGroup(name, biome);
			return this;
		}

		public Builder addBiomeGroup(String name, Holder<Biome> biome1, Holder<Biome> biome2) {
			this.createBiomeGroup(name, biome1, biome2);
			return this;
		}

		public Builder addBiomeGroup(String name, Holder<Biome> biome1, Holder<Biome> biome2, Holder<Biome> biome3) {
			this.createBiomeGroup(name, biome1, biome2, biome3);
			return this;
		}

		public Builder addBiomeGroup(String name, Holder<?> ...biomes) {
			this.createBiomeGroup(name, biomes);
			return this;
		}
		
		// ============ Build the BiomeWeightGroups from this ============
		
		public BiomeWeightGroups build() {
			if(this.biomeGroups.size() == 0 && this.namedBiomeGroups.size() == 0) {
				return BiomeWeightGroups.EMPTY;
			}
			
			// List of all biome groups that have at least 1 element
			List<List<Holder<Biome>>> nonEmptyBiomeGroups = new ArrayList<>();

			this.biomeGroups.stream()
				.filter(BiomeGroup::nonEmpty)
				.map(BiomeGroup::biomes)
				.forEach(nonEmptyBiomeGroups::add);
			
			this.namedBiomeGroups.values().stream()
				.filter(BiomeGroup::nonEmpty)
				.sorted((group1, group2) -> Objects.compare(group1.stringId().get(), group2.stringId().get(), String::compareTo))
				.map(BiomeGroup::biomes)
				.forEach(nonEmptyBiomeGroups::add);

//			Set<HolderSet<Biome>> biomeGroupSet = nonEmptyBiomeGroups.stream().map(HolderSet::direct).collect(Collectors.toUnmodifiableSet());
			List<HolderSet<Biome>> biomeGroupSet = nonEmptyBiomeGroups.stream().map(HolderSet::direct).collect(Collectors.toUnmodifiableList());
			
			return new BiomeWeightGroups(biomeGroupSet);
		}
	}

	// =================================
}
