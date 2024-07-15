package thebetweenlands.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TemplePillarBlock extends RotatedPillarBlock {

	public static final VoxelShape X_AABB = Block.box(0.0D, 1.0D, 1.0D, 16.0D, 15.0D, 15.0D);
	public static final VoxelShape Y_AABB = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);
	public static final VoxelShape Z_AABB = Block.box(1.0D, 1.0D, 0.0D, 15.0D, 15.0D, 16.0D);

	public TemplePillarBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return switch (state.getValue(AXIS)) {
			case X -> X_AABB;
			case Y -> Y_AABB;
			case Z -> Z_AABB;
		};
	}
}
