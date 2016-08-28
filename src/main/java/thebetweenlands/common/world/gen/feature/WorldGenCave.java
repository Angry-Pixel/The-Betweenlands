package thebetweenlands.common.world.gen.feature;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import thebetweenlands.common.world.gen.biome.decorator.SurfaceType;

public abstract class WorldGenCave extends WorldGenerator {
	protected final EnumFacing[] directions = { EnumFacing.SOUTH, EnumFacing.WEST, EnumFacing.NORTH, EnumFacing.EAST };

	public WorldGenCave(boolean doBlockNotify) {
		super(doBlockNotify);
	}

	protected boolean isGoodStart(World world, BlockPos pos) {
		if (supports(world, pos)) {
			int sides = 0;
			for (EnumFacing dir : directions) {
				if (!isValidBlock(world, pos.offset(dir))) {
					return false;
				}
				if (isValidBlock(world, pos.offset(dir).down()) && world.isSideSolid(pos.offset(dir).down(), dir)) {
					sides++;
				}
			}
			return sides > 0;
		}
		return false;
	}

	protected boolean supports(World world, BlockPos pos) {
		return isValidBlock(world, pos) && world.isAirBlock(pos.down());
	}

	protected boolean isValidBlock(World world, BlockPos pos) {
		return SurfaceType.UNDERGROUND.matches(world.getBlockState(pos));
	}


	protected class PlantLocation {
		private BlockPos pos;

		private int height;

		public PlantLocation(World world, BlockPos pos) {
			this.setPos(pos);
			setHeight(1);
			while (world.isAirBlock(pos.add(0, -getHeight(), 0)) && (pos.getY() - getHeight()) > 0) {
				setHeight(getHeight() + 1);
			}
		}

		public int getHeight() {
			return height;
		}

		public void setHeight(int height) {
			this.height = height;
		}

		public BlockPos getPos() {
			return pos;
		}

		public void setPos(BlockPos pos) {
			this.pos = pos;
		}
	}
}
