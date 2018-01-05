package thebetweenlands.common.world.gen.feature;

import java.util.Random;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenMangroveTree extends WorldGenerator {
	@Override
	public boolean generate(World worldIn, Random rand, BlockPos pos) {
		return true;
	}
}