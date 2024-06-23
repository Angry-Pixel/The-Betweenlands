package thebetweenlands.common.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import thebetweenlands.common.TheBetweenlands;

public class BetweenlandsBlockStateProvider extends BlockStateProvider {

	public BetweenlandsBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
		super(output, TheBetweenlands.ID, exFileHelper);
	}

	@Override
	protected void registerStatesAndModels() {
		// Block models
		//BlockModelBuilder SwampGrassModel = models().getBuilder(BlockRegistry.SWAMP_GRASS.get().getRegistryName().getPath())
		//        .parent(models().getExistingFile(mcLoc("cube")))
		//        .customLoader((blockModelBuilder, helper) -> new CustomLoaderBuilder<BlockModelBuilder>(TheBetweenlands.prefix("swamp_grass"), blockModelBuilder, helper) { })
		//        .end();
		//BlockModelBuilder SwampGrassModelTuft0 = models().getBuilder(TheBetweenlands.prefix("swamp_grass_tuft0").getPath())
		//        .parent(SwampGrassModel)
		//        .customLoader((blockModelBuilder, helper) -> new CustomLoaderBuilder<BlockModelBuilder>(TheBetweenlands.prefix("swamp_grass_tuft0"), blockModelBuilder, helper) { })
		//        .end();
		//BlockModelBuilder SwampGrassModelTuft1 = models().getBuilder(TheBetweenlands.prefix("swamp_grass_tuft1").getPath())
		//        .parent(SwampGrassModel)
		//        .customLoader((blockModelBuilder, helper) -> new CustomLoaderBuilder<BlockModelBuilder>(TheBetweenlands.prefix("swamp_grass_tuft1"), blockModelBuilder, helper) { })
		//        .end();

		// Item models (just casts block model generator to item model generator)
		//ItemModelBuilder SwampGrassItemModel = itemModels().getBuilder(BlockRegistry.SWAMP_GRASS.get().getRegistryName().getPath())
		//		.parent(SwampGrassModel).customLoader((ItemModelBuilder, helper) -> new CustomLoaderBuilder<ItemModelBuilder>(TheBetweenlands.prefix("swamp_grass"), ItemModelBuilder, helper) { })
		//		.end();

		// Block states
		//VariantBlockStateBuilder SwampGrassBlockState = getVariantBuilder(BlockRegistry.SWAMP_GRASS.get())
		//        .partialState().setModels(new ConfiguredModel(SwampGrassModel, 0, 0, false, 2), new ConfiguredModel(SwampGrassModelTuft0, 0, 0, false, 2));
	}
}
