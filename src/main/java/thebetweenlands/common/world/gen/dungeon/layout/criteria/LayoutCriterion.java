package thebetweenlands.common.world.gen.dungeon.layout.criteria;

import thebetweenlands.common.world.gen.dungeon.layout.LayoutPhase;

public abstract class LayoutCriterion<TPhase extends LayoutPhase<?>> {
	private boolean retry;
	private boolean revertPrevious;
	private boolean revertFully;
	private boolean abort;

	public abstract void check(TPhase phase, boolean result, int iteration);

	public final void reset() {
		this.retry = false;
		this.revertPrevious = false;
		this.revertFully = false;
		this.abort = false;
	}

	protected final void setRetry() {
		this.retry = true;
	}
	
	protected final void setRetryPrevious() {
		this.retry = true;
		this.revertPrevious = true;
	}
	
	protected final void setRetryFully() {
		this.retry = true;
		this.revertFully = true;
	}

	protected final void setAbort() {
		this.abort = true;
	}

	public final boolean isRetry() {
		return this.retry;
	}

	public final boolean isRetryPrevious() {
		return this.revertPrevious;
	}

	public final boolean isRetryFully() {
		return this.revertFully;
	}
	
	public final boolean isAbort() {
		return this.abort;
	}
}
