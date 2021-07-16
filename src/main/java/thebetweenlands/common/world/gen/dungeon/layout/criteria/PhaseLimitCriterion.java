package thebetweenlands.common.world.gen.dungeon.layout.criteria;

import thebetweenlands.common.world.gen.dungeon.layout.LayoutPhase;

public class PhaseLimitCriterion extends LayoutCriterion<LayoutPhase<?>> {

	private int phases;

	public PhaseLimitCriterion(int maxPhases) {
		this.phases = maxPhases;
	}

	@Override
	public void check(LayoutPhase<?> phase, boolean result, int iteration) {
		if(this.phases-- <= 0) {
			this.setAbort();
		}
	}

}
