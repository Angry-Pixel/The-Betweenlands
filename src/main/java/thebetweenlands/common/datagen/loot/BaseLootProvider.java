package thebetweenlands.common.datagen.loot;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.WritableRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import thebetweenlands.common.registries.LootTableRegistry;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class BaseLootProvider extends LootTableProvider {

	public BaseLootProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
		super(output, LootTableRegistry.getAllBuiltInTables(), List.of(
			new LootTableProvider.SubProviderEntry(EntityLootProvider::new, LootContextParamSets.ENTITY),
			new LootTableProvider.SubProviderEntry(provider1 -> new MiscLootProvider(), LootContextParamSets.EMPTY)
		), provider);
	}

	@Override
	protected void validate(WritableRegistry<LootTable> registry, ValidationContext context, ProblemReporter.Collector collector) {

	}
}
