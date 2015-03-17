package thebetweenlands.world.biomes.decorators.data;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import thebetweenlands.blocks.BLBlockRegistry;

public enum SurfaceType {
	SWAMP_GRASS,
	GRASS,
	DIRT,
	SAND,
	WATER,
	MIXED;

	public boolean matchBlock(Block block) {
		switch (this) {
			case SWAMP_GRASS:
			return block == BLBlockRegistry.swampGrass || block == Blocks.mycelium;
			case GRASS:
				return block == Blocks.grass || block == Blocks.mycelium;
			case DIRT:
				return block == BLBlockRegistry.swampDirt || block == Blocks.dirt;
			case SAND:
				return block == Blocks.sand;
			case WATER:
				return block == BLBlockRegistry.swampWater;
			case MIXED:
				return block == Blocks.grass || block == Blocks.dirt || block == Blocks.sand || block == BLBlockRegistry.swampGrass || block == BLBlockRegistry.swampDirt;
			default:
				return false;
		}
	}
}
