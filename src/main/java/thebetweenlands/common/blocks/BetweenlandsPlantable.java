package thebetweenlands.common.blocks;

import net.minecraft.world.level.block.Block;

public class BetweenlandsPlantable extends Block {
	public BetweenlandsPlantable(Properties p_49795_) {
		super(p_49795_);
	}
//TODO fixy as this doesn't exist now
	/*
	@Override
	public boolean canSustainPlant(BlockState state, BlockGetter world, BlockPos pos, Direction facing, IPlantable plantable) {
		PlantType type = plantable.getPlantType(world, pos.relative(facing));
		return PlantType.PLAINS.equals(type);
	}
	*/
}
