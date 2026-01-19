package thebetweenlands.common.world.gen;

import java.util.ArrayList;
import java.util.List;
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
import thebetweenlands.api.world.generator.ConfiguredEarlyGenerator;

// Early Generators: run before biome generators
// Late Generators: run after biome generators
public record GlobalEarlyGenerators(List<HolderSet<ConfiguredEarlyGenerator<?, ?>>> earlyGenerators, List<HolderSet<ConfiguredEarlyGenerator<?, ?>>> lateGenerators) {
	public static final GlobalEarlyGenerators EMPTY = new GlobalEarlyGenerators(List.of(), List.of());
	
	public static final MapCodec<GlobalEarlyGenerators> MAP_CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
					ConfiguredEarlyGenerator.LIST_OF_LISTS_CODEC.fieldOf("early").forGetter(GlobalEarlyGenerators::earlyGenerators),
					ConfiguredEarlyGenerator.LIST_OF_LISTS_CODEC.fieldOf("late").forGetter(GlobalEarlyGenerators::lateGenerators)
				).apply(instance, GlobalEarlyGenerators::new)
			);
	
	public static final Codec<GlobalEarlyGenerators> CODEC = MAP_CODEC.codec();
	

	// ============ Builder ============
	
	public static Builder builder(HolderGetter<ConfiguredEarlyGenerator<?, ?>> generatorRegistry) {
		return new Builder(generatorRegistry);
	}
	
	@NotThreadSafe
	public static final class Builder {

		public static record GeneratorGroup(List<Holder<ConfiguredEarlyGenerator<?, ?>>> generators) {
			public static boolean nonEmpty(GeneratorGroup group) {
				return !group.isEmpty();
			}
			
			public GeneratorGroup() {
				this(new ArrayList<>());
			}

			public boolean addGenerator(Holder<ConfiguredEarlyGenerator<?, ?>> generator) {
				return this.generators.add(generator);
			}

			public boolean isEmpty() {
				return this.generators.isEmpty();
			}
		}

		private final List<GeneratorGroup> earlyGenerators;
		private final List<GeneratorGroup> lateGenerators;
		private final HolderGetter<ConfiguredEarlyGenerator<?, ?>> generatorRegistry;
		
		public Builder(HolderGetter<ConfiguredEarlyGenerator<?, ?>> generatorRegistry) {
			this.generatorRegistry = generatorRegistry;
			this.earlyGenerators = new ArrayList<>();
			this.lateGenerators = new ArrayList<>();
		}

		// ======== Getting/Creating generator groups ========

		protected GeneratorGroup createEarlyGeneratorGroup() {
			// Add a new generator group to the end of the list
			GeneratorGroup generatorGroup = new GeneratorGroup();
			this.earlyGenerators.add(generatorGroup);
			return generatorGroup;
		}

		protected GeneratorGroup insertEarlyGeneratorGroup(int index) {
			// Fill up the list
			while(this.earlyGenerators.size() < index) {
				this.createEarlyGeneratorGroup();
			}
			// Insert a new generator group at the specified list index
			GeneratorGroup generatorGroup = new GeneratorGroup();
			this.earlyGenerators.add(index, generatorGroup);
			return generatorGroup;
		}

		protected GeneratorGroup createLateGeneratorGroup() {
			// Add a new generator group to the end of the list
			GeneratorGroup generatorGroup = new GeneratorGroup();
			this.earlyGenerators.add(generatorGroup);
			return generatorGroup;
		}

		protected GeneratorGroup insertLateGeneratorGroup(int index) {
			// Fill up the list
			while(this.earlyGenerators.size() < index) {
				this.createEarlyGeneratorGroup();
			}
			// Insert a new generator group at the specified list index
			GeneratorGroup generatorGroup = new GeneratorGroup();
			this.earlyGenerators.add(index, generatorGroup);
			return generatorGroup;
		}

		// ======== Adding a new early generator group ========

		public GeneratorGroup addEarlyGeneratorGroup(ResourceKey<ConfiguredEarlyGenerator<?, ?>> generator) {
			GeneratorGroup generatorGroup = this.createEarlyGeneratorGroup();
			generatorGroup.addGenerator(this.generatorRegistry.getOrThrow(generator));
			return generatorGroup;
		}

		public GeneratorGroup addEarlyGeneratorGroup(ResourceKey<ConfiguredEarlyGenerator<?, ?>> generator1, ResourceKey<ConfiguredEarlyGenerator<?, ?>> generator2) {
			GeneratorGroup generatorGroup = this.createEarlyGeneratorGroup();
			generatorGroup.addGenerator(this.generatorRegistry.getOrThrow(generator1));
			generatorGroup.addGenerator(this.generatorRegistry.getOrThrow(generator2));
			return generatorGroup;
		}

		public GeneratorGroup addEarlyGeneratorGroup(ResourceKey<ConfiguredEarlyGenerator<?, ?>> generator1, ResourceKey<ConfiguredEarlyGenerator<?, ?>> generator2, ResourceKey<ConfiguredEarlyGenerator<?, ?>> generator3) {
			GeneratorGroup generatorGroup = this.createEarlyGeneratorGroup();
			generatorGroup.addGenerator(this.generatorRegistry.getOrThrow(generator1));
			generatorGroup.addGenerator(this.generatorRegistry.getOrThrow(generator2));
			generatorGroup.addGenerator(this.generatorRegistry.getOrThrow(generator3));
			return generatorGroup;
		}

		public GeneratorGroup addEarlyGeneratorGroup(ResourceKey<?> ...generators) {
			GeneratorGroup generatorGroup = this.createEarlyGeneratorGroup();
			for(ResourceKey<?> key : generators) {
				if(key.isFor(Registries.BIOME)) {
					@SuppressWarnings("unchecked")
					ResourceKey<ConfiguredEarlyGenerator<?, ?>> generator = (ResourceKey<ConfiguredEarlyGenerator<?, ?>>)key;
					generatorGroup.addGenerator(this.generatorRegistry.getOrThrow(generator));
				}
			}
			return generatorGroup;
		}
		
		public GeneratorGroup addEarlyGeneratorGroup(Holder<ConfiguredEarlyGenerator<?, ?>> generator) {
			GeneratorGroup generatorGroup = this.createEarlyGeneratorGroup();
			generatorGroup.addGenerator(generator);
			return generatorGroup;
		}

		public GeneratorGroup addEarlyGeneratorGroup(Holder<ConfiguredEarlyGenerator<?, ?>> generator1, Holder<ConfiguredEarlyGenerator<?, ?>> generator2) {
			GeneratorGroup generatorGroup = this.createEarlyGeneratorGroup();
			generatorGroup.addGenerator(generator1);
			generatorGroup.addGenerator(generator2);
			return generatorGroup;
		}

		public GeneratorGroup addEarlyGeneratorGroup(Holder<ConfiguredEarlyGenerator<?, ?>> generator1, Holder<ConfiguredEarlyGenerator<?, ?>> generator2, Holder<ConfiguredEarlyGenerator<?, ?>> generator3) {
			GeneratorGroup generatorGroup = this.createEarlyGeneratorGroup();
			generatorGroup.addGenerator(generator1);
			generatorGroup.addGenerator(generator2);
			generatorGroup.addGenerator(generator3);
			return generatorGroup;
		}

		public GeneratorGroup addEarlyGeneratorGroup(Holder<?> ...generators) {
			GeneratorGroup generatorGroup = this.createEarlyGeneratorGroup();
			for(Holder<?> holder : generators) {
				if(holder.getKey().isFor(Registries.BIOME)) {
					@SuppressWarnings("unchecked")
					Holder<ConfiguredEarlyGenerator<?, ?>> generator = (Holder<ConfiguredEarlyGenerator<?, ?>>)holder;
					generatorGroup.addGenerator(generator);
				}
			}
			return generatorGroup;
		}
		
		// -------- Currying Functions --------
		
		public Builder addEarlyGroup(ResourceKey<ConfiguredEarlyGenerator<?, ?>> generator) {
			this.addEarlyGeneratorGroup(generator);
			return this;
		}

		public Builder addEarlyGroup(ResourceKey<ConfiguredEarlyGenerator<?, ?>> generator1, ResourceKey<ConfiguredEarlyGenerator<?, ?>> generator2) {
			this.addEarlyGeneratorGroup(generator1, generator2);
			return this;
		}

		public Builder addEarlyGroup(ResourceKey<ConfiguredEarlyGenerator<?, ?>> generator1, ResourceKey<ConfiguredEarlyGenerator<?, ?>> generator2, ResourceKey<ConfiguredEarlyGenerator<?, ?>> generator3) {
			this.addEarlyGeneratorGroup(generator1, generator2, generator3);
			return this;
		}

		public Builder addEarlyGroup(ResourceKey<?> ...generators) {
			this.addEarlyGeneratorGroup(generators);
			return this;
		}
		
		public Builder addEarlyGroup(Holder<ConfiguredEarlyGenerator<?, ?>> generator) {
			this.addEarlyGeneratorGroup(generator);
			return this;
		}

		public Builder addEarlyGroup(Holder<ConfiguredEarlyGenerator<?, ?>> generator1, Holder<ConfiguredEarlyGenerator<?, ?>> generator2) {
			this.addEarlyGeneratorGroup(generator1, generator2);
			return this;
		}

		public Builder addEarlyGroup(Holder<ConfiguredEarlyGenerator<?, ?>> generator1, Holder<ConfiguredEarlyGenerator<?, ?>> generator2, Holder<ConfiguredEarlyGenerator<?, ?>> generator3) {
			this.addEarlyGeneratorGroup(generator1, generator2, generator3);
			return this;
		}

		public Builder addEarlyGroup(Holder<?> ...generators) {
			this.addEarlyGeneratorGroup(generators);
			return this;
		}

		// ======== Inserting an early group to an index ========

		public GeneratorGroup insertEarlyGeneratorGroup(int index, ResourceKey<ConfiguredEarlyGenerator<?, ?>> generator) {
			GeneratorGroup generatorGroup = this.insertEarlyGeneratorGroup(index);
			generatorGroup.addGenerator(this.generatorRegistry.getOrThrow(generator));
			return generatorGroup;
		}

		public GeneratorGroup insertEarlyGeneratorGroup(int index, ResourceKey<ConfiguredEarlyGenerator<?, ?>> generator1, ResourceKey<ConfiguredEarlyGenerator<?, ?>> generator2) {
			GeneratorGroup generatorGroup = this.insertEarlyGeneratorGroup(index);
			generatorGroup.addGenerator(this.generatorRegistry.getOrThrow(generator1));
			generatorGroup.addGenerator(this.generatorRegistry.getOrThrow(generator2));
			return generatorGroup;
		}

		public GeneratorGroup insertEarlyGeneratorGroup(int index, ResourceKey<ConfiguredEarlyGenerator<?, ?>> generator1, ResourceKey<ConfiguredEarlyGenerator<?, ?>> generator2, ResourceKey<ConfiguredEarlyGenerator<?, ?>> generator3) {
			GeneratorGroup generatorGroup = this.insertEarlyGeneratorGroup(index);
			generatorGroup.addGenerator(this.generatorRegistry.getOrThrow(generator1));
			generatorGroup.addGenerator(this.generatorRegistry.getOrThrow(generator2));
			generatorGroup.addGenerator(this.generatorRegistry.getOrThrow(generator3));
			return generatorGroup;
		}

		public GeneratorGroup insertEarlyGeneratorGroup(int index, ResourceKey<?> ...generators) {
			GeneratorGroup generatorGroup = this.insertEarlyGeneratorGroup(index);
			for(ResourceKey<?> key : generators) {
				if(key.isFor(Registries.BIOME)) {
					@SuppressWarnings("unchecked")
					ResourceKey<ConfiguredEarlyGenerator<?, ?>> generator = (ResourceKey<ConfiguredEarlyGenerator<?, ?>>)key;
					generatorGroup.addGenerator(this.generatorRegistry.getOrThrow(generator));
				}
			}
			return generatorGroup;
		}
		
		public GeneratorGroup insertEarlyGeneratorGroup(int index, Holder<ConfiguredEarlyGenerator<?, ?>> generator) {
			GeneratorGroup generatorGroup = this.insertEarlyGeneratorGroup(index);
			generatorGroup.addGenerator(generator);
			return generatorGroup;
		}

		public GeneratorGroup insertEarlyGeneratorGroup(int index, Holder<ConfiguredEarlyGenerator<?, ?>> generator1, Holder<ConfiguredEarlyGenerator<?, ?>> generator2) {
			GeneratorGroup generatorGroup = this.insertEarlyGeneratorGroup(index);
			generatorGroup.addGenerator(generator1);
			generatorGroup.addGenerator(generator2);
			return generatorGroup;
		}

		public GeneratorGroup insertEarlyGeneratorGroup(int index, Holder<ConfiguredEarlyGenerator<?, ?>> generator1, Holder<ConfiguredEarlyGenerator<?, ?>> generator2, Holder<ConfiguredEarlyGenerator<?, ?>> generator3) {
			GeneratorGroup generatorGroup = this.insertEarlyGeneratorGroup(index);
			generatorGroup.addGenerator(generator1);
			generatorGroup.addGenerator(generator2);
			generatorGroup.addGenerator(generator3);
			return generatorGroup;
		}

		public GeneratorGroup insertEarlyGeneratorGroup(int index, Holder<?> ...generators) {
			GeneratorGroup generatorGroup = this.insertEarlyGeneratorGroup(index);
			for(Holder<?> holder : generators) {
				if(holder.getKey().isFor(Registries.BIOME)) {
					@SuppressWarnings("unchecked")
					Holder<ConfiguredEarlyGenerator<?, ?>> generator = (Holder<ConfiguredEarlyGenerator<?, ?>>)holder;
					generatorGroup.addGenerator(generator);
				}
			}
			return generatorGroup;
		}

		// -------- Currying Functions --------
		
		public Builder insertEarlyGroup(int index, ResourceKey<ConfiguredEarlyGenerator<?, ?>> generator) {
			this.insertEarlyGeneratorGroup(index, generator);
			return this;
		}

		public Builder insertEarlyGroup(int index, ResourceKey<ConfiguredEarlyGenerator<?, ?>> generator1, ResourceKey<ConfiguredEarlyGenerator<?, ?>> generator2) {
			this.insertEarlyGeneratorGroup(index, generator1, generator2);
			return this;
		}

		public Builder insertEarlyGroup(int index, ResourceKey<ConfiguredEarlyGenerator<?, ?>> generator1, ResourceKey<ConfiguredEarlyGenerator<?, ?>> generator2, ResourceKey<ConfiguredEarlyGenerator<?, ?>> generator3) {
			this.insertEarlyGeneratorGroup(index, generator1, generator2, generator3);
			return this;
		}

		public Builder insertEarlyGroup(int index, ResourceKey<?> ...generators) {
			this.insertEarlyGeneratorGroup(index, generators);
			return this;
		}
		
		public Builder insertEarlyGroup(int index, Holder<ConfiguredEarlyGenerator<?, ?>> generator) {
			this.insertEarlyGeneratorGroup(index, generator);
			return this;
		}

		public Builder insertEarlyGroup(int index, Holder<ConfiguredEarlyGenerator<?, ?>> generator1, Holder<ConfiguredEarlyGenerator<?, ?>> generator2) {
			this.insertEarlyGeneratorGroup(index, generator1, generator2);
			return this;
		}

		public Builder insertEarlyGroup(int index, Holder<ConfiguredEarlyGenerator<?, ?>> generator1, Holder<ConfiguredEarlyGenerator<?, ?>> generator2, Holder<ConfiguredEarlyGenerator<?, ?>> generator3) {
			this.insertEarlyGeneratorGroup(index, generator1, generator2, generator3);
			return this;
		}

		public Builder insertEarlyGroup(int index, Holder<?> ...generators) {
			this.insertEarlyGeneratorGroup(index, generators);
			return this;
		}

		// ======== Adding a new late generator group ========

		public GeneratorGroup addLateGeneratorGroup(ResourceKey<ConfiguredEarlyGenerator<?, ?>> generator) {
			GeneratorGroup generatorGroup = this.createLateGeneratorGroup();
			generatorGroup.addGenerator(this.generatorRegistry.getOrThrow(generator));
			return generatorGroup;
		}

		public GeneratorGroup addLateGeneratorGroup(ResourceKey<ConfiguredEarlyGenerator<?, ?>> generator1, ResourceKey<ConfiguredEarlyGenerator<?, ?>> generator2) {
			GeneratorGroup generatorGroup = this.createLateGeneratorGroup();
			generatorGroup.addGenerator(this.generatorRegistry.getOrThrow(generator1));
			generatorGroup.addGenerator(this.generatorRegistry.getOrThrow(generator2));
			return generatorGroup;
		}

		public GeneratorGroup addLateGeneratorGroup(ResourceKey<ConfiguredEarlyGenerator<?, ?>> generator1, ResourceKey<ConfiguredEarlyGenerator<?, ?>> generator2, ResourceKey<ConfiguredEarlyGenerator<?, ?>> generator3) {
			GeneratorGroup generatorGroup = this.createLateGeneratorGroup();
			generatorGroup.addGenerator(this.generatorRegistry.getOrThrow(generator1));
			generatorGroup.addGenerator(this.generatorRegistry.getOrThrow(generator2));
			generatorGroup.addGenerator(this.generatorRegistry.getOrThrow(generator3));
			return generatorGroup;
		}

		public GeneratorGroup addLateGeneratorGroup(ResourceKey<?> ...generators) {
			GeneratorGroup generatorGroup = this.createLateGeneratorGroup();
			for(ResourceKey<?> key : generators) {
				if(key.isFor(Registries.BIOME)) {
					@SuppressWarnings("unchecked")
					ResourceKey<ConfiguredEarlyGenerator<?, ?>> generator = (ResourceKey<ConfiguredEarlyGenerator<?, ?>>)key;
					generatorGroup.addGenerator(this.generatorRegistry.getOrThrow(generator));
				}
			}
			return generatorGroup;
		}
		
		public GeneratorGroup addLateGeneratorGroup(Holder<ConfiguredEarlyGenerator<?, ?>> generator) {
			GeneratorGroup generatorGroup = this.createLateGeneratorGroup();
			generatorGroup.addGenerator(generator);
			return generatorGroup;
		}

		public GeneratorGroup addLateGeneratorGroup(Holder<ConfiguredEarlyGenerator<?, ?>> generator1, Holder<ConfiguredEarlyGenerator<?, ?>> generator2) {
			GeneratorGroup generatorGroup = this.createLateGeneratorGroup();
			generatorGroup.addGenerator(generator1);
			generatorGroup.addGenerator(generator2);
			return generatorGroup;
		}

		public GeneratorGroup addLateGeneratorGroup(Holder<ConfiguredEarlyGenerator<?, ?>> generator1, Holder<ConfiguredEarlyGenerator<?, ?>> generator2, Holder<ConfiguredEarlyGenerator<?, ?>> generator3) {
			GeneratorGroup generatorGroup = this.createLateGeneratorGroup();
			generatorGroup.addGenerator(generator1);
			generatorGroup.addGenerator(generator2);
			generatorGroup.addGenerator(generator3);
			return generatorGroup;
		}

		public GeneratorGroup addLateGeneratorGroup(Holder<?> ...generators) {
			GeneratorGroup generatorGroup = this.createLateGeneratorGroup();
			for(Holder<?> holder : generators) {
				if(holder.getKey().isFor(Registries.BIOME)) {
					@SuppressWarnings("unchecked")
					Holder<ConfiguredEarlyGenerator<?, ?>> generator = (Holder<ConfiguredEarlyGenerator<?, ?>>)holder;
					generatorGroup.addGenerator(generator);
				}
			}
			return generatorGroup;
		}
		
		// -------- Currying Functions --------
		
		public Builder addLateGroup(ResourceKey<ConfiguredEarlyGenerator<?, ?>> generator) {
			this.addLateGeneratorGroup(generator);
			return this;
		}

		public Builder addLateGroup(ResourceKey<ConfiguredEarlyGenerator<?, ?>> generator1, ResourceKey<ConfiguredEarlyGenerator<?, ?>> generator2) {
			this.addLateGeneratorGroup(generator1, generator2);
			return this;
		}

		public Builder addLateGroup(ResourceKey<ConfiguredEarlyGenerator<?, ?>> generator1, ResourceKey<ConfiguredEarlyGenerator<?, ?>> generator2, ResourceKey<ConfiguredEarlyGenerator<?, ?>> generator3) {
			this.addLateGeneratorGroup(generator1, generator2, generator3);
			return this;
		}

		public Builder addLateGroup(ResourceKey<?> ...generators) {
			this.addLateGeneratorGroup(generators);
			return this;
		}
		
		public Builder addLateGroup(Holder<ConfiguredEarlyGenerator<?, ?>> generator) {
			this.addLateGeneratorGroup(generator);
			return this;
		}

		public Builder addLateGroup(Holder<ConfiguredEarlyGenerator<?, ?>> generator1, Holder<ConfiguredEarlyGenerator<?, ?>> generator2) {
			this.addLateGeneratorGroup(generator1, generator2);
			return this;
		}

		public Builder addLateGroup(Holder<ConfiguredEarlyGenerator<?, ?>> generator1, Holder<ConfiguredEarlyGenerator<?, ?>> generator2, Holder<ConfiguredEarlyGenerator<?, ?>> generator3) {
			this.addLateGeneratorGroup(generator1, generator2, generator3);
			return this;
		}

		public Builder addLateGroup(Holder<?> ...generators) {
			this.addLateGeneratorGroup(generators);
			return this;
		}

		// ======== Inserting an late group to an index ========

		public GeneratorGroup insertLateGeneratorGroup(int index, ResourceKey<ConfiguredEarlyGenerator<?, ?>> generator) {
			GeneratorGroup generatorGroup = this.insertLateGeneratorGroup(index);
			generatorGroup.addGenerator(this.generatorRegistry.getOrThrow(generator));
			return generatorGroup;
		}

		public GeneratorGroup insertLateGeneratorGroup(int index, ResourceKey<ConfiguredEarlyGenerator<?, ?>> generator1, ResourceKey<ConfiguredEarlyGenerator<?, ?>> generator2) {
			GeneratorGroup generatorGroup = this.insertLateGeneratorGroup(index);
			generatorGroup.addGenerator(this.generatorRegistry.getOrThrow(generator1));
			generatorGroup.addGenerator(this.generatorRegistry.getOrThrow(generator2));
			return generatorGroup;
		}

		public GeneratorGroup insertLateGeneratorGroup(int index, ResourceKey<ConfiguredEarlyGenerator<?, ?>> generator1, ResourceKey<ConfiguredEarlyGenerator<?, ?>> generator2, ResourceKey<ConfiguredEarlyGenerator<?, ?>> generator3) {
			GeneratorGroup generatorGroup = this.insertLateGeneratorGroup(index);
			generatorGroup.addGenerator(this.generatorRegistry.getOrThrow(generator1));
			generatorGroup.addGenerator(this.generatorRegistry.getOrThrow(generator2));
			generatorGroup.addGenerator(this.generatorRegistry.getOrThrow(generator3));
			return generatorGroup;
		}

		public GeneratorGroup insertLateGeneratorGroup(int index, ResourceKey<?> ...generators) {
			GeneratorGroup generatorGroup = this.insertLateGeneratorGroup(index);
			for(ResourceKey<?> key : generators) {
				if(key.isFor(Registries.BIOME)) {
					@SuppressWarnings("unchecked")
					ResourceKey<ConfiguredEarlyGenerator<?, ?>> generator = (ResourceKey<ConfiguredEarlyGenerator<?, ?>>)key;
					generatorGroup.addGenerator(this.generatorRegistry.getOrThrow(generator));
				}
			}
			return generatorGroup;
		}
		
		public GeneratorGroup insertLateGeneratorGroup(int index, Holder<ConfiguredEarlyGenerator<?, ?>> generator) {
			GeneratorGroup generatorGroup = this.insertLateGeneratorGroup(index);
			generatorGroup.addGenerator(generator);
			return generatorGroup;
		}

		public GeneratorGroup insertLateGeneratorGroup(int index, Holder<ConfiguredEarlyGenerator<?, ?>> generator1, Holder<ConfiguredEarlyGenerator<?, ?>> generator2) {
			GeneratorGroup generatorGroup = this.insertLateGeneratorGroup(index);
			generatorGroup.addGenerator(generator1);
			generatorGroup.addGenerator(generator2);
			return generatorGroup;
		}

		public GeneratorGroup insertLateGeneratorGroup(int index, Holder<ConfiguredEarlyGenerator<?, ?>> generator1, Holder<ConfiguredEarlyGenerator<?, ?>> generator2, Holder<ConfiguredEarlyGenerator<?, ?>> generator3) {
			GeneratorGroup generatorGroup = this.insertLateGeneratorGroup(index);
			generatorGroup.addGenerator(generator1);
			generatorGroup.addGenerator(generator2);
			generatorGroup.addGenerator(generator3);
			return generatorGroup;
		}

		public GeneratorGroup insertLateGeneratorGroup(int index, Holder<?> ...generators) {
			GeneratorGroup generatorGroup = this.insertLateGeneratorGroup(index);
			for(Holder<?> holder : generators) {
				if(holder.getKey().isFor(Registries.BIOME)) {
					@SuppressWarnings("unchecked")
					Holder<ConfiguredEarlyGenerator<?, ?>> generator = (Holder<ConfiguredEarlyGenerator<?, ?>>)holder;
					generatorGroup.addGenerator(generator);
				}
			}
			return generatorGroup;
		}

		// -------- Currying Functions --------
		
		public Builder insertLateGroup(int index, ResourceKey<ConfiguredEarlyGenerator<?, ?>> generator) {
			this.insertLateGeneratorGroup(index, generator);
			return this;
		}

		public Builder insertLateGroup(int index, ResourceKey<ConfiguredEarlyGenerator<?, ?>> generator1, ResourceKey<ConfiguredEarlyGenerator<?, ?>> generator2) {
			this.insertLateGeneratorGroup(index, generator1, generator2);
			return this;
		}

		public Builder insertLateGroup(int index, ResourceKey<ConfiguredEarlyGenerator<?, ?>> generator1, ResourceKey<ConfiguredEarlyGenerator<?, ?>> generator2, ResourceKey<ConfiguredEarlyGenerator<?, ?>> generator3) {
			this.insertLateGeneratorGroup(index, generator1, generator2, generator3);
			return this;
		}

		public Builder insertLateGroup(int index, ResourceKey<?> ...generators) {
			this.insertLateGeneratorGroup(index, generators);
			return this;
		}
		
		public Builder insertLateGroup(int index, Holder<ConfiguredEarlyGenerator<?, ?>> generator) {
			this.insertLateGeneratorGroup(index, generator);
			return this;
		}

		public Builder insertLateGroup(int index, Holder<ConfiguredEarlyGenerator<?, ?>> generator1, Holder<ConfiguredEarlyGenerator<?, ?>> generator2) {
			this.insertLateGeneratorGroup(index, generator1, generator2);
			return this;
		}

		public Builder insertLateGroup(int index, Holder<ConfiguredEarlyGenerator<?, ?>> generator1, Holder<ConfiguredEarlyGenerator<?, ?>> generator2, Holder<ConfiguredEarlyGenerator<?, ?>> generator3) {
			this.insertLateGeneratorGroup(index, generator1, generator2, generator3);
			return this;
		}

		public Builder insertLateGroup(int index, Holder<?> ...generators) {
			this.insertLateGeneratorGroup(index, generators);
			return this;
		}
		
		// ============ Build the GlobalEarlyGenerators from this ============
		
		public GlobalEarlyGenerators build() {
			if(this.earlyGenerators.size() == 0 && this.lateGenerators.size() == 0) {
				return GlobalEarlyGenerators.EMPTY;
			}
			
			// Filter out empty sets and create HolderSets of early generators
			List<HolderSet<ConfiguredEarlyGenerator<?, ?>>> nonEmptyEarlyGenerators = this.earlyGenerators.stream()
					.filter(GeneratorGroup::nonEmpty) // Filter empty groups
					.map(GeneratorGroup::generators)  // Map to generators
					.map(HolderSet::direct)           // Map to HolderSets
					.collect(Collectors.toUnmodifiableList());

			// Filter out empty sets and create HolderSets of late generators
			List<HolderSet<ConfiguredEarlyGenerator<?, ?>>> nonEmptyLateGenerators = this.lateGenerators.stream()
					.filter(GeneratorGroup::nonEmpty) // Filter empty groups
					.map(GeneratorGroup::generators)  // Map to generators
					.map(HolderSet::direct)           // Map to HolderSets
					.collect(Collectors.toUnmodifiableList());
			
			return new GlobalEarlyGenerators(nonEmptyEarlyGenerators, nonEmptyLateGenerators);
		}
	}

	// =================================
}
