package thebetweenlands.common.world.gen.dungeon.layout.grid;

import java.util.BitSet;
import java.util.Collection;

import thebetweenlands.common.world.gen.dungeon.layout.LayoutPhase;
import thebetweenlands.common.world.gen.dungeon.layout.grid.Accelerator.IExtent;
import thebetweenlands.common.world.gen.dungeon.layout.grid.Accelerator.Region;

public abstract class GridObject extends MetaObject {
	public abstract Accelerator getAccelerator();

	abstract void getExtent(IExtent extent, int regionSize);

	abstract void setRegions(Collection<Region> regions);

	public abstract Collection<Region> getRegions();

	public abstract void applyTags(BitSet set);

	public abstract boolean getTag(int tag);

	public GridObject setTag(int tag) {
		this.setTag(tag, true);
		return this;
	}

	public GridObject setTag(int tag, boolean value) {
		this.setTagWithoutUpdate(tag, value);
		this.getAccelerator().update(this);
		return this;
	}

	protected abstract GridObject setTagWithoutUpdate(int tag, boolean value);

	public abstract GridObject clearTags(LayoutPhase<?> phase);
}