package thebetweenlands.common.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.DataMapProvider;
import thebetweenlands.common.items.datamaps.DecayFood;
import thebetweenlands.common.items.datamaps.FluxMultiplier;
import thebetweenlands.common.registries.DataMapRegistry;
import thebetweenlands.common.registries.ItemRegistry;

import java.util.concurrent.CompletableFuture;

public class BLDataMapProvider extends DataMapProvider {

	public BLDataMapProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
		super(output, lookupProvider);
	}

	@Override
	protected void gather() {
		var decayMap = this.builder(DataMapRegistry.DECAY_FOOD);
		decayMap.add(ItemRegistry.SAP_BALL, new DecayFood(2, 0.0F), false);
		decayMap.add(ItemRegistry.FORBIDDEN_FIG, new DecayFood(20, 0.2F), false);
		decayMap.add(ItemRegistry.SAP_JELLO, new DecayFood(4, 0.2F), false);

		var fluxMap = this.builder(DataMapRegistry.FLUX_MULTIPLIER);
		fluxMap.add(Tags.Items.ORES, new FluxMultiplier(0.33F, 2), false);
	}
}
