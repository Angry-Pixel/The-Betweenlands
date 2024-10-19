package thebetweenlands.common.datagen.tags;

import java.util.concurrent.CompletableFuture;

import javax.annotation.Nullable;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.registries.BiomeRegistry;

public class BLBiomeTagProvider extends TagsProvider<Biome> {

	public static final TagKey<Biome> IN_BETWEENLANDS = TagKey.create(Registries.BIOME, TheBetweenlands.prefix("in_betweenlands"));
	public static final TagKey<Biome> CORRODING_AURA = TagKey.create(Registries.BIOME, TheBetweenlands.prefix("corroding_aura"));
	public static final TagKey<Biome> DISABLE_CORROSION = TagKey.create(Registries.BIOME, TheBetweenlands.prefix("disable_corrosion"));

	public static final TagKey<Biome> SPAWNS_SILVER_ANADIA = TagKey.create(Registries.BIOME, TheBetweenlands.prefix("spawns_silver_anadia"));
	public static final TagKey<Biome> SPAWNS_PURPLE_ANADIA = TagKey.create(Registries.BIOME, TheBetweenlands.prefix("spawns_purple_anadia"));
	public static final TagKey<Biome> SPAWNS_GREEN_ANADIA = TagKey.create(Registries.BIOME, TheBetweenlands.prefix("spawns_green_anadia"));
	public static final TagKey<Biome> DREADFUL_PEAT_MUMMY_SUMMONABLE = TagKey.create(Registries.BIOME, TheBetweenlands.prefix("dreadful_peat_mummy_summonable"));

	public BLBiomeTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper existingFileHelper) {
		super(output, Registries.BIOME, provider, TheBetweenlands.ID, existingFileHelper);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		this.tag(CORRODING_AURA);
		this.tag(DISABLE_CORROSION).add(BiomeRegistry.SWAMPLANDS_CLEARING);

		this.tag(SPAWNS_SILVER_ANADIA).add(BiomeRegistry.DEEP_WATERS);
		this.tag(SPAWNS_PURPLE_ANADIA).add(BiomeRegistry.COARSE_ISLANDS, BiomeRegistry.RAISED_ISLES);
		this.tag(SPAWNS_GREEN_ANADIA).add(BiomeRegistry.PATCHY_ISLANDS);
		this.tag(DREADFUL_PEAT_MUMMY_SUMMONABLE).add(BiomeRegistry.SLUDGE_PLAINS, BiomeRegistry.MARSH, BiomeRegistry.ERODED_MARSH);

		this.tag(IN_BETWEENLANDS).add(BiomeRegistry.COARSE_ISLANDS,
			BiomeRegistry.DEEP_WATERS, BiomeRegistry.ERODED_MARSH, BiomeRegistry.MARSH,
			BiomeRegistry.PATCHY_ISLANDS, BiomeRegistry.RAISED_ISLES, BiomeRegistry.SLUDGE_PLAINS,
			BiomeRegistry.SLUDGE_PLAINS_CLEARING, BiomeRegistry.SWAMPLANDS, BiomeRegistry.SWAMPLANDS_CLEARING);

		this.tag(BiomeTags.SPAWNS_WARM_VARIANT_FROGS).addTag(IN_BETWEENLANDS);
		this.tag(BiomeTags.WITHOUT_PATROL_SPAWNS).addTag(IN_BETWEENLANDS);
		this.tag(BiomeTags.WITHOUT_WANDERING_TRADER_SPAWNS).addTag(IN_BETWEENLANDS);
		this.tag(BiomeTags.WITHOUT_ZOMBIE_SIEGES).addTag(IN_BETWEENLANDS);
		this.tag(BiomeTags.HAS_CLOSER_WATER_FOG).addTag(IN_BETWEENLANDS);

		this.tag(Tags.Biomes.NO_DEFAULT_MONSTERS).addTag(IN_BETWEENLANDS);
	}
}
