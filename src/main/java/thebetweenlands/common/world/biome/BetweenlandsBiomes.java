package thebetweenlands.common.world.biome;

// Builds betweenlands biomes
// Features are added in WorldEventsHook
public class BetweenlandsBiomes {
    /*
    public static Biome Swamplands() {
        // Biome carvers and features
        this.biomeGenSettings.addCarver(GenerationStep.Carving.AIR, CarverRegistries.ConfigueredCarvers.CAVES_BETWEENLANDS.getHolder().get())
                .addCarver(GenerationStep.Carving.AIR, CarverRegistries.ConfigueredCarvers.BETWEENLANDS_RAVINES.getHolder().get());

        // Biome colors and other effects
        this.biomeSpecialFX.foliageColorOverride(5418842)
                .fogColor(0x0a1e16)
                .waterColor(0x184220)
                .waterFogColor(0x184220)
                .skyColor(0xff78A7FF)
                .backgroundMusic(MusicHandler.BETWEENLANDS_MUSIC)
                .grassColorModifier(BiomeSpecialEffects.GrassColorModifier.create("betweenlands_swamplands", "betweenlands_swamplands", (x, z, color) -> ((BetweenlandsColorModifiers.Betweenlands2ColorMod((int)x, (int)z, 2817792, 5283904, 0.07d)))));
        //this.setFoliageColors(0x2AFF00, 0x52AF5A);
        //this.setSecondaryFoliageColors(0x50a040, 0x85af51);

        // Build Biome
        this.biomeBuilder.generationSettings(this.biomeGenSettings.build())
                .mobSpawnSettings(this.mobSpawnSettings.build())
                .specialEffects(this.biomeSpecialFX.build())
                .downfall(0.9f)
                .temperature(0.8f)
                .biomeCategory(Biome.BiomeCategory.SWAMP)
                .precipitation(Biome.Precipitation.RAIN)
                .build();

        // Biome decorator
        this.biomeGenerator = new BiomeGenerator(new BiomeDecorator(List.of(new FlatLandFeature(TheBetweenlands.LAYER_HEIGHT, 8),
                new SiltBeachFeature(0.98F), new PatchFeature(0.18D, 0.18D, BlockRegistry.SWAMP_GRASS.get().defaultBlockState()),
                new PatchFeature(0.05D, 0.05D, BlockRegistry.SWAMP_GRASS.get().defaultBlockState()),
                new PatchFeature(0.74D, 0.74D, BlockRegistry.SWAMP_DIRT.get().defaultBlockState()),
                new PatchFeature(0.65D, 0.65D, BlockRegistry.MUD.get().defaultBlockState(), 1.0D / 1.35D, 1.72D)
        ), List.of(
                //new DecoratorFunction(80, DecorationHelper::generateBiomeDebug)),
                new DecoratorFunction(80, DecorationHelper::generateWeedwoodTree),
                new DecoratorFunction(100, DecorationHelper::generateNibbletwigTree),
                new DecoratorFunction(4, DecorationHelper::generateRubberTree),
                new DecoratorFunction(8, DecorationHelper::generateSapTree)),
                this.id));

        return this.biomeBuilder.build();
    }
    */
}
