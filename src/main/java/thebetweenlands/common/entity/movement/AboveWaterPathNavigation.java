package thebetweenlands.common.entity.movement;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.phys.Vec3;

public class AboveWaterPathNavigation extends PathNavigation {

	public AboveWaterPathNavigation(Mob entity, Level level) {
		super(entity, level);
	}

	@Override
	protected Vec3 getTempMobPos() {
		return new Vec3(this.mob.getX(), this.mob.getY() + (double) this.mob.getBbHeight() * 0.5D, this.mob.getZ());
	}

	@Override
	protected boolean canUpdatePath() {
		return this.mob.level().getBlockState(this.mob.blockPosition()).liquid();
    }

	@Override
	protected PathFinder createPathFinder(int maxVisitedNodes) {
		return new PathFinder(new RowingNodeEvaluator(), maxVisitedNodes);
	}

	@Override
	protected void followThePath() {
		Vec3 vec3d = this.getTempMobPos();
		float f = this.mob.getBbWidth() * this.mob.getBbWidth();
		int i = 6;

		if (vec3d.distanceToSqr(this.path.getEntityPosAtNode(this.mob, this.path.getNextNodeIndex())) < (double) f) {
			this.path.advance();
		}

		for (int j = Math.min(this.path.getNextNodeIndex() + 6, this.path.getNodeCount() - 1); j > this.path.getNextNodeIndex(); --j) {
			Vec3 vec3d1 = this.path.getEntityPosAtNode(this.mob, j);

			if (vec3d1.distanceToSqr(vec3d) <= 36.0D && this.canMoveDirectly(vec3d, vec3d1)) {
				this.path.setNextNodeIndex(j);
				break;
			}
		}

		this.doStuckDetection(vec3d);
	}

	@Override
	protected boolean canMoveDirectly(Vec3 posVec31, Vec3 posVec32) {
		return isClearForMovementBetween(this.mob, posVec31, new Vec3(posVec32.x, posVec32.y + (double) this.mob.getBbHeight() * 0.5D, posVec32.z), false);
	}

}
