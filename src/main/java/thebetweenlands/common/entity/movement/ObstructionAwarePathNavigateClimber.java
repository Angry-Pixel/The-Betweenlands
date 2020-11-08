package thebetweenlands.common.entity.movement;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.World;
import thebetweenlands.common.entity.mobs.EntityClimberBase;

public class ObstructionAwarePathNavigateClimber<T extends EntityClimberBase> extends ObstructionAwarePathNavigateGround<T> {
	protected final EntityClimberBase climber;

	protected EnumFacing verticalFacing = EnumFacing.DOWN;

	public ObstructionAwarePathNavigateClimber(T entity, World worldIn, boolean checkObstructions, boolean canPathWalls, boolean canPathCeiling) {
		super(entity, worldIn, checkObstructions);

		this.climber = entity;

		if(this.nodeProcessor instanceof ObstructionAwareWalkNodeProcessor) {
			@SuppressWarnings("unchecked")
			ObstructionAwareWalkNodeProcessor<T> processor = (ObstructionAwareWalkNodeProcessor<T>) this.nodeProcessor;
			processor.setStartPathOnGround(false);
			processor.setCanPathWalls(canPathWalls);
			processor.setCanPathCeiling(canPathCeiling);
		}
	}

	@Override
	protected boolean canNavigate() {
		return !this.isInLiquid() || this.getCanSwim() && this.isInLiquid() || this.entity.isRiding();
	}

	@Override
	protected Vec3d getEntityPosition() {
		return this.entity.getPositionVector().add(0, this.entity.height / 2.0f, 0);
	}

	@Override
	@Nullable
	public Path getPathToPos(BlockPos pos) {
		if(!this.canNavigate()) {
			return null;
		} else if(this.currentPath != null && !this.currentPath.isFinished() && pos.equals(this.targetPos)) {
			return this.currentPath;
		} else {
			this.targetPos = pos;
			float searchRange = this.getPathSearchRange();
			this.world.profiler.startSection("pathfind");
			BlockPos cachePos = new BlockPos(this.entity);
			int cacheSize = (int)(searchRange + 8.0F);
			ChunkCache chunkCache = new ChunkCache(this.world, cachePos.add(-cacheSize, -cacheSize, -cacheSize), cachePos.add(cacheSize, cacheSize, cacheSize), 0);
			Path path = this.pathFinder.findPath(chunkCache, this.entity, this.targetPos, searchRange);
			this.world.profiler.endSection();
			return path;
		}
	}

	@Override
	@Nullable
	public Path getPathToEntityLiving(Entity entityIn) {
		if(!this.canNavigate()) {
			return null;
		} else {
			BlockPos pos = new BlockPos(entityIn);

			if(this.currentPath != null && !this.currentPath.isFinished() && pos.equals(this.targetPos)) {
				return this.currentPath;
			} else {
				this.targetPos = pos;
				float searchRange = this.getPathSearchRange();
				this.world.profiler.startSection("pathfind");
				BlockPos cachePos = pos.up();
				int cacheSize = (int)(searchRange + 16.0F);
				ChunkCache chunkCache = new ChunkCache(this.world, cachePos.add(-cacheSize, -cacheSize, -cacheSize), cachePos.add(cacheSize, cacheSize, cacheSize), 0);
				Path path = this.pathFinder.findPath(chunkCache, this.entity, entityIn, searchRange);
				this.world.profiler.endSection();
				return path;
			}
		}
	}

