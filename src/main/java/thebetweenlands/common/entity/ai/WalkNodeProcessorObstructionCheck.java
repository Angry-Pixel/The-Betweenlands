package thebetweenlands.common.entity.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.WalkNodeProcessor;
import net.minecraft.world.IBlockAccess;

public class WalkNodeProcessorObstructionCheck extends WalkNodeProcessor {
	private IPathObstructionCallback callback;

	public void setCallback(IPathObstructionCallback callback) {
		this.callback = callback;
		this.callback.setMalusMode(true);
	}

	@Override
	public PathNodeType getPathNodeType(IBlockAccess blockaccessIn, int x, int y, int z, EntityLiving entitylivingIn, int xSize, int ySize, int zSize, boolean canBreakDoorsIn, boolean canEnterDoorsIn) {
		this.callback.setMalusMode(false);
		PathNodeType type = super.getPathNodeType(blockaccessIn, x, y, z, entitylivingIn, xSize, ySize, zSize, canBreakDoorsIn, canEnterDoorsIn);
		this.callback.setMalusMode(true);
		return type;
	}
}