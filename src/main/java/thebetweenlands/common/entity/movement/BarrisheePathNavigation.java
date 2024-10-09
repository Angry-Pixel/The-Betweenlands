package thebetweenlands.common.entity.movement;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.NodeEvaluator;
import thebetweenlands.common.entity.boss.Barrishee;
import thebetweenlands.common.entity.movement.climb.CustomPathFinder;
import thebetweenlands.common.entity.movement.climb.ObstructionAwareGroundNavigation;

public class BarrisheePathNavigation extends ObstructionAwareGroundNavigation<Barrishee> {
	public BarrisheePathNavigation(Barrishee entity, Level level) {
		super(entity, level, true);
		this.setCanFloat(true);
	}

	@Override
	protected CustomPathFinder createAdvancedPathFinder(int maxExpansions) {
		NodeEvaluator evaluator = new BarrisheeNodeEvaluator(this.advancedPathFindingEntity);
		evaluator.setCanPassDoors(true);
		return new CustomPathFinder(evaluator, maxExpansions);
	}
}
