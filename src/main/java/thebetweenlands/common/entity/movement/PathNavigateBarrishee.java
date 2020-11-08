package thebetweenlands.common.entity.movement;

import net.minecraft.pathfinding.NodeProcessor;
import net.minecraft.world.World;
import thebetweenlands.common.entity.mobs.EntityBarrishee;

public class PathNavigateBarrishee extends ObstructionAwarePathNavigateGround<EntityBarrishee> {
	public PathNavigateBarrishee(EntityBarrishee entity, World worldIn) {
		super(entity, worldIn);
		this.setCanSwim(true);
	}
	
	@Override
	protected CustomPathFinder createPathFinder() {
		NodeProcessor nodeProcessor = new WalkNodeProcessorBarrishee();
		nodeProcessor.setCanEnterDoors(true);
		return new CustomPathFinder(nodeProcessor);
	}
}
