package thebetweenlands.common.world.gen.feature.structure;

import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.common.world.gen.feature.structure.utils.SludgeWormMazeBlockHelper;

public class SludgeWormMazeMicroBuilds {

	private SludgeWormMazeBlockHelper blockHelper = new SludgeWormMazeBlockHelper();

	public SludgeWormMazeMicroBuilds() {}

	public void selectFeature(World world, BlockPos pos, EnumFacing facing, Random rand, int level, int layer) {
		// add random selection for builds here
	}

	@SuppressWarnings("incomplete-switch")
	public void rotatedCubeVolume(World world, Random rand, BlockPos pos, int offsetA, int offsetB, int offsetC, IBlockState state, int sizeWidth, int sizeHeight, int sizeDepth, EnumFacing facing) {

		switch (facing) {
		case SOUTH:
			for (int yy = offsetB; yy < offsetB + sizeHeight; yy++)
				for (int xx = offsetA; xx < offsetA + sizeWidth; xx++)
					for (int zz = offsetC; zz < offsetC + sizeDepth; zz++) {
						world.setBlockState(pos.add(xx, yy, zz), state, 16);
					}
			break;
		case WEST:
			for (int yy = offsetB; yy < offsetB + sizeHeight; yy++)
				for (int zz = - offsetA ; zz > - offsetA - sizeWidth; zz--)
					for (int xx = offsetC; xx < offsetC + sizeDepth; xx++) {
						world.setBlockState(pos.add(xx, yy, zz), state, 16);
					}
			break;
		case NORTH:
			for (int yy = offsetB; yy < offsetB + sizeHeight; yy++)
				for (int xx = - offsetA; xx > - offsetA - sizeWidth; xx--)
					for (int zz = - offsetC; zz > - offsetC - sizeDepth; zz--) {
						world.setBlockState(pos.add(xx, yy, zz), state, 16);
					}
			break;
		case EAST:
			for (int yy = offsetB; yy < offsetB + sizeHeight; yy++)
				for (int zz = offsetA; zz < offsetA + sizeWidth; zz++)
					for (int xx = - offsetC; xx > - offsetC - sizeDepth; xx--) {
						world.setBlockState(pos.add(xx, yy, zz), state, 16);
					}
			break;
		}
	}
}
