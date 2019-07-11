package thebetweenlands.common.world.gen.layer;

import java.util.List;

import com.google.common.collect.Lists;

public class InstancedIntCache {
	private int intCacheSize = 256;
	private final List<int[]> freeSmallArrays = Lists.<int[]>newArrayList();
	private final List<int[]> inUseSmallArrays = Lists.<int[]>newArrayList();
	private final List<int[]> freeLargeArrays = Lists.<int[]>newArrayList();
	private final List<int[]> inUseLargeArrays = Lists.<int[]>newArrayList();

	public synchronized int[] getIntCache(int size) {
		if (size <= 256) {
			if (freeSmallArrays.isEmpty()) {
				int[] aint4 = new int[256];
				inUseSmallArrays.add(aint4);
				return aint4;
			} else {
				int[] aint3 = freeSmallArrays.remove(freeSmallArrays.size() - 1);
				inUseSmallArrays.add(aint3);
				return aint3;
			}
		} else if (size > intCacheSize) {
			intCacheSize = size;
			freeLargeArrays.clear();
			inUseLargeArrays.clear();
			int[] aint2 = new int[intCacheSize];
			inUseLargeArrays.add(aint2);
			return aint2;
		} else if (freeLargeArrays.isEmpty()) {
			int[] aint1 = new int[intCacheSize];
			inUseLargeArrays.add(aint1);
			return aint1;
		} else {
			int[] aint = freeLargeArrays.remove(freeLargeArrays.size() - 1);
			inUseLargeArrays.add(aint);
			return aint;
		}
	}

	public synchronized void resetIntCache() {
		if (!freeLargeArrays.isEmpty()) {
			freeLargeArrays.remove(freeLargeArrays.size() - 1);
		}

		if (!freeSmallArrays.isEmpty()) {
			freeSmallArrays.remove(freeSmallArrays.size() - 1);
		}

		freeLargeArrays.addAll(inUseLargeArrays);
		freeSmallArrays.addAll(inUseSmallArrays);
		inUseLargeArrays.clear();
		inUseSmallArrays.clear();
	}
}