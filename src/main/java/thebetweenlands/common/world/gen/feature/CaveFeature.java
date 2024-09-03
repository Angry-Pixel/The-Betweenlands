package thebetweenlands.common.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import thebetweenlands.common.world.gen.SurfaceType;

public abstract class CaveFeature extends Feature<NoneFeatureConfiguration> {
	protected final Direction[] directions = Direction.Plane.HORIZONTAL.stream().toArray(Direction[]::new);

	public CaveFeature(Codec<NoneFeatureConfiguration> codec) {
		super(codec);
	}

	protected boolean isGoodStart(WorldGenLevel level, BlockPos pos) {
		if (supports(level, pos)) {
			int sides = 0;
			for (Direction dir : directions) {
				if (!isValidBlock(level, pos.relative(dir))) {
					return false;
				}
				if (isValidBlock(level, pos.relative(dir).below()) && level.getBlockState(pos).isFaceSturdy(level, pos.relative(dir).below(), dir)) {
					sides++;
				}
			}
			return sides > 0;
		}
		return false;
	}

	protected boolean supports(WorldGenLevel level, BlockPos pos) {
		return isValidBlock(level, pos) && level.getBlockState(pos.below()).isAir();
	}

	protected boolean isValidBlock(WorldGenLevel level, BlockPos pos) {
		return SurfaceType.UNDERGROUND.matches(level.getBlockState(pos));
	}

	protected static class PlantLocation {
		private BlockPos pos;
		private int height;

		public PlantLocation(WorldGenLevel level, BlockPos pos) {
			this.setPos(pos);
			this.setHeight(1);
			while (level.getBlockState(pos.offset(0, -this.getHeight(), 0)).isAir() && (pos.getY() - this.getHeight()) > 0) {
				this.setHeight(this.getHeight() + 1);
			}
		}

		public int getHeight() {
			return this.height;
		}

		public void setHeight(int height) {
			this.height = height;
		}

		public BlockPos getPos() {
			return this.pos;
		}

		public void setPos(BlockPos pos) {
			this.pos = pos;
		}
	}
}
