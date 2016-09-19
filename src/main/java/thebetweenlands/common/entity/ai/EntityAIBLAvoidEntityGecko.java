package thebetweenlands.common.entity.ai;

import com.google.common.base.Predicate;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import thebetweenlands.common.entity.mobs.EntityGecko;
import thebetweenlands.common.registries.BlockRegistry;

import java.util.*;

public class EntityAIBLAvoidEntityGecko extends EntityAIBase {
	public final Predicate<Entity> viableSelector = new Predicate<Entity>() {
		@Override
		public boolean apply(Entity entity) {
			return entity.isEntityAlive() && gecko.getEntitySenses().canSee(entity);
		}
	};

	private final Comparator<BlockPos> closest = (a, b) -> {
		double aLength = a.getDistance(0, 0, 0);
		double bLength = b.getDistance(0, 0, 0);
		return aLength < bLength ? -1 : aLength > bLength ? 1 : 0;
	};

	private EntityGecko gecko;

	private double farSpeed;

	private double nearSpeed;

	private Entity closestLivingEntity;

	private float distance;

	private Path path;

	private PathNavigate navigator;

	private Class<? extends Entity> avoidingEntityClass;

	private boolean bushBound;

	private BlockPos target;

	public EntityAIBLAvoidEntityGecko(EntityGecko gecko, Class<? extends Entity> avoidingEntityClass, float distance, double farSpeed, double nearSpeed) {
		this.gecko = gecko;
		this.avoidingEntityClass = avoidingEntityClass;
		this.distance = distance;
		this.farSpeed = farSpeed;
		this.nearSpeed = nearSpeed;
		navigator = gecko.getNavigator();
		setMutexBits(1);
	}

	@Override
	public boolean shouldExecute() {
		if (gecko.isHiding()) {
			return false;
		}
		if (avoidingEntityClass == EntityPlayer.class) {
			closestLivingEntity = gecko.worldObj.getClosestPlayerToEntity(gecko, distance);
			if (closestLivingEntity == null) {
				return false;
			}
		} else {
			List<Entity> list = gecko.worldObj.getEntitiesWithinAABB(avoidingEntityClass, gecko.getEntityBoundingBox().expand(distance, 3.0D, distance), viableSelector);
			if (list.isEmpty()) {
				return false;
			}
			closestLivingEntity = list.get(0);
		}
		target = findNearBush();
		if (target == null) {
			Vec3d target = RandomPositionGenerator.findRandomTargetBlockAwayFrom(gecko, 16, 7, new Vec3d(closestLivingEntity.posX, closestLivingEntity.posY, closestLivingEntity.posZ));
			if (target != null) {
				this.target = new BlockPos(target);
			}
		} else {
			bushBound = true;
		}
		if (target == null) {
			return false;
		} else if (!bushBound && closestLivingEntity.getDistanceSq(target) < closestLivingEntity.getDistanceSqToEntity(gecko)) {
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
				return finalPathPoint.xCoord == target.getX() && finalPathPoint.yCoord == target.getY() && finalPathPoint.zCoord == target.getZ() || bushBound && doesPathDestinationNeighborBush(target, path);
			}
			return false;
		}
	}

	private boolean doesGeckoNeighborBush(BlockPos target) {
		BlockPos geckoPos = new BlockPos(gecko);
		for (EnumFacing facing : EnumFacing.values()) {
			if (geckoPos.distanceSq(target.offset(facing)) == 0) {
				return true;
			}
		}
		return false;
	}

	private boolean doesPathDestinationNeighborBush(BlockPos target, Path path) {
		for (EnumFacing facing : EnumFacing.values()) {
			BlockPos nearTarget = new BlockPos(target.offset(facing));
			PathPoint finalPathPoint = path.getFinalPathPoint();
			if (finalPathPoint.xCoord == nearTarget.getX() && finalPathPoint.yCoord == nearTarget.getY() && finalPathPoint.zCoord == nearTarget.getZ()) {
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
		for (int dx = -radius; dx <= radius; dx++) {
			for (int dy = -radius / 2; dy <= radius; dy++) {
				for (int dz = -radius; dz <= radius; dz++) {
					BlockPos pos = center.add(dx, dy, dz);
					IBlockState state = gecko.worldObj.getBlockState(pos);
					if (state.getBlock() == BlockRegistry.WEEDWOOD_BUSH && gecko.worldObj.isBlockNormalCube(pos.down(), false)) {
						bushes.add(pos);
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
	public boolean continueExecuting() {
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
		closestLivingEntity = null;
		bushBound = false;
	}

	@Override
	public void updateTask() {
		if (gecko.getDistanceSqToEntity(closestLivingEntity) < 49) {
			gecko.getNavigator().setSpeed(nearSpeed);
		} else {
			gecko.getNavigator().setSpeed(farSpeed);
		}
	}
}
