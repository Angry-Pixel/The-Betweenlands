package thebetweenlands.util;

import java.util.Random;

public class StalactiteHelper {
	public double tX, tZ;
	public double bX, bZ;

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
		core.bX = ((double)(hZ + bZ) / 2) / 16;
		core.bZ = ((double)(hX + bX) / 2) / 16;
		core.tX = ((double)(hZ + aZ) / 2) / 16;
		core.tZ = ((double)(hX + aX) / 2) / 16;
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
}