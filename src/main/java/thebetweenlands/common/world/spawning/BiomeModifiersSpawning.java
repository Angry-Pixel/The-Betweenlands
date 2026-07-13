package thebetweenlands.common.world.spawning;

import java.util.List;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.registries.BiomeRegistry;
import thebetweenlands.common.registries.EntityRegistry;

public class BiomeModifiersSpawning {

    public static final ResourceKey<BiomeModifier> PATCHY_ISLANDS_MODIFIER = makeKey("patchy_islands_spawns");
    public static final ResourceKey<BiomeModifier> SWAMPLANDS_MODIFIER = makeKey("swamplands_spawns");
    public static final ResourceKey<BiomeModifier> DEEP_WATERS_MODIFIER = makeKey("deep_waters_spawns");
    public static final ResourceKey<BiomeModifier> COARSE_ISLANDS_MODIFIER = makeKey("coarse_islands_spawns");
    public static final ResourceKey<BiomeModifier> RAISED_ISLES_MODIFIER = makeKey("raised_isles_spawns");
    public static final ResourceKey<BiomeModifier> SLUDGE_PLAINS_MODIFIER = makeKey("sludge_plains_spawns");
    public static final ResourceKey<BiomeModifier> ERODED_MARSH_MODIFIER = makeKey("eroded_marsh_spawns");
    public static final ResourceKey<BiomeModifier> MARSH_MODIFIER = makeKey("marsh_spawns");
    public static final ResourceKey<BiomeModifier> SWAMPLANDS_CLEARING_MODIFIER = makeKey("swamplands_clearing_spawns");
    public static final ResourceKey<BiomeModifier> SLUDGE_PLAINS_CLEARING_MODIFIER = makeKey("sludge_plains_clearing_spawns");

    public static void bootstrap(BootstrapContext<BiomeModifier> context) {
        var biomes = context.lookup(Registries.BIOME);

        registerSpawnsForBiome(context, PATCHY_ISLANDS_MODIFIER, biomes, BiomeRegistry.PATCHY_ISLANDS, List.of(
            new MobSpawnSettings.SpawnerData(EntityRegistry.DRAGONFLY.get(), 35, 1, 3),
            new MobSpawnSettings.SpawnerData(EntityRegistry.FIREFLY.get(), 60, 1, 3),
            new MobSpawnSettings.SpawnerData(EntityRegistry.MIRE_SNAIL.get(), 60, 1, 5),
            new MobSpawnSettings.SpawnerData(EntityRegistry.FROG.get(), 32, 1, 3),
            new MobSpawnSettings.SpawnerData(EntityRegistry.OLM.get(), 30, 3, 5),
            new MobSpawnSettings.SpawnerData(EntityRegistry.GECKO.get(), 40, 1, 3),
            new MobSpawnSettings.SpawnerData(EntityRegistry.SPORELING.get(), 80, 2, 5),
            new MobSpawnSettings.SpawnerData(EntityRegistry.GREEBLING.get(), 20, 1, 3),
            new MobSpawnSettings.SpawnerData(EntityRegistry.EMBERLING.get(), 20, 1, 1),
            new MobSpawnSettings.SpawnerData(EntityRegistry.LURKER.get(), 35, 1, 3),
            new MobSpawnSettings.SpawnerData(EntityRegistry.ANGLER.get(), 45, 1, 3),
            new MobSpawnSettings.SpawnerData(EntityRegistry.SWAMP_HAG.get(), 90, 1, 3),
            new MobSpawnSettings.SpawnerData(EntityRegistry.WIGHT.get(), 16, 1, 1),
            new MobSpawnSettings.SpawnerData(EntityRegistry.SILT_CRAB.get(), 50, 2, 8),
            new MobSpawnSettings.SpawnerData(EntityRegistry.BLOOD_SNAIL.get(), 30, 1, 3),
            new MobSpawnSettings.SpawnerData(EntityRegistry.LEECH.get(), 35, 1, 3),
            new MobSpawnSettings.SpawnerData(EntityRegistry.CHIROMAW.get(), 40, 1, 3),
            new MobSpawnSettings.SpawnerData(EntityRegistry.CHIROMAW_GREEBLING_RIDER.get(), 20, 1, 3),
            new MobSpawnSettings.SpawnerData(EntityRegistry.STALKER.get(), 13, 1, 1),
            new MobSpawnSettings.SpawnerData(EntityRegistry.INFESTATION.get(), 50, 1, 1),
            new MobSpawnSettings.SpawnerData(EntityRegistry.SHAMBLER.get(), 30, 1, 3),
            new MobSpawnSettings.SpawnerData(EntityRegistry.ANADIA.get(), 60, 1, 5),
            new MobSpawnSettings.SpawnerData(EntityRegistry.CAVE_FISH.get(), 30, 1, 3)
        ));
        
        registerSpawnsForBiome(context, SWAMPLANDS_MODIFIER, biomes, BiomeRegistry.SWAMPLANDS, List.of(
        		
        	));

        	registerSpawnsForBiome(context, DEEP_WATERS_MODIFIER, biomes, BiomeRegistry.DEEP_WATERS, List.of(

        	));

        	registerSpawnsForBiome(context, COARSE_ISLANDS_MODIFIER, biomes, BiomeRegistry.COARSE_ISLANDS, List.of(

        	    ));

        	registerSpawnsForBiome(context, RAISED_ISLES_MODIFIER, biomes, BiomeRegistry.RAISED_ISLES, List.of(

        	    ));

        	registerSpawnsForBiome(context, SLUDGE_PLAINS_MODIFIER, biomes, BiomeRegistry.SLUDGE_PLAINS, List.of(
     
        	    ));

        	registerSpawnsForBiome(context, ERODED_MARSH_MODIFIER, biomes, BiomeRegistry.ERODED_MARSH, List.of(

        	    ));

        	registerSpawnsForBiome(context, MARSH_MODIFIER, biomes, BiomeRegistry.MARSH, List.of(

        	    ));

        	registerSpawnsForBiome(context, SWAMPLANDS_CLEARING_MODIFIER, biomes, BiomeRegistry.SWAMPLANDS_CLEARING, List.of(
 
        	    ));

        	registerSpawnsForBiome(context, SLUDGE_PLAINS_CLEARING_MODIFIER, biomes, BiomeRegistry.SLUDGE_PLAINS_CLEARING, List.of(

        	    ));
    }

    private static void registerSpawnsForBiome(BootstrapContext<BiomeModifier> context, ResourceKey<BiomeModifier> modifierKey, HolderGetter<Biome> biomeLookup, ResourceKey<Biome> biomeKey, List<MobSpawnSettings.SpawnerData> spawns) {
		HolderSet<Biome> targetBiome = HolderSet.direct(biomeLookup.getOrThrow(biomeKey));
		context.register(modifierKey, new BiomeModifiers.AddSpawnsBiomeModifier(targetBiome, spawns));
	}

    private static ResourceKey<BiomeModifier> makeKey(String name) {
        return ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, TheBetweenlands.prefix(name));
    }
}
