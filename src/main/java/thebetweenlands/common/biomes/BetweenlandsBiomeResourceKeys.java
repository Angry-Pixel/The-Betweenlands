package thebetweenlands.common.biomes;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.carver.CarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CaveCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import thebetweenlands.common.TheBetweenlands;

public class BetweenlandsBiomeResourceKeys {
	
	// Use this to avoid repeat function calls for biome ResourceKey vars
	public static final ResourceKey<Biome> SWAMPLANDS = ResourceKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(TheBetweenlands.ID, "betweenlands_swamplands"));
	public static final ResourceKey<Biome> SLUGEPLAINS = ResourceKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(TheBetweenlands.ID, "betweenlands_slugeplains"));
	public static final ResourceKey<Biome> DEEPWATERS = ResourceKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(TheBetweenlands.ID, "betweenlands_deepwaters"));
}
