package thebetweenlands.common.datagen;

import java.util.concurrent.CompletableFuture;

import javax.annotation.Nullable;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import thebetweenlands.common.TheBetweenlands;

public class BetweenlandsDimensionTagProvider extends IntrinsicHolderTagsProvider<Level> {

	public static final TagKey<Level> DECAYING_AURA = TagKey.create(Registries.DIMENSION, TheBetweenlands.prefix("decaying_aura"));
	public static final TagKey<Level> CORRODING_AURA = TagKey.create(Registries.DIMENSION, TheBetweenlands.prefix("corroding_aura"));

	public BetweenlandsDimensionTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper existingFileHelper) {
		super(output, Registries.DIMENSION, provider, level -> level.dimension(), TheBetweenlands.ID, existingFileHelper);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		//TODO REMOVE OVERWORLD AFTER TESTING
		this.tag(DECAYING_AURA).add(Level.OVERWORLD);
		this.tag(CORRODING_AURA).add(Level.OVERWORLD);
	}
}
