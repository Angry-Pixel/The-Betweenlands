package thebetweenlands.common.entity.ai.goals;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.common.entity.creature.Gecko;
import thebetweenlands.common.registries.BlockRegistry;

import java.util.*;

public class HideFromRainGoal extends Goal {

	private final Comparator<BlockPos> closest = (a, b) -> {
		double aLength = a.distToCenterSqr(0, 0, 0);
		double bLength = b.distToCenterSqr(0, 0, 0);
		return Double.compare(aLength, bLength);
	};

	private final Gecko gecko;
	private final double speed;
	@Nullable
	private Path path;
	private boolean bushBound;
	@Nullable
	private BlockPos target;

	public HideFromRainGoal(Gecko gecko, double speed) {
		this.gecko = gecko;
		this.speed = speed;
	}


	@Override
	public boolean canUse() {
		if (this.gecko.isHiding()) {
			return false;
		}
		if(!this.gecko.level().isRainingAt(this.gecko.blockPosition())) {
			return false;
		}
		this.target = this.findNearBush();
		if (this.target == null) {
			Vec3 target = DefaultRandomPos.getPosAway(this.gecko, 16, 7, this.gecko.position());
			if (target != null) {
				this.target = BlockPos.containing(target);
			}
		} else {
			this.bushBound = true;
		}
		if (this.target == null) {
			return false;
		} else if (!this.bushBound) {
			return false;
		} else {
			this.path = this.gecko.getNavigation().createPath(this.target, 0);
			if (doesGeckoNeighborBush(this.target)) {
				this.gecko.setHidingBush(this.target);
				this.gecko.startHiding();
				return false;
			}
			if (this.path != null) {
				Node finalPathPoint = this.path.getEndNode();
				if (finalPathPoint != null) {
					return finalPathPoint.x == this.target.getX() && finalPathPoint.y == this.target.getY() && finalPathPoint.z == this.target.getZ() || this.bushBound && this.doesPathDestinationNeighborBush(this.target, this.path);
				}
			}
			return false;
		}
	}

	@Override
	public boolean canContinueToUse() {
		if(this.target != null && !this.gecko.level().getEntitiesOfClass(Gecko.class, new AABB(this.target), e -> e != this.gecko).isEmpty()) {
			return false;
		}
		return !this.gecko.getNavigation().isDone();
	}

	@Override
	public void start() {
		this.gecko.getNavigation().moveTo(this.path, this.speed);
		this.gecko.setHidingBush(this.target);
	}

	@Override
	public void stop() {
		if (this.bushBound && this.path.isDone()) {
			this.gecko.startHiding();

		}
		this.bushBound = false;
	}

	private boolean doesGeckoNeighborBush(BlockPos target) {
		for (Direction facing : Direction.values()) {
			if (target.relative(facing).equals(this.gecko.blockPosition())) {
				return true;
			}
		}
		return false;
	}

	private boolean doesPathDestinationNeighborBush(BlockPos target, Path path) {
		for (Direction facing : Direction.values()) {
			BlockPos nearTarget = new BlockPos(target.relative(facing));
			Node finalPathPoint = path.getEndNode();
			if (finalPathPoint == null) return false;
			if (finalPathPoint.x == nearTarget.getX() && finalPathPoint.y == nearTarget.getY() && finalPathPoint.z == nearTarget.getZ()) {
				return true;
			}
		}
		return false;
	}

	@Nullable
	private BlockPos findNearBush() {
		final int radius = 8;
		BlockPos center = this.gecko.blockPosition();
		RandomSource rand = this.gecko.getRandom();
		List<BlockPos> bushes = new ArrayList<>();
		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
		for (int dx = -radius; dx <= radius; dx++) {
			for (int dy = -radius / 2; dy <= radius; dy++) {
				for (int dz = -radius; dz <= radius; dz++) {
					pos.set(center.getX() + dx, center.getY() + dy, center.getZ() + dz);
					BlockState state = this.gecko.level().getBlockState(pos);
					if (state.is(BlockRegistry.WEEDWOOD_BUSH) && Block.isFaceFull(this.gecko.level().getBlockState(pos.below()).getCollisionShape(this.gecko.level(), pos.below()), Direction.UP) &&
						this.gecko.level().getEntitiesOfClass(Gecko.class, new AABB(pos), e -> e != gecko).isEmpty()) {
						bushes.add(pos.subtract(center));
					}
				}
			}
		}
		if (bushes.isEmpty()) {
			return null;
		}
		bushes.sort(this.closest);
		double targetDistance = bushes.getFirst().distToCenterSqr(0, 0, 0);
		final double epsilon = 1e-8;
		for (int i = 0, bushCount = bushes.size(); i < bushCount; i++) {
			boolean end = i == bushCount - 1;
			if (Math.abs(bushes.get(i).distToCenterSqr(0, 0, 0) - targetDistance) > epsilon || end) {
				return bushes.get(rand.nextInt(end ? i + 1 : i)).offset(center);
			}
		}
		throw new ConcurrentModificationException("I'm not sure how I feel about this...");
	}
}
