package thebetweenlands.common.registries;

import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.world.biome.*;

import javax.annotation.CheckForNull;
import java.util.ArrayList;
import java.util.List;

// When i finish up the features and
public class BiomeRegistry {
	// Biome registry
	public static final DeferredRegister<Biome> BIOMES = DeferredRegister.create(ForgeRegistries.BIOMES, TheBetweenlands.ID);

	// Biomes
	// Static references just for ease of use
	public static final BiomeBetweenlands PATCHY_ISLANDS = new BiomePatchyIslands();
    public static final BiomeBetweenlands SWAMPLANDS = new BiomeSwamplands();
    public static final BiomeBetweenlands DEEP_WATERS = new BiomeDeepWaters();
    public static final BiomeBetweenlands COARSE_ISLANDS = new BiomeCoarseIslands();
    public static final BiomeBetweenlands RAISED_ISLES = new BiomeRaisedIsles();
    public static final BiomeBetweenlands SLUDGE_PLAINS = new BiomeSludgePlains();
    public static final BiomeBetweenlands ERODED_MARSH = new BiomeErodedMarsh();
    public static final BiomeBetweenlands MARSH = new BiomeMarsh();
	public static final BiomeBetweenlands SWAMPLANDS_CLEARING = new BiomeSwamplandsClearing();
	public static final BiomeBetweenlands SLUDGE_PLAINS_CLEARING = new BiomeSludgePlainsClearing();


	// List of biomes to generate: TODO: make json loader for data pack added biomes, and api for mods
	// The betweenlands generator registry
	// TODO: store betweenlands generator registry in the generator instance for data pack use
	public static List<BiomeBetweenlands> BETWEENLANDS_DIM_BIOME_REGISTRY = new ArrayList<>();

	// add biomes to the betweenlands
	public static void registerBiomes(BiomeBetweenlands... inbiome) {
		// add to DeferredRegister
		for (BiomeBetweenlands biome : inbiome) {
			biome.register(BIOMES, BETWEENLANDS_DIM_BIOME_REGISTRY.size());
		}
	}

	// Get betweenlands biome from holder
	@CheckForNull
	public static BiomeBetweenlands getBiomeBetweenlands(Holder<Biome> biome) {
		for (BiomeBetweenlands ent : BETWEENLANDS_DIM_BIOME_REGISTRY) {
			if (biome.is(ent.biome.getId()))
				return ent;
		}

		// No match
		return null;
	}

	// Register biomes to minecraft
	public static void register(IEventBus eventBus) {

		// just incase some maniac wants to replace all biomes (vanilla biomes)
		if (TheBetweenlands.useVanillaBiomes) {
			// add vanilla biomes to list
			BETWEENLANDS_DIM_BIOME_REGISTRY.add(PATCHY_ISLANDS.register(BIOMES, BETWEENLANDS_DIM_BIOME_REGISTRY.size()));
			BETWEENLANDS_DIM_BIOME_REGISTRY.add(SWAMPLANDS.register(BIOMES, BETWEENLANDS_DIM_BIOME_REGISTRY.size()));
			BETWEENLANDS_DIM_BIOME_REGISTRY.add(DEEP_WATERS.register(BIOMES, BETWEENLANDS_DIM_BIOME_REGISTRY.size()));
			BETWEENLANDS_DIM_BIOME_REGISTRY.add(COARSE_ISLANDS.register(BIOMES, BETWEENLANDS_DIM_BIOME_REGISTRY.size()));
			BETWEENLANDS_DIM_BIOME_REGISTRY.add(RAISED_ISLES.register(BIOMES, BETWEENLANDS_DIM_BIOME_REGISTRY.size()));
			BETWEENLANDS_DIM_BIOME_REGISTRY.add(SLUDGE_PLAINS.register(BIOMES, BETWEENLANDS_DIM_BIOME_REGISTRY.size()));
			BETWEENLANDS_DIM_BIOME_REGISTRY.add(ERODED_MARSH.register(BIOMES, BETWEENLANDS_DIM_BIOME_REGISTRY.size()));
			BETWEENLANDS_DIM_BIOME_REGISTRY.add(MARSH.register(BIOMES, BETWEENLANDS_DIM_BIOME_REGISTRY.size()));
			BETWEENLANDS_DIM_BIOME_REGISTRY.add(SWAMPLANDS_CLEARING.register(BIOMES, BETWEENLANDS_DIM_BIOME_REGISTRY.size()));
			BETWEENLANDS_DIM_BIOME_REGISTRY.add(SLUDGE_PLAINS_CLEARING.register(BIOMES, BETWEENLANDS_DIM_BIOME_REGISTRY.size()));
		}

		// Register
		BIOMES.register(eventBus);
	}
}
