package thebetweenlands.common.datagen.tags;

import java.util.concurrent.CompletableFuture;

import javax.annotation.Nullable;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import thebetweenlands.common.TheBetweenlands;

public class BLDimensionTypeTagProvider extends TagsProvider<DimensionType> {

	public static final TagKey<DimensionType> DECAYING_AURA = TagKey.create(Registries.DIMENSION_TYPE, TheBetweenlands.prefix("decaying_aura"));
	public static final TagKey<DimensionType> CORRODING_AURA = TagKey.create(Registries.DIMENSION_TYPE, TheBetweenlands.prefix("corroding_aura"));

	public BLDimensionTypeTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper existingFileHelper) {
		super(output, Registries.DIMENSION_TYPE, provider, TheBetweenlands.ID, existingFileHelper);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		//TODO REMOVE OVERWORLD AFTER TESTING
		this.tag(DECAYING_AURA).add(BuiltinDimensionTypes.OVERWORLD);
		this.tag(CORRODING_AURA).add(BuiltinDimensionTypes.OVERWORLD);
	}
}
