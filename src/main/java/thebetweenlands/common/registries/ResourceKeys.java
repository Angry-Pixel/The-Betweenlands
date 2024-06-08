package thebetweenlands.common.registries;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import thebetweenlands.TheBetweenlands;

public class ResourceKeys {
	
	// Recource locations
	public static final ResourceLocation SWAMPLANDS_RESOURCE = new ResourceLocation(TheBetweenlands.ID, "betweenlands_swamplands");
	public static final ResourceLocation SLUGEPLAINS_RESOURCE = new ResourceLocation(TheBetweenlands.ID, "betweenlands_sluge_plains");
	public static final ResourceLocation DEEPWATERS_RESOURCE = new ResourceLocation(TheBetweenlands.ID, "betweenlands_deep_waters");
	public static final ResourceLocation MARSH_RESOURCE = new ResourceLocation(TheBetweenlands.ID, "betweenlands_marsh");
	public static final ResourceLocation ERODED_MARSH_RESOURCE = new ResourceLocation(TheBetweenlands.ID, "betweenlands_eroded_marsh");
	public static final ResourceLocation PATCHY_ISLANDS_RESOURCE = new ResourceLocation(TheBetweenlands.ID, "betweenlands_patchy_islands");
	public static final ResourceLocation CORSE_ISLANDS_RESOURCE = new ResourceLocation(TheBetweenlands.ID, "betweenlands_coarse_islands");
	public static final ResourceLocation RASED_ISLES_RESOURCE = new ResourceLocation(TheBetweenlands.ID, "betweenlands_rased_isles");
	public static final ResourceLocation SWAMPLANDS_CLEARING_RESOURCE = new ResourceLocation(TheBetweenlands.ID, "betweenlands_sluge_plains_clearing");
	public static final ResourceLocation SLUGEPLAINS_CLEARING_RESOURCE = new ResourceLocation(TheBetweenlands.ID, "betweenlands_swamplands_clearing");

	// Register keys to registery
	public static final ResourceKey<Biome> SWAMPLANDS = ResourceKey.create(Registry.BIOME_REGISTRY, SWAMPLANDS_RESOURCE);
	public static final ResourceKey<Biome> SLUGEPLAINS = ResourceKey.create(Registry.BIOME_REGISTRY, SLUGEPLAINS_RESOURCE);
	public static final ResourceKey<Biome> DEEPWATERS = ResourceKey.create(Registry.BIOME_REGISTRY, DEEPWATERS_RESOURCE);
	public static final ResourceKey<Biome> MARSH = ResourceKey.create(Registry.BIOME_REGISTRY, MARSH_RESOURCE);
	public static final ResourceKey<Biome> ERODED_MARSH = ResourceKey.create(Registry.BIOME_REGISTRY, ERODED_MARSH_RESOURCE);
	public static final ResourceKey<Biome> PATCHY_ISLANDS = ResourceKey.create(Registry.BIOME_REGISTRY, PATCHY_ISLANDS_RESOURCE);
	public static final ResourceKey<Biome> CORSE_ISLANDS = ResourceKey.create(Registry.BIOME_REGISTRY, CORSE_ISLANDS_RESOURCE);
	public static final ResourceKey<Biome> RASED_ISLES = ResourceKey.create(Registry.BIOME_REGISTRY, RASED_ISLES_RESOURCE);
	public static final ResourceKey<Biome> SWAMPLANDS_CLEARING = ResourceKey.create(Registry.BIOME_REGISTRY, SWAMPLANDS_CLEARING_RESOURCE);
	public static final ResourceKey<Biome> SLUGEPLAINS_CLEARING = ResourceKey.create(Registry.BIOME_REGISTRY, SLUGEPLAINS_CLEARING_RESOURCE);
}
