package thebetweenlands.common.world.gen.dungeon.layout.criteria;

import thebetweenlands.common.world.gen.dungeon.layout.LayoutPhase;

public class RetryCriterion extends LayoutCriterion<LayoutPhase<?>> {

	private final int maxRetries;
	private final boolean retryPrevious, retryFully;

	public RetryCriterion(int maxRetries, boolean retryPrevious, boolean retryFully) {
		this.maxRetries = maxRetries;
		this.retryPrevious = retryPrevious;
		this.retryFully = retryFully;
	}

	@Override
	public void check(LayoutPhase<?> phase, boolean result, int iteration) {
		if(!result && iteration < this.maxRetries) {
			if(this.retryFully) {
				this.setRetryFully();
			} else if(this.retryPrevious) {
				this.setRetryPrevious();
			} else {
				this.setRetry();
			}
		}
	}

}
