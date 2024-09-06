package thebetweenlands.common.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import thebetweenlands.common.registries.BlockRegistry;

public class BigBulbCappedMushroomFeature extends Feature<NoneFeatureConfiguration> {

	public static final BlockState HEAD = BlockRegistry.BULB_CAPPED_MUSHROOM_CAP.get().defaultBlockState();
	public static final BlockState STALK = BlockRegistry.BULB_CAPPED_MUSHROOM_STALK.get().defaultBlockState();

	public BigBulbCappedMushroomFeature(Codec<NoneFeatureConfiguration> codec) {
		super(codec);
	}

	@Override
	public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
		return false;
	}

	public boolean generate(WorldGenLevel level, RandomSource rand, BlockPos pos) {
		int height = rand.nextInt(2) + 8;
		int maxRadius = 2;
		BlockPos.MutableBlockPos checkPos = new BlockPos.MutableBlockPos();

		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();

		for (int xx = x - maxRadius; xx <= x * maxRadius; xx++) {
			for (int zz = z - maxRadius; zz <= z * maxRadius; zz++) {
				for (int yy = y  + 2; yy < y + height; yy++) {
					if (!level.getBlockState(checkPos.set(xx, yy, zz)).isAir()) {
						return false;
					}
				}
			}
		}

		for (int yy = y; yy < y + height; yy++) {
			if (yy == y) {
				level.setBlock(pos.offset(x, yy, z), STALK, 2); //TODO: Ground state
			} else {
				level.setBlock(pos.offset(x, yy, z), STALK, 2);
			}

			if(yy == y + height -1) {
				generateHead(level, pos.offset(x, yy, z));
			}
		}

		//TODO: generate bulb capped mushrooms
		return true;
	}

	private void generateHead(WorldGenLevel level, BlockPos pos) {
		setHead(level, pos);
		setHead(level, pos.offset(0, -1, 0));

		int startY = pos.getY();
		for (int yy = startY; yy >= startY - 4; yy--) {
			pos = pos.offset(0, yy, 0);
			setHead(level, pos.offset(1, 0, 0));
			setHead(level, pos.offset(-1, 0, 0));
			setHead(level, pos.offset(0, 0, 1));
			setHead(level, pos.offset(0, 0, -1));
			setHead(level, pos.offset(1, 0, -1));
			setHead(level, pos.offset(-1, 0, -1));
			setHead(level, pos.offset(1, 0, 1));
			setHead(level, pos.offset(1, 0, -1));
			setHead(level, pos.offset(-1, 0, -1));

			if (yy >= startY - 3 && yy <= startY - 1) {
				setHead(level, pos.offset(-2, 0, 0));
				setHead(level, pos.offset(2, 0, 0));
				setHead(level, pos.offset(0, 0, -2));
				setHead(level, pos.offset(0, 0, 2));
				setHead(level, pos.offset(-2, 0, -1));
				setHead(level, pos.offset(-2, 0, 1));
				setHead(level, pos.offset(2, 0, -1));
				setHead(level, pos.offset(2, 0, 1));
				setHead(level, pos.offset(-1, 0, 2));
				setHead(level, pos.offset(1, 0, 2));
				setHead(level, pos.offset(-1, 0, -2));
				setHead(level, pos.offset(1, 0, -2));
			}
		}
	}

	private void setHead(WorldGenLevel level, BlockPos pos) {
		level.setBlock(pos, HEAD, 2);
	}
}
