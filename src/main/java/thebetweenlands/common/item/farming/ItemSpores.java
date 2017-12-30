package thebetweenlands.common.item.farming;

import thebetweenlands.common.block.farming.BlockGenericDugSoil;
import thebetweenlands.common.registries.BlockRegistry;

public class ItemSpores extends ItemPlantableSeeds {
	public ItemSpores() {
		super(() -> { 
			return BlockRegistry.FUNGUS_CROP.getDefaultState();
		}, state -> { 
			return state.getBlock() instanceof BlockGenericDugSoil && !state.getValue(BlockGenericDugSoil.DECAYED);
		});
	}
}
