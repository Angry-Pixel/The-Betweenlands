package thebetweenlands.common.world.gen.feature;

import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import thebetweenlands.common.world.gen.feature.config.BigBulbCappedMushroomFeatureConfiguration;

public class BigBulbCappedMushroomFeature extends Feature<BigBulbCappedMushroomFeatureConfiguration> {

	public BigBulbCappedMushroomFeature(Codec<BigBulbCappedMushroomFeatureConfiguration> codec) {
		super(codec);
	}

	@Override
	public boolean place(FeaturePlaceContext<BigBulbCappedMushroomFeatureConfiguration> context) {
		WorldGenLevel level = context.level();
		RandomSource rand = context.random();
		BlockPos pos = context.origin();
		BigBulbCappedMushroomFeatureConfiguration config = context.config();
		
		int height = rand.nextInt(config.minHeightInclusive(), config.maxHeightInclusive() + 1);
		int maxRadius = 2;
		BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		
		for (int xx = x - maxRadius; xx <= x + maxRadius; xx++) {
			mutablePos.setX(xx);
			for (int zz = z - maxRadius; zz <= z + maxRadius; zz++) {
				mutablePos.setZ(zz);
				for (int yy = y + 2; yy < y + height; yy++) {
					mutablePos.setY(yy);
					if (!level.getBlockState(mutablePos).isAir()) {
						return false;
					}
				}
			}
		}

		mutablePos.set(x, 0, z);
		for (int yy = y; yy < y + height; yy++) {
			mutablePos.setY(yy);
			if (yy == y) {
				level.setBlock(mutablePos, config.stalkBottomState(), 2);
			} else {
				level.setBlock(mutablePos, config.stalkState(), 2);
			}

			if(yy == y + height -1) {
				generateHead(level, mutablePos.immutable(), config.headState());
			}
		}

		// Generate bulb capped mushrooms
		config.patchFeature().ifPresent((feature) -> {
			feature.value().place(context.level(), context.chunkGenerator(), context.random(), context.origin());
		});
		return true;
	}

	// During world generation, features are provided with a 3x3 region of chunks, centered on the chunk being generated, that they can safely generate into.
	private void generateHead(WorldGenLevel level, BlockPos pos, BlockState headState) {
		level.setBlock(pos, headState, 2);
		level.setBlock(pos.offset(0, -1, 0), headState, 2);

		final int startX = pos.getX();
		final int startY = pos.getY();
		final int startZ = pos.getZ();
		MutableBlockPos blockPos = pos.mutable();
		for (int yy = startY; yy >= startY - 4; yy--) {
			blockPos.setY(yy);
			
			int distance = 1;
			if(yy >= startY - 3 && yy <= startY - 1) {
				distance = 2;
			}
			
			for(int x = startX - distance; x <= startX + distance; ++x) {
				blockPos.setX(x);
				int unedgeDistance = distance;
				if(x == startX - distance || x == startX + distance) {
					unedgeDistance = 1;
				}
				for(int z = startZ - unedgeDistance; z <= startZ + unedgeDistance; ++z) {
					blockPos.setZ(z);
					if(x != startX || z != startZ) {
						level.setBlock(blockPos, headState, 2);
					}
				}
			}
		}
	}
}
