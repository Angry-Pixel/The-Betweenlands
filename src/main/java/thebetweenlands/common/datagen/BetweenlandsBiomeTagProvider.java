package thebetweenlands.common.datagen;

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

public class BetweenlandsBiomeTagProvider extends TagsProvider<Biome> {

	public static final TagKey<Biome> CORRODING_AURA = TagKey.create(Registries.BIOME, TheBetweenlands.prefix("corroding_aura"));
	public static final TagKey<Biome> DISABLE_CORROSION = TagKey.create(Registries.BIOME, TheBetweenlands.prefix("disable_corrosion"));

	public BetweenlandsBiomeTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper existingFileHelper) {
		super(output, Registries.BIOME, provider, TheBetweenlands.ID, existingFileHelper);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		this.tag(CORRODING_AURA);
		this.tag(DISABLE_CORROSION).add(BiomeRegistry.SWAMPLANDS_CLEARING);
	}
}
