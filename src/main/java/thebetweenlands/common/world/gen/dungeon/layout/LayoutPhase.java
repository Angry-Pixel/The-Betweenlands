package thebetweenlands.common.world.gen.dungeon.layout;

import java.util.BitSet;
import java.util.Random;
import java.util.function.Function;

import javax.annotation.Nullable;

import thebetweenlands.common.world.gen.dungeon.layout.grid.Grid;
import thebetweenlands.common.world.gen.dungeon.layout.grid.GridObject;

public abstract class LayoutPhase<TMeta> {
	public static interface TagSupplier {
		public int reserve(int num);
	}

	protected Grid grid;
	protected Random rng;

	private BitSet tagsMask = new BitSet();
	private boolean metaSet = false;
	private boolean isMetaShared;
	private Class<TMeta> metaClass;
	private Function<GridObject, TMeta> meta;
	private int metaId = -1;

	public void init(Grid grid, Random rng, TagSupplier tagSupplier) {
		this.grid = grid;
		this.rng = rng;
		this.tagsMask = new BitSet();
		this.metaSet = false;
		this.isMetaShared = false;
		this.metaClass = null;
		this.meta = null;
		this.metaId = -1;
	}

	public final Grid getGrid() {
		return this.grid;
	}

	public final Random getRng() {
		return this.rng;
	}

	final void setTagsMask(BitSet tagsMask) {
		this.tagsMask = tagsMask;
	}

	public final BitSet getTagsMask() {
		return this.tagsMask;
	}

	protected final void setMeta(Class<TMeta> cls, Function<GridObject, TMeta> factory, boolean share) {
		if(this.metaSet) {
			throw new IllegalStateException("Cannot set meta after it has already been used");
		}
		if(this.meta != null) {
			throw new IllegalStateException("Cannot set meta multiple times");
		}
		this.isMetaShared = share;
		this.metaClass = cls;
		this.meta = factory;
	}

	@Nullable
	public final Function<GridObject, TMeta> getMeta() {
		this.metaSet = true;
		return this.meta;
	}

	@Nullable
	public final Class<TMeta> getMetaClass() {
		this.metaSet = true;
		return this.metaClass;
	}

	public final boolean isMetaShared() {
		this.metaSet = true;
		return this.isMetaShared;
	}

	final void setMetaId(int id) {
		this.metaId = id;
	}

	public final int getMetaId() {
		return this.metaId;
	}
}
