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
    PEAT,
    MIXED,
    UNDERGROUND;

    public boolean matchBlock(Block block) {
        switch (this) {
            case SWAMP_GRASS:
                return block == BLBlockRegistry.swampGrass || block == Blocks.mycelium || block == BLBlockRegistry.deadGrass;
            case GRASS:
                return block == Blocks.grass || block == Blocks.mycelium;
            case DIRT:
                return block == BLBlockRegistry.swampDirt || block == Blocks.dirt || block == BLBlockRegistry.mud;
            case SAND:
                return block == Blocks.sand;
            case WATER:
                return block == BLBlockRegistry.swampWater;
            case PEAT:
                return block == BLBlockRegistry.peat;
            case MIXED:
                return block == Blocks.grass || block == Blocks.dirt || block == Blocks.sand || block == BLBlockRegistry.swampGrass || block == BLBlockRegistry.swampDirt || block == BLBlockRegistry.deadGrass || block == BLBlockRegistry.mud;
            case UNDERGROUND:
                return block == BLBlockRegistry.betweenstone;
            default:
                return false;
        }
    }
}
