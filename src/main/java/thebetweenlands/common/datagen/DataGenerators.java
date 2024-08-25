package thebetweenlands.common.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import thebetweenlands.common.datagen.loot.BaseLootProvider;
import thebetweenlands.common.datagen.tags.*;

import java.util.concurrent.CompletableFuture;

public class DataGenerators {

	public static void gatherData(GatherDataEvent event) {
		DataGenerator gen = event.getGenerator();
		PackOutput output = gen.getPackOutput();
		ExistingFileHelper helper = event.getExistingFileHelper();

		//Belongs in /assets/
		boolean assets = event.includeClient();
		//Belongs in /data/
		boolean data = event.includeServer();

		// Registry
		RegistryProvider datapack = new RegistryProvider(output, event.getLookupProvider());
		CompletableFuture<HolderLookup.Provider> dataProvider = datapack.getRegistryProvider();
		gen.addProvider(data, datapack);

		// Tags
		BlockTagProvider blockTags = new BlockTagProvider(output, dataProvider, helper);
		gen.addProvider(data, blockTags);
		gen.addProvider(data, new EntityTagProvider(output, dataProvider, helper));
		gen.addProvider(data, new ItemTagProvider(output, dataProvider, blockTags.contentsGetter(), helper));
		gen.addProvider(data, new FluidTagGenerator(output, dataProvider, helper));
		gen.addProvider(data, new BiomeTagProvider(output, dataProvider, helper));
		gen.addProvider(data, new DimensionTypeTagProvider(output, dataProvider, helper));

		// Misc Data
		gen.addProvider(data, new RecipeProvider(output, dataProvider));
		gen.addProvider(data, new BaseLootProvider(output, dataProvider));
		gen.addProvider(data, new DataMapProvider(output, dataProvider));

		// Assets
		gen.addProvider(assets, new AtlasProvider(output, dataProvider, helper));
		gen.addProvider(assets, new LangProvider(output));
		gen.addProvider(assets, new BlockStateProvider(output, helper));
		gen.addProvider(assets, new ItemModelProvider(output, helper));
		gen.addProvider(assets, new SoundDefinitionProvider(output, helper));
	}
}
