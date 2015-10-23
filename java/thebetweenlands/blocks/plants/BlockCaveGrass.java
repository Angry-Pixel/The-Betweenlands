package thebetweenlands.blocks.plants;

import net.minecraft.block.Block;
import thebetweenlands.blocks.BLBlockRegistry;

public class BlockCaveGrass extends BlockBLSmallPlants {
	public BlockCaveGrass(String type) {
		super(type);
	}

	@Override
	public boolean canPlaceBlockOn(Block block) {
		return block == BLBlockRegistry.pitstone || block == BLBlockRegistry.limestone;
	}
}
