package thebetweenlands.entities.entityAI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Random;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.util.ForgeDirection;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.entities.mobs.EntityGecko;

public class EntityAIBLAvoidEntityGecko extends EntityAIBase {
	public final IEntitySelector viableSelector = new IEntitySelector() {
		@Override
		public boolean isEntityApplicable(Entity entity) {
			return entity.isEntityAlive() && gecko.getEntitySenses().canSee(entity);
		}
	};

	private final Comparator<Vec3> smallest = new Comparator<Vec3>() {
		@Override
		public int compare(Vec3 a, Vec3 b) {
			double aLength = a.lengthVector();
			double bLength = b.lengthVector();
			return aLength < bLength ? -1 : aLength > bLength ? 1 : 0;
		}
	};

	private EntityGecko gecko;

	private double farSpeed;

	private double nearSpeed;

	private Entity closestLivingEntity;

	private float distance;

	private PathEntity path;

	private PathNavigate navigator;

	private Class<?> avoidingEntityClass;

	private boolean bushBound;

	private Vec3 target;

	public EntityAIBLAvoidEntityGecko(EntityGecko gecko, Class<?> avoidingEntityClass, float distance, double farSpeed, double nearSpeed) {
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
			List<Entity> list = gecko.worldObj.selectEntitiesWithinAABB(avoidingEntityClass, gecko.boundingBox.expand(distance, 3.0D, distance), viableSelector);
			if (list.isEmpty()) {
				return false;
			}
			closestLivingEntity = list.get(0);
		}
		target = findNearBush();
		if (target == null) {
			target = RandomPositionGenerator.findRandomTargetBlockAwayFrom(gecko, 16, 7, Vec3.createVectorHelper(closestLivingEntity.posX, closestLivingEntity.posY, closestLivingEntity.posZ));
		} else {
			bushBound = true;
		}
		if (target == null) {
			return false;
		} else if (!bushBound && closestLivingEntity.getDistanceSq(target.xCoord, target.yCoord, target.zCoord) < closestLivingEntity.getDistanceSqToEntity(gecko)) {
			return false;
		} else {
			path = navigator.getPathToXYZ(target.xCoord, target.yCoord, target.zCoord);
			if (doesGeckoNeighborBush(target)) {
				gecko.setHidingBush(MathHelper.floor_double(gecko.posX), MathHelper.floor_double(gecko.posY), MathHelper.floor_double(gecko.posZ));
				gecko.startHiding();
				return false;
			}
			return path == null ? false : path.isDestinationSame(target) ? true : bushBound ? doesPathDestinationNeighborBush(target, path) : false;
		}
	}

	private boolean doesGeckoNeighborBush(Vec3 target) {
		Vec3 geckoPos = Vec3.createVectorHelper(MathHelper.floor_double(gecko.posX), MathHelper.floor_double(gecko.posY), MathHelper.floor_double(gecko.posZ));
		for (ForgeDirection dir : ForgeDirection.values()) {
			Vec3 nearTarget = Vec3.createVectorHelper(target.xCoord + dir.offsetX, target.yCoord + dir.offsetY, target.zCoord + dir.offsetZ);
			if (geckoPos.squareDistanceTo(nearTarget) == 0) {
				return true;
			}
		}
		return false;
	}

	private boolean doesPathDestinationNeighborBush(Vec3 target, PathEntity path) {
		for (ForgeDirection dir : ForgeDirection.values()) {
			Vec3 nearTarget = Vec3.createVectorHelper(target.xCoord + dir.offsetX, target.yCoord + dir.offsetY, target.zCoord + dir.offsetZ);
			if (path.isDestinationSame(nearTarget)) {
				return true;
			}
		}
		return false;
	}

	private Vec3 findNearBush() {
		final int radius = 8;
		int range = radius * 2 + 1;
		int centerX = MathHelper.floor_double(gecko.posX);
		int centerY = MathHelper.floor_double(gecko.posY);
		int centerZ = MathHelper.floor_double(gecko.posZ);
		Random rand = gecko.getRNG();
		List<Vec3> bushes = new ArrayList<Vec3>();
		for (int dx = -radius; dx <= radius; dx++) {
			for (int dy = -radius / 2; dy <= radius; dy++) {
				for (int dz = -radius; dz <= radius; dz++) {
					int x = centerX + dx;
					int y = centerY + dy;
					int z = centerZ + dz;
					if (gecko.worldObj.getBlock(x, y, z) == BLBlockRegistry.weedwoodBush &&	gecko.worldObj.isBlockNormalCubeDefault(x, y - 1, z, false)) {
						bushes.add(Vec3.createVectorHelper(dx, dy, dz));
					}
				}
			}
		}
		if (bushes.size() == 0) {
			return null;
		}
		Collections.sort(bushes, smallest);
		double targetDistance = bushes.get(0).lengthVector();
		final double epsilon = 1e-8;
		for (int i = 0, bushCount = bushes.size(); i < bushCount; i++) {
			boolean end = i == bushCount - 1;
			if (Math.abs(bushes.get(i).lengthVector() - targetDistance) > epsilon || end) {
				return bushes.get(rand.nextInt(end ? i + 1 : i)).addVector(centerX, centerY, centerZ); 
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
		PathPoint destination = path.getFinalPathPoint();
		gecko.setHidingBush(MathHelper.floor_double(target.xCoord), MathHelper.floor_double(target.yCoord), MathHelper.floor_double(target.zCoord));
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
