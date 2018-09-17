package thebetweenlands.common.entity.mobs;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.common.registries.BlockRegistry;

public abstract class EntitySpiritTreeFace extends EntityWallFace {
	public EntitySpiritTreeFace(World world) {
		super(world);
	}

	@Override
	protected boolean canDespawn() {
		return false;
	}

	@Override
	public boolean canResideInBlock(BlockPos pos) {
		return this.world.getBlockState(pos).getBlock() == BlockRegistry.LOG_SPIRIT_TREE;
	}

	@Override
	public boolean canMoveFaceInto(BlockPos pos) {
		return this.world.isAirBlock(pos);
	}
}
