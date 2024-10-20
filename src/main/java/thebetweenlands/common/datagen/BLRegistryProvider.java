package thebetweenlands.common.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import thebetweenlands.api.BLRegistries;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.registries.*;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class BLRegistryProvider extends DatapackBuiltinEntriesProvider {

	public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
		.add(Registries.BIOME, BiomeRegistry::bootstrap)
		.add(Registries.CONFIGURED_FEATURE, (T) -> ConfiguredFeatureRegistry.bootstrap(T))
		.add(Registries.PLACED_FEATURE, PlacedFeatureRegistry::bootstrap)
		.add(Registries.STRUCTURE, StructureRegistry::bootstrap)
		.add(Registries.STRUCTURE_SET, StructureRegistry::bootstrapSet)
//		.add(Registries.CONFIGURED_CARVER, CarverRegistry::bootstrap)
		.add(Registries.LEVEL_STEM, DimensionRegistries::bootstrapStem)
		.add(Registries.DIMENSION_TYPE, DimensionRegistries::bootstrapType)
		.add(Registries.NOISE_SETTINGS, DimensionRegistries::bootstrapNoise)
		.add(Registries.JUKEBOX_SONG, MusicRegistry::bootstrap)
		.add(BLRegistries.Keys.ASPECT_ITEMS, AspectItemRegistry::bootstrap)
		.add(BLRegistries.Keys.ASPECT_TYPES, AspectTypeRegistry::bootstrap)
		.add(BLRegistries.Keys.ELIXIR_RECIPES, ElixirRecipeRegistry::bootstrap)
		.add(Registries.DAMAGE_TYPE, DamageTypeRegistry::bootstrap)
		.add(BLRegistries.Keys.FROG_VARIANT, FrogVariantRegistry::bootstrap);

	public BLRegistryProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
		super(output, provider, BUILDER, Set.of("minecraft", TheBetweenlands.ID));
	}
}
