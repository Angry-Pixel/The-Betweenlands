package thebetweenlands.common.world;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.Event;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.registries.BiomeRegistry;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

// where we add structures and features to biomes
@Mod.EventBusSubscriber(modid = TheBetweenlands.ID)
public class WorldEventsHook {

	// List of biomes to add features to
	// only called once, so I can get away with slower string comparisons
	public static List<appendFeatureRegister> appendFeatureList = new ArrayList();
	public static List<appendSpawnRegister> appendSpawnList = new ArrayList();

	// Register feature
	// call this to add a feature to a specified biome
	public static void regiserFeature(ResourceLocation biomeName, GenerationStep.Decoration generationStep, Holder<PlacedFeature> placedFeatureHolder) {
		appendFeatureList.add(new appendFeatureRegister(biomeName, generationStep, placedFeatureHolder));
	}

	// Register spawn
	// call this to add a mob spawn to a specified biome
	public static void regiserSpawn(ResourceLocation biomeName, MobCategory mobCategory, MobSpawnSettings.SpawnerData spawnerData) {
		appendSpawnList.add(new appendSpawnRegister(biomeName, mobCategory, spawnerData));
	}

	// The Betweenlands world events manager

	// This method also seems to produce the least issues with out-of-order definitions
	// (DeferredRegister and manual definitions should work with this)

	// TODO: call registerer to load in all features even when BiomeLoadingEvent is not needed, eg: on world reload features will fail to show in placeFeature command

	public static boolean firstcall = true;

	@SubscribeEvent
	public static void dimEvent(final WorldEvent.Load event) {
	}


	@SubscribeEvent
	public static void biomeLoadingEvent(final BiomeLoadingEvent event) {

		// TODO: find an event perfect for these register calls
		if (firstcall) {

			// add dark druid circle
			// regiserFeature(Biomes.SWAMP.getRegistryName(), GenerationStep.Decoration.SURFACE_STRUCTURES, FeatureRegistries.PlacedFeatures.DURID_CIRCLE);

			BiomeRegistry.BETWEENLANDS_DIM_BIOME_REGISTRY.forEach((biome) -> {
				biome.setFeatures();
				biome.setSpawns();
			});
		}
		firstcall = false;

		// Features
		for (appendFeatureRegister entry : appendFeatureList) {
			// biome name check
			if (event.getName().compareTo(entry.name) == 0) {
				event.getGeneration().addFeature(entry.generationStep, entry.placedFeatureHolder);
			}
		}

		// Spawns
		for (appendSpawnRegister entry : appendSpawnList) {
			// biome name check
			if (event.getName().compareTo(entry.name) == 0) {
				event.getSpawns().addSpawn(entry.category, entry.spawnerData);
			}
		}

		// First add features
		//event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, FeatureRegistries.PlacedFeatures.SULFUR);
		//event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, FeatureRegistries.PlacedFeatures.SYRMORITE);
		//event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, FeatureRegistries.PlacedFeatures.BONE_ORE);
		//event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, FeatureRegistries.PlacedFeatures.OCTINE);
		//event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, FeatureRegistries.PlacedFeatures.SWAMP_DIRT);
		//event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, FeatureRegistries.PlacedFeatures.LIMESTONE);
		//event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, FeatureRegistries.PlacedFeatures.VALONITE);
		//event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, FeatureRegistries.PlacedFeatures.SCABYST);
	}

	static class appendFeatureRegister {
		public Holder<PlacedFeature> placedFeatureHolder;
		public GenerationStep.Decoration generationStep;
		public ResourceLocation name;

		// Use if using a hardcoded feature
		public appendFeatureRegister (ResourceLocation name, GenerationStep.Decoration generationStep, Holder<PlacedFeature> placedFeatureHolder) {
			this.name = name;
			this.generationStep = generationStep;
			this.placedFeatureHolder = placedFeatureHolder;
		}
	}

	static class appendSpawnRegister {
		public MobSpawnSettings.SpawnerData spawnerData;
		public MobCategory category;
		public ResourceLocation name;

		// Use if using a hardcoded feature
		public appendSpawnRegister (ResourceLocation name, MobCategory category, MobSpawnSettings.SpawnerData spawnerData) {
			this.name = name;
			this.category = category;
			this.spawnerData = spawnerData;
		}
	}
}
