package thebetweenlands.common.entity.ai.goals;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.common.entity.monster.Stalker;

import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;

public class StalkerScurryGoal extends Goal {

	private final Stalker entity;

	private final float speed;

	private int maxPathMemory = 32;
	private final LinkedHashMap<Pair<Path, Integer>, BlockPos> pathMemory = new LinkedHashMap<>();

	@Nullable
	private Path path;

	@Nullable
	private Vec3 scurryingStart;
	private double lastDistance;
	private boolean isScurrying = false;

	private int scurryingCooldown = 0;

	public StalkerScurryGoal(Stalker entity, float speed) {
		this.entity = entity;
		this.speed = speed;
		this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
	}

	@Override
	public boolean canUse() {
		if (!this.isScurrying) {
			Path path = this.entity.getNavigation().getPath();
			if (path != null) {
				double smallestDst = Double.MAX_VALUE;
				Node closestPathPoint = null;
				int closestPathPointIndex = 0;

				for (int i = 0; i < path.getNextNodeIndex(); i++) {
					Node point = path.getNode(i);

					double dst = this.entity.distanceToSqr(point.x + 0.5f, point.y + 0.5f, point.z + 0.5f);

					if (dst <= smallestDst) {
						smallestDst = dst;
						closestPathPoint = point;
						closestPathPointIndex = i;
					}
				}

				if (closestPathPoint != null) {
					Pair<Path, Integer> key = Pair.of(path, closestPathPointIndex);

					if (!this.pathMemory.containsKey(key)) {
						if (this.pathMemory.size() > this.maxPathMemory) {
							this.pathMemory.remove(this.pathMemory.keySet().iterator().next());
						}

						this.pathMemory.put(key, new BlockPos(closestPathPoint.x, closestPathPoint.y, closestPathPoint.z));
					}
				}
			}

			if (this.entity.isStalking || this.scurryingCooldown++ < 40) {
				return false;
			}
		}

		LivingEntity target = this.entity.getTarget();

		if (target != null) {
			Vec3 dir = this.entity.getEyePosition().subtract(target.getEyePosition()).normalize();
			float angle = (float) Math.toDegrees(Math.acos(dir.dot(target.getViewVector(1))));
			return angle < 5;
		}

		return false;
	}

	@Override
	public void start() {
		this.scurryingCooldown = 0;

		this.isScurrying = true;

		Node finalPoint = this.path != null ? this.path.getEndNode() : null;
		this.scurryingStart = this.entity.getTarget() != null ? this.entity.getTarget().position() : finalPoint != null ? new Vec3(finalPoint.x + 0.5f, finalPoint.y + 0.5f, finalPoint.z + 0.5f) : this.entity.position();

		Node[] pathPoints = new Node[this.pathMemory.size()];

		double furthestPathPosDst = 0;
		BlockPos furthestPathPos = null;

		int i = 0;
		for (BlockPos pos : this.pathMemory.values()) {
			pathPoints[this.pathMemory.size() - ++i] = new Node(pos.getX(), pos.getY(), pos.getZ());

			double dst = pos.distToCenterSqr((int) this.scurryingStart.x, (int) this.scurryingStart.y, (int) this.scurryingStart.z);
			if (dst > furthestPathPosDst) {
				furthestPathPosDst = dst;
				furthestPathPos = pos;
			}
		}

		if (furthestPathPos != null) {
			this.path = this.entity.getNavigation().createPath(furthestPathPos, 0);
		}

		if (this.path == null) {
			this.path = new Path(List.of(pathPoints), furthestPathPos, false);
		}

		for (int j = 0; (this.path == null || this.path.getNodeCount() < 6 || this.path.getEndNode().distanceTo(new Node((int) this.scurryingStart.x, (int) this.scurryingStart.y, (int) this.scurryingStart.z)) < 6) && j < 3; j++) {
			Vec3 dir = this.entity.position().subtract(this.scurryingStart).add(this.entity.getLookAngle().scale(-0.25f)).normalize();

			Vec3 right = dir.cross(new Vec3(0, 1, 0));
			Vec3 up = right.cross(dir);

			Vec3 target = this.entity.position().add(dir.scale(32.0f).add(right.scale((this.entity.getRandom().nextFloat() - 0.5f) * 16.0f)).add(up.scale((this.entity.getRandom().nextFloat() - 0.5f) * 16.0f)));

			this.path = this.entity.getNavigation().createPath(target.x, target.y, target.z, 0);
		}

		this.entity.playAmbientSound();
	}

	@Override
	public void tick() {
		if (this.path != null) {
			Node finalPoint = this.path.getEndNode();

			//Check if path still possible
			if (finalPoint == null || (this.entity.tickCount % 20 == 0 && this.entity.getNavigation().createPath(new BlockPos(finalPoint.x, finalPoint.y, finalPoint.z), 0) == null)) {
				this.isScurrying = false;
			} else {
				this.entity.getNavigation().moveTo(this.path, this.speed);
			}
		}
	}

	@Override
	public boolean canContinueToUse() {
		if (this.scurryingStart != null) {
			double distance = this.scurryingStart.distanceTo(this.entity.position());

			if (distance < this.lastDistance - 1) {
				return false;
			} else {
				this.lastDistance = Math.max(this.lastDistance, distance);
			}
		} else {
			return false;
		}

		return this.isScurrying && this.path != null && !this.path.isDone();
	}

	@Override
	public void stop() {
		this.isScurrying = false;
		this.scurryingStart = null;
		this.lastDistance = 0;
		this.path = null;
		this.pathMemory.clear();
		this.entity.getNavigation().stop();
	}
}
