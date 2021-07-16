package thebetweenlands.common.world.gen.dungeon.layout.grid;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import gnu.trove.set.TLongSet;
import gnu.trove.set.hash.TLongHashSet;
import thebetweenlands.common.world.gen.dungeon.layout.LayoutPhase;

public class Accelerator {
	static interface IExtent {
		public void set(int x, int y, int z);
	}

	public static class Region {
		private static final Region EMPTY = new Region(0);

		protected final Set<GridObject> objects = new HashSet<>();
		protected final long key;

		protected final BitSet tags = new BitSet();

		protected Region(long key) {
			this.key = key;
		}

		public int getX() {
			return keyToX(this.key);
		}

		public int getY() {
			return keyToY(this.key);
		}

		public int getZ() {
			return keyToZ(this.key);
		}

		public Iterable<GridObject> get() {
			return this.objects;
		}

		@SuppressWarnings("unchecked")
		public <T extends GridObject> Iterable<? extends T> get(Class<T> cls) {
			final Iterator<GridObject> it = this.objects.iterator();
			return new Iterable<T>() {
				@Override
				public Iterator<T> iterator() {
					return new Iterator<T>() {
						private T next;

						{
							this.step();
						}

						@Override
						public boolean hasNext() {
							return this.next != null;
						}

						@Override
						public T next() {
							T next = this.next;
							this.step();
							return next;
						}

						private void step() {
							this.next = null;

							while(it.hasNext()) {
								GridObject obj = it.next();

								if(cls.isInstance(obj)) {
									this.next = (T) obj;
									break;
								}
							}
						}
					};
				}
			};
		}

		public boolean isEmpty() {
			return this.objects.isEmpty();
		}

		public boolean getTag(int tag) {
			return this.tags.get(tag);
		}

		public void clearTags(LayoutPhase<?> phase) {
			this.tags.andNot(phase.getTagsMask());
		}
	}

	private static class MutableRegion extends Region {
		private MutableRegion(long key) {
			super(key);
		}

		private boolean add(GridObject obj) {
			return this.objects.add(obj);
		}

		private boolean remove(GridObject obj) {
			return this.objects.remove(obj);
		}
	}

	private int regionSize;

	private final TLongObjectMap<MutableRegion> regions = new TLongObjectHashMap<>();

	public Accelerator(int regionSize) {
		this.regionSize = regionSize;
	}

	public int getRegionSize() {
		return this.regionSize;
	}

	public static long xyzToKey(int x, int y, int z) {
		return ((((long)z + 0xFFFFF) & 0x1FFFFF) << 42) | ((((long)y + 0xFFFFF) & 0x1FFFFF) << 21) | (((long)x + 0xFFFFF) & 0x1FFFFF);
	}

	public static int keyToX(long key) {
		return (int)((key & 0x1FFFFF) - 0xFFFFF);
	}

	public static int keyToY(long key) {
		return (int)(((key >> 21) & 0x1FFFFF) - 0xFFFFF);
	}

	public static int keyToZ(long key) {
		return (int)(((key >> 42) & 0x1FFFFF) - 0xFFFFF);
	}

	public Region get(int x, int y, int z) {
		Region region = this.regions.get(xyzToKey(x, y, z));
		if(region != null) {
			return region;
		}
		return Region.EMPTY;
	}

	public void update(GridObject obj) {
		TLongSet extent = new TLongHashSet();
		List<Region> regions = new ArrayList<>();

		obj.getExtent((x, y, z) -> {
			long key = xyzToKey(x, y, z);
			if(extent.add(key)) {
				MutableRegion region = this.regions.get(key);
				if(region == null) {
					this.regions.put(key, region = new MutableRegion(key));
				}
				region.add(obj);
				regions.add(region);
				obj.applyTags(region.tags); //Propagate tags
			}
		}, this.regionSize);

		for(Region region : obj.getRegions()) {
			if(!extent.contains(region.key)) {
				((MutableRegion) region).remove(obj);

				if(region.isEmpty()) {
					this.regions.remove(region.key);
				}
			}
		}

		obj.setRegions(regions);
	}

	public void remove(GridObject obj) {
		for(Region region : obj.getRegions()) {
			((MutableRegion) region).remove(obj);

			if(region.isEmpty()) {
				this.regions.remove(region.key);
			}
		}

		obj.setRegions(Collections.emptyList());
	}

	public boolean iterateRegions(Function<Region, Boolean> consumer) {
		return !this.regions.forEachValue(region -> !consumer.apply(region));
	}

	public void clear() {
		for(Region region : this.regions.valueCollection()) {
			for(GridObject obj : region.get()) {
				obj.setRegions(Collections.emptyList());
			}
		}
		this.regions.clear();
	}
}
