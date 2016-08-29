package thebetweenlands.common.world.gen.feature;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import thebetweenlands.common.block.plant.BlockDoublePlantBL;

public class WorldGenDoublePlantCluster extends WorldGenerator {
	private final BlockDoublePlantBL block;
	
	public WorldGenDoublePlantCluster(BlockDoublePlantBL block) {
		this.block = block;
	}
	
	@Override
    public boolean generate(World worldIn, Random rand, BlockPos position) {
        boolean generated = false;

        for (int i = 0; i < 64; ++i) {
            BlockPos blockpos = position.add(rand.nextInt(8) - rand.nextInt(8), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(8) - rand.nextInt(8));

            if (worldIn.isAirBlock(blockpos) && (!worldIn.provider.getHasNoSky() || blockpos.getY() < 254) && this.block.canPlaceBlockAt(worldIn, blockpos)) {
                this.block.placeAt(worldIn, blockpos, 2);
                generated = true;
            }
        }

        return generated;
    }
}