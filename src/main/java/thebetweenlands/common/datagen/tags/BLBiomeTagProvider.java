package thebetweenlands.common.datagen.tags;

import java.util.concurrent.CompletableFuture;

import javax.annotation.Nullable;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.registries.BiomeRegistry;

public class BLBiomeTagProvider extends TagsProvider<Biome> {

	public static final TagKey<Biome> CORRODING_AURA = TagKey.create(Registries.BIOME, TheBetweenlands.prefix("corroding_aura"));
	public static final TagKey<Biome> DISABLE_CORROSION = TagKey.create(Registries.BIOME, TheBetweenlands.prefix("disable_corrosion"));

	public static final TagKey<Biome> SPAWNS_SILVER_ANADIA = TagKey.create(Registries.BIOME, TheBetweenlands.prefix("spawns_silver_anadia"));
	public static final TagKey<Biome> SPAWNS_PURPLE_ANADIA = TagKey.create(Registries.BIOME, TheBetweenlands.prefix("spawns_purple_anadia"));
	public static final TagKey<Biome> SPAWNS_GREEN_ANADIA = TagKey.create(Registries.BIOME, TheBetweenlands.prefix("spawns_green_anadia"));

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
	}
}
