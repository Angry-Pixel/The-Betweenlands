package thebetweenlands.common.entity.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class PathNavigateFlyingBL extends PathNavigate {

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
		return new PathFinder(this.nodeProcessor = new FlyingNodeProcessorBL());
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
		Vec3d vec3d = this.getEntityPosition();
		float f = this.entity.width * this.entity.width;

		if (vec3d.squareDistanceTo(this.currentPath.getVectorFromIndex(this.entity, this.currentPath.getCurrentPathIndex())) < (double) f) {
			this.currentPath.incrementPathIndex();
		}

		for (int j = Math.min(this.currentPath.getCurrentPathIndex() + 6, this.currentPath.getCurrentPathLength() - 1); j > this.currentPath.getCurrentPathIndex(); --j) {
			Vec3d vec3d1 = this.currentPath.getVectorFromIndex(this.entity, j);

			if (vec3d1.squareDistanceTo(vec3d) <= 36.0D && this.isDirectPathBetweenPoints(vec3d, vec3d1, 0, 0, 0)) {
				this.currentPath.setCurrentPathIndex(j);
				break;
			}
		}

		this.checkForStuck(vec3d);
	}

	@Override
	protected void removeSunnyPath() {
		// super.removeSunnyPath();
	}

	@Override
	protected boolean isDirectPathBetweenPoints(Vec3d posVec31, Vec3d posVec32, int sizeX, int sizeY, int sizeZ) {
		RayTraceResult raytraceresult = this.world.rayTraceBlocks(posVec31, new Vec3d(posVec32.x, posVec32.y + (double) this.entity.height * 0.5D, posVec32.z), false, true, false);
		return raytraceresult == null || raytraceresult.typeOfHit == RayTraceResult.Type.MISS;
	}

	@Override
	public boolean canEntityStandOnPos(BlockPos pos) {
		return !this.world.getBlockState(pos).isNormalCube();
	}
}