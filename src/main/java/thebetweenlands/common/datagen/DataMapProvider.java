package thebetweenlands.common.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import thebetweenlands.common.items.datamaps.DecayFood;
import thebetweenlands.common.registries.DataMapRegistry;
import thebetweenlands.common.registries.ItemRegistry;

import java.util.concurrent.CompletableFuture;

public class DataMapProvider extends net.neoforged.neoforge.common.data.DataMapProvider {

	public DataMapProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
		super(packOutput, lookupProvider);
	}

	@Override
	protected void gather() {
		var decayMap = this.builder(DataMapRegistry.DECAY_FOOD);
		decayMap.add(ItemRegistry.SAP_BALL, new DecayFood(2, 0.0F), false);
	}
}
