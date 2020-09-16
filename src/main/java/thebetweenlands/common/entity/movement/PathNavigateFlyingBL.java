package thebetweenlands.common.entity.movement;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.World;

public class PathNavigateFlyingBL extends PathNavigate {
	protected BlockPos targetPos;
	protected long lastTimeUpdated;

	public PathNavigateFlyingBL(EntityLiving entitylivingIn, World worldIn) {
		super(entitylivingIn, worldIn);
	}

	public PathNavigateFlyingBL(EntityLiving entitylivingIn, World worldIn, int preferredMinHeight) {
		super(entitylivingIn, worldIn);
		if(this.nodeProcessor instanceof FlyingNodeProcessorBL) {
			((FlyingNodeProcessorBL)this.nodeProcessor).preferredMinHeight = preferredMinHeight;
		}
	}

	@Override
	protected PathFinder getPathFinder() {
		return new FlyingPathFinder(this.nodeProcessor = new FlyingNodeProcessorBL());
	}

	@Override
	public void onUpdateNavigation() {
		++this.totalTicks;

		if (this.tryUpdatePath) {
			this.updatePath();
		}

		if (!this.noPath()) {
			if (this.canNavigate()) {
				this.pathFollow();
			} else if (this.currentPath != null && this.currentPath.getCurrentPathIndex() < this.currentPath.getCurrentPathLength()) {
				Vec3d vec3d = this.getEntityPosition();
				Vec3d vec3d1 = this.currentPath.getVectorFromIndex(this.entity, this.currentPath.getCurrentPathIndex());

				if (vec3d.y > vec3d1.y && !this.entity.onGround && MathHelper.floor(vec3d.x) == MathHelper.floor(vec3d1.x) && MathHelper.floor(vec3d.z) == MathHelper.floor(vec3d1.z)) {
					this.currentPath.setCurrentPathIndex(this.currentPath.getCurrentPathIndex() + 1);
				}
			}

			this.debugPathFinding();

			if (!this.noPath()) {
				Vec3d vec3d2 = this.currentPath.getPosition(this.entity);
				BlockPos blockpos = (new BlockPos(vec3d2)).down();
				AxisAlignedBB axisalignedbb = this.world.getBlockState(blockpos).getBoundingBox(this.world, blockpos);
				vec3d2 = vec3d2.subtract(0.0D, 1.0D - axisalignedbb.maxY, 0.0D);
				this.entity.getMoveHelper().setMoveTo(vec3d2.x, vec3d2.y, vec3d2.z, this.speed);
			}
		}
	}

	@Override
	protected boolean canNavigate() {
		return !this.isInLiquid();
	}

	@Override
	protected Vec3d getEntityPosition() {
		return new Vec3d(this.entity.posX, this.entity.posY + (double) this.entity.height * 0.5D, this.entity.posZ);
	}

	@Override
	protected void pathFollow() {
		Vec3d currentPosition = this.getEntityPosition();
		float f = this.entity.width * this.entity.width;

		if (currentPosition.squareDistanceTo(this.currentPath.getVectorFromIndex(this.entity, this.currentPath.getCurrentPathIndex())) < (double) f) {
			this.currentPath.incrementPathIndex();
		}

		for (int j = Math.min(this.currentPath.getCurrentPathIndex() + 6, this.currentPath.getCurrentPathLength() - 1); j > this.currentPath.getCurrentPathIndex(); --j) {
			Vec3d pathNodePosition = this.currentPath.getVectorFromIndex(this.entity, j);

			if (pathNodePosition.squareDistanceTo(currentPosition) <= 256.0D && this.isDirectPathBetweenPoints(currentPosition, pathNodePosition, 0, 0, 0)) {
				this.currentPath.setCurrentPathIndex(j);
				break;
			}
		}

		this.checkForStuck(currentPosition);
	}

	@Override
	protected void removeSunnyPath() {
		// super.removeSunnyPath();
	}

	@Override
	protected boolean isDirectPathBetweenPoints(Vec3d start, Vec3d end, int sizeX, int sizeY, int sizeZ) {
		RayTraceResult raytraceresult = this.world.rayTraceBlocks(start, new Vec3d(end.x, end.y + (double) this.entity.height * 0.5D, end.z), false, true, false);
		return raytraceresult == null || raytraceresult.typeOfHit == RayTraceResult.Type.MISS;
	}

	@Override
	public boolean canEntityStandOnPos(BlockPos pos) {
		return !this.world.getBlockState(pos).isNormalCube();
	}

	@Override
	public void updatePath() {
		if (this.world.getTotalWorldTime() - this.lastTimeUpdated > 20L) {
			if (this.targetPos != null) {
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
	@Nullable
	public Path getPathToPos(BlockPos pos) {
		if (!this.canNavigate()) {
			return null;
		} else if (this.currentPath != null && !this.currentPath.isFinished() && pos.equals(this.targetPos)) {
			return this.currentPath;
		} else {
			this.targetPos = pos;
			float f = this.getPathSearchRange();
			this.world.profiler.startSection("pathfind");
			BlockPos blockpos = new BlockPos(this.entity);
			int i = (int)(f + 8.0F);
			ChunkCache chunkcache = new ChunkCache(this.world, blockpos.add(-i, -i, -i), blockpos.add(i, i, i), 0);
			Path path = this.getPathFinder().findPath(chunkcache, this.entity, this.targetPos, f);
			this.world.profiler.endSection();
			return path;
		}
	}

	@Override
	@Nullable
	public Path getPathToEntityLiving(Entity entityIn) {
		if (!this.canNavigate()) {
			return null;
		} else {
			BlockPos blockpos = new BlockPos(entityIn);

			if (this.currentPath != null && !this.currentPath.isFinished() && blockpos.equals(this.targetPos)) {
				return this.currentPath;
			} else {
				this.targetPos = blockpos;
				float f = this.getPathSearchRange();
				this.world.profiler.startSection("pathfind");
				BlockPos blockpos1 = (new BlockPos(this.entity)).up();
				int i = (int)(f + 16.0F);
				ChunkCache chunkcache = new ChunkCache(this.world, blockpos1.add(-i, -i, -i), blockpos1.add(i, i, i), 0);
				Path path = this.getPathFinder().findPath(chunkcache, this.entity, new BlockPos(entityIn.posX, entityIn.getEntityBoundingBox().minY + entityIn.height / 2.0D, entityIn.posZ), f);
				this.world.profiler.endSection();
				return path;
			}
		}
	}
}