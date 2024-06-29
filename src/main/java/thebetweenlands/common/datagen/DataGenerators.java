package thebetweenlands.common.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

public class DataGenerators {

	public static void gatherData(GatherDataEvent event) {
		DataGenerator gen = event.getGenerator();
		PackOutput output = gen.getPackOutput();
		CompletableFuture<HolderLookup.Provider> provider = event.getLookupProvider();
		ExistingFileHelper helper = event.getExistingFileHelper();

		//Belongs in /assets/
		boolean assets = event.includeClient();
		//Belongs in /data/
		boolean data = event.includeServer();

		// Data
		gen.addProvider(data, new BetweenlandsRegistryProvider(output, provider));
		gen.addProvider(data, new BetweenlandsBlockTagsProvider(output, provider, helper));

		// Assets
		gen.addProvider(assets, new BetweenlandsLangProvider(output));
		gen.addProvider(assets, new BetweenlandsBlockStateProvider(output, helper));
		gen.addProvider(assets, new BetweenlandsSoundDefinitionsProvider(output, helper));
	}
}