	@Override
	protected void pathFollow() {
		Vec3d pos = this.getEntityPosition();

		EntityClimberBase.Orientation orientation = this.climber.getOrientation(1);
		Vec3d upVector = orientation.getForward(this.climber.rotationYaw, -90);

		this.verticalFacing = EnumFacing.getFacingFromVector((float)upVector.x, (float)upVector.y, (float)upVector.z);
		EnumFacing.Axis verticalAxis = this.verticalFacing.getAxis();

		int firstDifferentHeightPoint = this.currentPath.getCurrentPathLength();

		switch(verticalAxis) {
		case X:
			for(int i = this.currentPath.getCurrentPathIndex(); i < this.currentPath.getCurrentPathLength(); ++i) {
				if(this.currentPath.getPathPointFromIndex(i).x != Math.floor(pos.x)) {
					firstDifferentHeightPoint = i;
					break;
				}
			}
			break;
		case Y:
			for(int i = this.currentPath.getCurrentPathIndex(); i < this.currentPath.getCurrentPathLength(); ++i) {
				if(this.currentPath.getPathPointFromIndex(i).y != Math.floor(pos.y)) {
					firstDifferentHeightPoint = i;
					break;
				}
			}
			break;
		case Z:
			for(int i = this.currentPath.getCurrentPathIndex(); i < this.currentPath.getCurrentPathLength(); ++i) {
				if(this.currentPath.getPathPointFromIndex(i).z != Math.floor(pos.z)) {
					firstDifferentHeightPoint = i;
					break;
				}
			}
			break;
		}

		this.maxDistanceToWaypoint = this.entity.width > 0.75F ? this.entity.width / 2.0F : 0.75F - this.entity.width / 2.0F;
		float maxDistanceToWaypointY = this.entity.height > 0.75F ? this.entity.height / 2.0F : 0.75F - this.entity.height / 2.0F;
		Vec3d currentTarget = this.currentPath.getCurrentPos();

		if(MathHelper.abs((float)(this.entity.posX - (currentTarget.x + 0.5D))) < this.maxDistanceToWaypoint &&
				MathHelper.abs((float)(this.entity.posY + this.entity.height / 2.0f - (currentTarget.y + 0.5D))) < maxDistanceToWaypointY &&
				MathHelper.abs((float)(this.entity.posZ - (currentTarget.z + 0.5D))) < this.maxDistanceToWaypoint){
			this.currentPath.setCurrentPathIndex(this.currentPath.getCurrentPathIndex() + 1);
		}

		int sizeX = MathHelper.ceil(this.entity.width);
		int sizeY = MathHelper.ceil(this.entity.height);
		int sizeZ = sizeX;

		for(int i = firstDifferentHeightPoint - 1; i >= this.currentPath.getCurrentPathIndex(); --i){
			if(this.isDirectPathBetweenPoints(pos, this.currentPath.getVectorFromIndex(this.entity, i), sizeX, sizeY, sizeZ)){
				this.currentPath.setCurrentPathIndex(i);
				break;
			}
		}

		this.checkForStuck(pos);
	}

	protected static double swizzle(Vec3d vec, EnumFacing.Axis axis) {
		switch(axis) {
		case X:
			return vec.x;
		case Y:
			return vec.y;
		case Z:
			return vec.z;
		}
		return 0;
	}

	protected static int swizzle(int x, int y, int z, EnumFacing.Axis axis) {
		switch(axis) {
		case X:
			return x;
		case Y:
			return y;
		case Z:
			return z;
		}
		return 0;
	}

	protected static int unswizzle(int x, int y, int z, EnumFacing.Axis ax, EnumFacing.Axis ay, EnumFacing.Axis az, EnumFacing.Axis axis) {
		EnumFacing.Axis unswizzle;
		if(axis == ax) {
			unswizzle = EnumFacing.Axis.X;
		} else if(axis == ay) {
			unswizzle = EnumFacing.Axis.Y;
		} else {
			unswizzle = EnumFacing.Axis.Z;
		}
		return swizzle(x, y, z, unswizzle);
	}

	@Override
	protected boolean isDirectPathBetweenPoints(Vec3d start, Vec3d end, int sizeX, int sizeY, int sizeZ) {
		switch(this.verticalFacing.getAxis()) {
		case X:
			return this.isDirectPathBetweenPoints(start, end, sizeX, sizeY, sizeZ, EnumFacing.Axis.Z, EnumFacing.Axis.X, EnumFacing.Axis.Y);
		case Y:
			return this.isDirectPathBetweenPoints(start, end, sizeX, sizeY, sizeZ, EnumFacing.Axis.X, EnumFacing.Axis.Y, EnumFacing.Axis.Z);
		case Z:
			return this.isDirectPathBetweenPoints(start, end, sizeX, sizeY, sizeZ, EnumFacing.Axis.Y, EnumFacing.Axis.Z, EnumFacing.Axis.X);
		}
		return false;
	}

