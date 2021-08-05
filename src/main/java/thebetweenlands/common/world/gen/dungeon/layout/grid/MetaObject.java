package thebetweenlands.common.world.gen.dungeon.layout.grid;

import java.util.function.Consumer;
import java.util.function.Function;

import javax.annotation.Nullable;

import thebetweenlands.common.world.gen.dungeon.layout.LayoutPhase;

public class MetaObject {
	private Object[] meta = null;

	public <TMeta> TMeta getOrCreateMeta(LayoutPhase<TMeta> phase) {
		TMeta meta = this.getMeta(phase);
		if(meta == null) {
			meta = phase.getMeta().apply(this);
			this.setMeta(phase, meta);
		}
		return meta;
	}

	public <TMeta> MetaObject updateOrCreateMeta(LayoutPhase<TMeta> phase, Consumer<TMeta> update) {
		update.accept(this.getOrCreateMeta(phase));
		return this;
	}

	private void ensureMetaCapacity(int id) {
		if(this.meta == null) {
			this.meta = new Object[id + 1];
		} else if(id >= this.meta.length) {
			Object[] newMeta = new Object[id + 1];
			System.arraycopy(this.meta, 0, newMeta, 0, this.meta.length);
			this.meta = newMeta;
		}
	}

	@SuppressWarnings("unchecked")
	@Nullable
	public <TMeta> TMeta getMeta(LayoutPhase<TMeta> phase) {
		int id = phase.getMetaId();
		if(id < 0 || this.meta == null || id >= this.meta.length) {
			return null;
		}
		return (TMeta) this.meta[id];
	}

	public <R, TMeta> R getMeta(LayoutPhase<TMeta> phase, Function<TMeta, R> func) {
		TMeta meta = this.getMeta(phase);
		return func.apply(meta);
	}

	public <R, TMeta> R getMeta(LayoutPhase<TMeta> phase, Function<TMeta, R> func, R defaultValue) {
		TMeta meta = this.getMeta(phase);
		if(meta != null) {
			return func.apply(meta);
		}
		return defaultValue;
	}

	public <TMeta> MetaObject setMeta(LayoutPhase<TMeta> phase, @Nullable TMeta meta) {
		int id = phase.getMetaId();
		if(id < 0) {
			return this;
		}
		this.ensureMetaCapacity(id);
		this.meta[id] = meta;
		return this;
	}
}
