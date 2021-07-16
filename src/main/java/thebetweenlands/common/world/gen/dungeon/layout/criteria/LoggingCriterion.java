package thebetweenlands.common.world.gen.dungeon.layout.criteria;

import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.world.gen.dungeon.layout.LayoutPhase;

public class LoggingCriterion extends LayoutCriterion<LayoutPhase<?>> {

	@Override
	public void check(LayoutPhase<?> phase, boolean result, int iteration) {
		TheBetweenlands.logger.info(String.format("Running phase %s iteration %d", phase.getClass().getName(), iteration));
	}

}