	protected boolean isDirectPathBetweenPoints(Vec3d start, Vec3d end, int sizeX, int sizeY, int sizeZ, EnumFacing.Axis ax, EnumFacing.Axis ay, EnumFacing.Axis az) {
		int bx = MathHelper.floor(swizzle(start, ax));
		int bz = MathHelper.floor(swizzle(start, az));
		double dx = swizzle(end, ax) - swizzle(start, ax);
		double dz = swizzle(end, az) - swizzle(start, az);
		double dSq = dx * dx + dz * dz;

		int by = (int)swizzle(start, ay);

		int sizeX2 = swizzle(sizeX, sizeY, sizeZ, ax);
		int sizeY2 = swizzle(sizeX, sizeY, sizeZ, ay);
		int sizeZ2 = swizzle(sizeX, sizeY, sizeZ, az);

		if(dSq < 1.0E-8D) {
			return false;
		} else {
			double d3 = 1.0D / Math.sqrt(dSq);
			dx = dx * d3;
			dz = dz * d3;
			sizeX2 = sizeX2 + 2;
			sizeZ2 = sizeZ2 + 2;

			if(!this.isSafeToStandAt(
					unswizzle(bx, by, bz, ax, ay, az, EnumFacing.Axis.X), unswizzle(bx, by, bz, ax, ay, az, EnumFacing.Axis.Y), unswizzle(bx, by, bz, ax, ay, az, EnumFacing.Axis.Z),
					unswizzle(sizeX2, sizeY2, sizeZ2, ax, ay, az, EnumFacing.Axis.X), unswizzle(sizeX2, sizeY2, sizeZ2, ax, ay, az, EnumFacing.Axis.Y), unswizzle(sizeX2, sizeY2, sizeZ2, ax, ay, az, EnumFacing.Axis.Z),
					start, dx, dz, ax, ay, az)) {
				return false;
			} else {
				sizeX2 = sizeX2 - 2;
				sizeZ2 = sizeZ2 - 2;
				double stepX = 1.0D / Math.abs(dx);
				double stepZ = 1.0D / Math.abs(dz);
				double relX = (double)bx - swizzle(start, ax);
				double relZ = (double)bz - swizzle(start, az);

				if(dx >= 0.0D) {
					++relX;
				}

				if(dz >= 0.0D) {
					++relZ;
				}

				relX = relX / dx;
				relZ = relZ / dz;
				int dirX = dx < 0.0D ? -1 : 1;
				int dirZ = dz < 0.0D ? -1 : 1;
				int ex = MathHelper.floor(swizzle(end, ax));
				int ez = MathHelper.floor(swizzle(end, az));
				int offsetX = ex - bx;
				int offsetZ = ez - bz;

				while(offsetX * dirX > 0 || offsetZ * dirZ > 0) {
					if(relX < relZ) {
						relX += stepX;
						bx += dirX;
						offsetX = ex - bx;
					} else {
						relZ += stepZ;
						bz += dirZ;
						offsetZ = ez - bz;
					}

					if(!this.isSafeToStandAt(
							unswizzle(bx, by, bz, ax, ay, az, EnumFacing.Axis.X), unswizzle(bx, by, bz, ax, ay, az, EnumFacing.Axis.Y), unswizzle(bx, by, bz, ax, ay, az, EnumFacing.Axis.Z),
							unswizzle(sizeX2, sizeY2, sizeZ2, ax, ay, az, EnumFacing.Axis.X), unswizzle(sizeX2, sizeY2, sizeZ2, ax, ay, az, EnumFacing.Axis.Y), unswizzle(sizeX2, sizeY2, sizeZ2, ax, ay, az, EnumFacing.Axis.Z),
							start, dx, dz, ax, ay, az)) {
						return false;
					}
				}

				return true;
			}
		}
	}

