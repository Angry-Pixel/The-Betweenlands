package thebetweenlands.common.world.biome;

import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biome.BiomeBuilder;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.FeatureRegistries;
import thebetweenlands.common.world.WorldEventsHook;
import thebetweenlands.common.world.gen.biome.generator.BiomeGenerator;

// The Betweenlands biome wrapper (Maybe going to extend biome class in future with ATs)
// TODO: port more features to vanilla feature type
public abstract class BiomeBetweenlands {
	
	public String biomeName;

	public BiomeGenerator biomeGenerator;

	public RegistryObject<Biome> biome;
	public int biomeWeight;					// used only in betweenlands generator
	public int id;							// equals index in biome reg list
	
	public MobSpawnSettings.Builder mobSpawnSettings = new MobSpawnSettings.Builder();
	public BiomeSpecialEffects.Builder biomeSpecialFX = new BiomeSpecialEffects.Builder();
	public BiomeGenerationSettings.Builder biomeGenSettings = new BiomeGenerationSettings.Builder();
	public BiomeBuilder biomeBuilder = new BiomeBuilder();
	
	private int[] fogColorRGB = new int[]{(int) 255, (int) 255, (int) 255};
	
	public BiomeBetweenlands() {
		this.biomeWeight = 10;
	}
	
	// registers the biome using deferred register
	public BiomeBetweenlands register(DeferredRegister<Biome> registry, int index) {
		this.id = index;
		if (this.biomeGenerator != null) {
			this.biomeGenerator.biomeID = index;
		}

		this.biome = registry.register(this.biomeName, this::biomeBuilder);

		return this;
	}

	// Build biome provider
	// Final chance to register features Hector...
	// The most information for biome generation can be found now
	public abstract Biome biomeBuilder();
	
	// set biome weight
	public void setWeight(int value) {
		this.biomeWeight = value;
	}
	
	public int[] getFogRGB() {
		return this.fogColorRGB;
	}

	// add vanilla features
	public void setFeatures() {
		WorldEventsHook.regiserFeature(this.biome.getId(),GenerationStep.Decoration.VEGETAL_DECORATION, FeatureRegistries.PlacedFeatures.GIANT_ROOTS);
		WorldEventsHook.regiserFeature(this.biome.getId(),GenerationStep.Decoration.UNDERGROUND_ORES, FeatureRegistries.PlacedFeatures.SULFUR);
		WorldEventsHook.regiserFeature(this.biome.getId(),GenerationStep.Decoration.UNDERGROUND_ORES, FeatureRegistries.PlacedFeatures.SYRMORITE);
		WorldEventsHook.regiserFeature(this.biome.getId(),GenerationStep.Decoration.UNDERGROUND_ORES, FeatureRegistries.PlacedFeatures.BONE_ORE);
		WorldEventsHook.regiserFeature(this.biome.getId(),GenerationStep.Decoration.UNDERGROUND_ORES, FeatureRegistries.PlacedFeatures.OCTINE);
		WorldEventsHook.regiserFeature(this.biome.getId(),GenerationStep.Decoration.UNDERGROUND_ORES, FeatureRegistries.PlacedFeatures.SWAMP_DIRT);
		WorldEventsHook.regiserFeature(this.biome.getId(),GenerationStep.Decoration.UNDERGROUND_ORES, FeatureRegistries.PlacedFeatures.LIMESTONE);
		WorldEventsHook.regiserFeature(this.biome.getId(),GenerationStep.Decoration.UNDERGROUND_ORES, FeatureRegistries.PlacedFeatures.VALONITE);
		WorldEventsHook.regiserFeature(this.biome.getId(),GenerationStep.Decoration.UNDERGROUND_ORES, FeatureRegistries.PlacedFeatures.SCABYST);
	}

	public void setSpawns() {
	}

	public Block topBlock(){
		return BlockRegistry.BETWEENSTONE.get();
	}

	public abstract float getBaseHeight();
	public abstract float getHeightVariation();
}
