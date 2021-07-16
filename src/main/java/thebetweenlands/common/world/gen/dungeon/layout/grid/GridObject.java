package thebetweenlands.common.world.gen.dungeon.layout.grid;

import java.util.BitSet;
import java.util.Collection;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import thebetweenlands.common.world.gen.dungeon.layout.LayoutPhase;
import thebetweenlands.common.world.gen.dungeon.layout.grid.Accelerator.IExtent;
import thebetweenlands.common.world.gen.dungeon.layout.grid.Accelerator.Region;

public abstract class GridObject {
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

	public <TMeta> TMeta getOrCreateMeta(LayoutPhase<TMeta> phase) {
		TMeta meta = this.getMeta(phase);
		if(meta == null) {
			meta = phase.getMeta().apply(this);
			this.setMeta(phase, meta);
		}
		return meta;
	}

	public <TMeta> GridObject updateOrCreateMeta(LayoutPhase<TMeta> phase, Consumer<TMeta> update) {
		update.accept(this.getOrCreateMeta(phase));
		return this;
	}

	@Nullable
	public abstract <TMeta> TMeta getMeta(LayoutPhase<TMeta> phase);

	public abstract <TMeta> GridObject setMeta(LayoutPhase<TMeta> phase, @Nullable TMeta meta);
}