package thebetweenlands.common.entity.ai;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.World;

public class ObstructionAwarePathNavigateGround<T extends EntityLiving & IPathObstructionAwareEntity> extends PathNavigateGround {
	public static interface PathObstructionCallback {
		public void onPathObstructed();
	}

	protected PathFinder pathFinder;
	protected long lastTimeUpdated;
	protected BlockPos targetPos;

	protected final T obstructionAwareEntity;
	protected final boolean startPathOnGround;

	protected int stuckCheckTicks = 0;

	public ObstructionAwarePathNavigateGround(T entity, World worldIn) {
		this(entity, worldIn, true, false, false, true);
	}

	@SuppressWarnings("unchecked")
	public ObstructionAwarePathNavigateGround(T entity, World worldIn, boolean checkObstructions, boolean canPathWalls, boolean canPathCeiling, boolean startPathOnGround) {
		super(entity, worldIn);
		this.obstructionAwareEntity = entity;
		this.startPathOnGround = startPathOnGround;

		if(this.nodeProcessor instanceof ObstructionAwareWalkNodeProcessor) {
			ObstructionAwareWalkNodeProcessor<T> processor = (ObstructionAwareWalkNodeProcessor<T>) this.nodeProcessor;
			processor.setObstructionAwareEntity(entity);
			processor.setStartPathOnGround(startPathOnGround);
			processor.setCheckObstructions(checkObstructions);
			processor.setCanPathWalls(canPathWalls);
			processor.setCanPathCeiling(canPathCeiling);
		}
	}

	@Override
	protected PathFinder getPathFinder() {
		this.nodeProcessor = new ObstructionAwareWalkNodeProcessor<T>();
		this.nodeProcessor.setCanEnterDoors(true);
		return this.pathFinder = new PathFinder(this.nodeProcessor);
	}
	
	@Override
	protected boolean canNavigate() {
        return !this.isInLiquid() || this.getCanSwim() && this.isInLiquid() || this.entity.isRiding();
    }
	
	@Override
	@Nullable
	public Path getPathToPos(BlockPos pos) {
		if(this.startPathOnGround) {
			return super.getPathToPos(pos);
		} else {
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
	}

	@Override
	@Nullable
	public Path getPathToEntityLiving(Entity entityIn) {
		if(this.startPathOnGround) {
			return super.getPathToEntityLiving(entityIn);
		} else {
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
	}

	@Override
	public void updatePath() {
		if(this.world.getTotalWorldTime() - this.lastTimeUpdated > 20L) {
			if(this.targetPos != null) {
				this.currentPath = null;
				this.currentPath = this.getPathToPos(this.targetPos);
				this.lastTimeUpdated = this.world.getTotalWorldTime();
				this.tryUpdatePath = false;
			}
		} else {
			this.tryUpdatePath = true;
		}
	}

	@Override
	protected void checkForStuck(Vec3d entityPos) {
		super.checkForStuck(entityPos);

		if(this.currentPath != null && !this.currentPath.isFinished()) {
			Vec3d target = this.currentPath.getVectorFromIndex(this.obstructionAwareEntity, Math.min(this.currentPath.getCurrentPathLength() - 1, this.currentPath.getCurrentPathIndex() + 0));
			Vec3d diff = target.subtract(entityPos);

			int axis = 0;
			double maxDiff = 0;
			for(int i = 0; i < 3; i++) {
				double d;

				switch(i) {
				default:
				case 0:
					d = Math.abs(diff.x);
					break;
				case 1:
					d = Math.abs(diff.y);
					break;
				case 2:
					d = Math.abs(diff.z);
					break;
				}

				if(d > maxDiff) {
					axis = i;
					maxDiff = d;
				}
			}

			int height = MathHelper.floor(this.obstructionAwareEntity.height + 1.0F);

			int ceilHalfWidth = MathHelper.ceil(this.obstructionAwareEntity.width / 2.0f + 0.05F);

			Vec3d checkPos;
			switch(axis) {
			default:
			case 0:
				checkPos = new Vec3d(entityPos.x + Math.signum(diff.x) * ceilHalfWidth, entityPos.y, target.z);
				break;
			case 1:
				checkPos = new Vec3d(entityPos.x, entityPos.y + (diff.y > 0 ? (height + 1) : -1), target.z);
				break;
			case 2:
				checkPos = new Vec3d(target.x, entityPos.y, entityPos.z + Math.signum(diff.z) * ceilHalfWidth);
				break;
			}

			Vec3d facingDiff = checkPos.subtract(entityPos.add(0, axis == 1 ? this.entity.height / 2 : 0, 0));
			EnumFacing facing = EnumFacing.getFacingFromVector((float)facingDiff.x, (float)facingDiff.y, (float)facingDiff.z);

			boolean blocked = false;

			loop: for(int yo = 0; yo < height; yo++) {
				for(int xzo = -ceilHalfWidth; xzo <= ceilHalfWidth; xzo++) {
					BlockPos pos = new BlockPos(checkPos.x + (axis != 0 ? xzo : 0), checkPos.y + (axis != 1 ? yo : 0), checkPos.z + (axis != 2 ? xzo : 0));

					IBlockState state = this.obstructionAwareEntity.world.getBlockState(pos);

					PathNodeType nodeType = state.getBlock().isPassable(this.obstructionAwareEntity.world, pos) ? PathNodeType.OPEN : PathNodeType.BLOCKED;

					if(nodeType == PathNodeType.BLOCKED) {
						AxisAlignedBB collisionBox = state.getCollisionBoundingBox(this.obstructionAwareEntity.world, pos);

						if(collisionBox != null && collisionBox.offset(pos).intersects(this.obstructionAwareEntity.getEntityBoundingBox().expand(Math.signum(diff.x) * 0.2D, Math.signum(diff.y) * 0.2D, Math.signum(diff.z) * 0.2D))) {
							blocked = true;
							break loop;
						}
					}
				}
			}

			if(blocked) {
				this.stuckCheckTicks++;

				if(this.stuckCheckTicks > this.obstructionAwareEntity.getMaxStuckCheckTicks()) {
					this.obstructionAwareEntity.onPathingObstructed(facing);
					this.stuckCheckTicks = 0;
				}
			} else {
				this.stuckCheckTicks = Math.max(this.stuckCheckTicks - 2, 0);
			}
		} else {
			this.stuckCheckTicks = Math.max(this.stuckCheckTicks - 4, 0);
		}
	}
}