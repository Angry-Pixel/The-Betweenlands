package thebetweenlands.util;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

public class StalactiteHelper {

	public float tX, tZ;
	public float bX, bZ;

	public static StalactiteHelper getValsFor(int x, int y, int z) {
		int size = 14;
		int margin = (int) ((16.0 - size) / 2.0);

		int below = getValFor(x, y - 1, z, size);
		int above = getValFor(x, y + 1, z, size);
		int here = getValFor(x, y, z, size);

		int bX = below % size + margin;
		int bZ = below / size + margin;
		int aX = above % size + margin;
		int aZ = above / size + margin;
		int hX = here % size + margin;
		int hZ = here / size + margin;

		StalactiteHelper core = new StalactiteHelper();
		core.bX = ((hZ + bZ) / 2.0F) / 16.0F;
		core.bZ = ((hX + bX) / 2.0F) / 16.0F;
		core.tX = ((hZ + aZ) / 2.0F) / 16.0F;
		core.tZ = ((hX + aX) / 2.0F) / 16.0F;
		return core;
	}

	private static int getValFor(int x, int y, int z, int size) {
		int seed = 0;
		int noise =  noise(noise(x, z, seed), y, seed);
		return moduloI(noise, size * size);
	}

	private static int hash32shift(int key) {
		key = ~key + (key << 15);
		key = key ^ (key >>> 12);
		key = key + (key << 2);
		key = key ^ (key >>> 4);
		key = key * 2057;
		key = key ^ (key >>> 16);
		return key;
	}

	private static int noise(int x, int y, int seed) {
		return hash32shift(seed + hash32shift(x + hash32shift(y)));
	}

	private static int moduloI(int input, int n) {
		int r = input % n;
		if (r < 0) r += n;
		return r;
	}

	private static float randRange(Random rand, float min, float max) {
		return rand.nextFloat() * (max - min) + min;
	}


	public interface IStalactite {
		default boolean doesConnect(BlockAndTintGetter level, BlockPos pos, BlockState state) {
			return state.getBlock() == this;
		}

		default boolean doesRenderOverlay(BlockAndTintGetter level, BlockPos pos, BlockState state) {
			return false;
		}
	}
}