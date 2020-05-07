package thebetweenlands.common.entity.ai;

import net.minecraft.pathfinding.PathFinder;
import net.minecraft.world.World;
import thebetweenlands.common.entity.mobs.EntityBarrishee;

public class PathNavigateBarrishee extends ObstructionAwarePathNavigateGround<EntityBarrishee> {
	public PathNavigateBarrishee(EntityBarrishee entity, World worldIn) {
		super(entity, worldIn);
		this.setCanSwim(true);
	}

	@Override
	protected PathFinder getPathFinder() {
		this.nodeProcessor = new WalkNodeProcessorBarrishee();
		this.nodeProcessor.setCanEnterDoors(true);
		return new PathFinder(this.nodeProcessor);
	}
}