	protected boolean isSafeToStandAt(int x, int y, int z, int sizeX, int sizeY, int sizeZ, Vec3d start, double dx, double dz, EnumFacing.Axis ax, EnumFacing.Axis ay, EnumFacing.Axis az) {
		int sizeX2 = swizzle(sizeX, sizeY, sizeZ, ax);
		int sizeZ2 = swizzle(sizeX, sizeY, sizeZ, az);

		int bx = swizzle(x, y, z, ax) - sizeX2 / 2;
		int bz = swizzle(x, y, z, az) - sizeZ2 / 2;

		int by = swizzle(x, y, z, ay);

		if(!this.isPositionClear(
				unswizzle(bx, y, bz, ax, ay, az, EnumFacing.Axis.X), unswizzle(bx, y, bz, ax, ay, az, EnumFacing.Axis.Y), unswizzle(bx, y, bz, ax, ay, az, EnumFacing.Axis.Z),
				sizeX, sizeY, sizeZ, start, dx, dz, ax, ay, az)) {
			return false;
		} else {
			for(int obx = bx; obx < bx + sizeX2; ++obx) {
				for(int obz = bz; obz < bz + sizeZ2; ++obz) {
					double offsetX = (double)obx + 0.5D - swizzle(start, ax);
					double offsetZ = (double)obz + 0.5D - swizzle(start, az);

					if(offsetX * dx + offsetZ * dz >= 0.0D) {
						PathNodeType nodeType = this.nodeProcessor.getPathNodeType(
								this.world,
								unswizzle(obx, by - 1, obz, ax, ay, az, EnumFacing.Axis.X), unswizzle(obx, by - 1, obz, ax, ay, az, EnumFacing.Axis.Y), unswizzle(obx, by - 1, obz, ax, ay, az, EnumFacing.Axis.Z),
								this.entity, sizeX, sizeY, sizeZ, true, true);

						if(nodeType == PathNodeType.WATER) {
							return false;
						}

						if(nodeType == PathNodeType.LAVA) {
							return false;
						}

						if(nodeType == PathNodeType.OPEN) {
							return false;
						}

						nodeType = this.nodeProcessor.getPathNodeType(
								this.world,
								unswizzle(obx, by, obz, ax, ay, az, EnumFacing.Axis.X), unswizzle(obx, by, obz, ax, ay, az, EnumFacing.Axis.Y), unswizzle(obx, by, obz, ax, ay, az, EnumFacing.Axis.Z),
								this.entity, sizeX, sizeY, sizeZ, true, true);
						float f = this.entity.getPathPriority(nodeType);

						if(f < 0.0F || f >= 8.0F) {
							return false;
						}

						if(nodeType == PathNodeType.DAMAGE_FIRE || nodeType == PathNodeType.DANGER_FIRE || nodeType == PathNodeType.DAMAGE_OTHER) {
							return false;
						}
					}
				}
			}

			return true;
		}
	}

	protected boolean isPositionClear(int x, int y, int z, int sizeX, int sizeY, int sizeZ, Vec3d start, double dx, double dz, EnumFacing.Axis ax, EnumFacing.Axis ay, EnumFacing.Axis az) {
		for(BlockPos pos : BlockPos.getAllInBox(new BlockPos(x, y, z), new BlockPos(x + sizeX - 1, y + sizeY - 1, z + sizeZ - 1))) {
			double offsetX = swizzle(pos.getX(), pos.getY(), pos.getZ(), ax) + 0.5D - swizzle(start, ax);
			double pffsetZ = swizzle(pos.getX(), pos.getY(), pos.getZ(), az) + 0.5D - swizzle(start, az);

			if(offsetX * dx + pffsetZ * dz >= 0.0D) {
				Block block = this.world.getBlockState(pos).getBlock();

				if(!block.isPassable(this.world, pos)) {
					return false;
				}
			}
		}

		return true;
	}
}
