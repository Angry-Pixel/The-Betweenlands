package thebetweenlands.common.item.farming;

import thebetweenlands.common.block.farming.BlockGenericDugSoil;
import thebetweenlands.common.registries.BlockRegistry;

public class ItemMiddleFruitBushSeeds extends ItemPlantableSeeds {
	public ItemMiddleFruitBushSeeds() {
		super(() -> { 
			return BlockRegistry.MIDDLE_FRUIT_BUSH.getDefaultState();
		}, state -> { 
			return state.getBlock() instanceof BlockGenericDugSoil && !state.getValue(BlockGenericDugSoil.DECAYED);
		});
	}
}
