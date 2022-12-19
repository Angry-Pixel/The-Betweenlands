package thebetweenlands.common.entity.ai.gecko;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.Vec3d;
import thebetweenlands.common.entity.mobs.EntityGecko;
import thebetweenlands.common.registries.BlockRegistry;

public abstract class EntityAIGeckoHide extends EntityAIBase {
	private final Comparator<BlockPos> closest = (a, b) -> {
		double aLength = a.getDistance(0, 0, 0);
		double bLength = b.getDistance(0, 0, 0);
		return aLength < bLength ? -1 : aLength > bLength ? 1 : 0;
	};

	protected final EntityGecko gecko;

	private double farSpeed;

	private double nearSpeed;

	private Path path;

	private PathNavigate navigator;

	private boolean bushBound;

	private BlockPos target;

	public EntityAIGeckoHide(EntityGecko gecko, double farSpeed, double nearSpeed) {
		this.gecko = gecko;
		this.farSpeed = farSpeed;
		this.nearSpeed = nearSpeed;
		navigator = gecko.getNavigator();
		setMutexBits(1);
	}

	@Nullable
	protected abstract Vec3d getFleeingCausePosition();

	protected abstract boolean shouldFlee();

	@Override
	public boolean shouldExecute() {
		if (gecko.isHiding()) {
			return false;
		}
		if(!shouldFlee()) {
			return false;
		}
		Vec3d fleeingCausePos = this.getFleeingCausePosition();
		target = findNearBush();
		if (target == null) {
			Vec3d target = fleeingCausePos != null ? RandomPositionGenerator.findRandomTargetBlockAwayFrom(gecko, 16, 7, fleeingCausePos) : RandomPositionGenerator.findRandomTargetBlockAwayFrom(gecko, 16, 7, gecko.getPositionVector());
			if (target != null) {
				this.target = new BlockPos(target);
			}
		} else {
			bushBound = true;
		}
		if (target == null) {
			return false;
		} else if (!bushBound && fleeingCausePos != null && fleeingCausePos.squareDistanceTo(target.getX(), target.getY(), target.getZ()) < fleeingCausePos.squareDistanceTo(gecko.getPositionVector())) {
			return false;
		} else {
			path = navigator.getPathToPos(target);
			if (doesGeckoNeighborBush(target)) {
				gecko.setHidingBush(new BlockPos(target));
				gecko.startHiding();
				return false;
			}
			if (path != null) {
				PathPoint finalPathPoint = path.getFinalPathPoint();
				return finalPathPoint.x == target.getX() && finalPathPoint.y == target.getY() && finalPathPoint.z == target.getZ() || bushBound && doesPathDestinationNeighborBush(target, path);
			}
			return false;
		}
	}

	private boolean doesGeckoNeighborBush(BlockPos target) {
		BlockPos geckoPos = new BlockPos(gecko);
		for (EnumFacing facing : EnumFacing.VALUES) {
			if (target.offset(facing).equals(geckoPos)) {
				return true;
			}
		}
		return false;
	}

	private boolean doesPathDestinationNeighborBush(BlockPos target, Path path) {
		for (EnumFacing facing : EnumFacing.VALUES) {
			BlockPos nearTarget = new BlockPos(target.offset(facing));
			PathPoint finalPathPoint = path.getFinalPathPoint();
			if (finalPathPoint.x == nearTarget.getX() && finalPathPoint.y == nearTarget.getY() && finalPathPoint.z == nearTarget.getZ()) {
				return true;
			}
		}
		return false;
	}

	private BlockPos findNearBush() {
		final int radius = 8;
		BlockPos center = new BlockPos(gecko);
		Random rand = gecko.getRNG();
		List<BlockPos> bushes = new ArrayList<>();
		MutableBlockPos pos = new MutableBlockPos();
		for (int dx = -radius; dx <= radius; dx++) {
			for (int dy = -radius / 2; dy <= radius; dy++) {
				for (int dz = -radius; dz <= radius; dz++) {
					pos.setPos(center.getX() + dx, center.getY() + dy, center.getZ() + dz);
					IBlockState state = gecko.world.getBlockState(pos);
					if (state.getBlock() == BlockRegistry.WEEDWOOD_BUSH && gecko.world.isBlockNormalCube(pos.down(), false) && 
							gecko.world.getEntitiesWithinAABB(EntityGecko.class, new AxisAlignedBB(pos), e -> e != gecko).isEmpty()) {
						bushes.add(pos.subtract(center));
					}
				}
			}
		}
		if (bushes.size() == 0) {
			return null;
		}
		Collections.sort(bushes, closest);
		double targetDistance = bushes.get(0).getDistance(0, 0, 0);
		final double epsilon = 1e-8;
		for (int i = 0, bushCount = bushes.size(); i < bushCount; i++) {
			boolean end = i == bushCount - 1;
			if (Math.abs(bushes.get(i).getDistance(0, 0, 0) - targetDistance) > epsilon || end) {
				return bushes.get(rand.nextInt(end ? i + 1 : i)).add(center);
			}
		}
		throw new ConcurrentModificationException("I'm not sure how I feel about this...");
	}

	@Override
	public boolean shouldContinueExecuting() {
		if(target != null && !gecko.world.getEntitiesWithinAABB(EntityGecko.class, new AxisAlignedBB(target), e -> e != gecko).isEmpty()) {
			return false;
		}
		return !navigator.noPath();
	}

	@Override
	public void startExecuting() {
		navigator.setPath(path, farSpeed);
		gecko.setHidingBush(target);
	}

	@Override
	public void resetTask() {
		if (bushBound && path.isFinished()) {
			gecko.startHiding();

		}
		bushBound = false;
	}

	@Override
	public void updateTask() {
		Vec3d fleeingCausePos = this.getFleeingCausePosition();
		if (fleeingCausePos != null && gecko.getPositionVector().squareDistanceTo(fleeingCausePos) < 49) {
			gecko.getNavigator().setSpeed(nearSpeed);
		} else {
			gecko.getNavigator().setSpeed(farSpeed);
		}
	}
}
