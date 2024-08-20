package thebetweenlands.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import thebetweenlands.common.registries.BlockRegistry;

public class SpiritTreeLeavesBlock extends LeavesBlock {

	public static final VoxelShape COLLISION_SHAPE = Block.box(6.5D, 0.0D, 6.5D, 9.5D, 16.0D, 9.5D);
	public final LeafType type;

	public SpiritTreeLeavesBlock(LeafType type, Properties properties) {
		super(properties);
		this.type = type;
	}

	@Override
	protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return COLLISION_SHAPE;
	}

	@Override
	protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		BlockState above = level.getBlockState(pos.above());
		return switch (this.type) {
			case MIDDLE -> above.is(BlockRegistry.MIDDLE_SPIRIT_TREE_LEAVES) || above.is(BlockRegistry.TOP_SPIRIT_TREE_LEAVES);
			case BOTTOM -> above.is(BlockRegistry.MIDDLE_SPIRIT_TREE_LEAVES);
			default -> true;
		};
	}

	@Override
	protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
		return state.canSurvive(level, currentPos) ? state : Blocks.AIR.defaultBlockState();
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
		if (random.nextInt(100) == 0) {
			double px = (double) pos.getX() + random.nextDouble() * 0.5D;
			double py = (double) pos.getY() + random.nextDouble() * 0.5D;
			double pz = (double) pos.getZ() + random.nextDouble() * 0.5D;
			//BLParticles.SPIRIT_BUTTERFLY.spawn(level, px, py, pz);
		}
	}

	@Override
	protected boolean skipRendering(BlockState state, BlockState neighborState, Direction direction) {
		return state.is(BlockRegistry.TOP_SPIRIT_TREE_LEAVES) && neighborState.is(BlockRegistry.TOP_SPIRIT_TREE_LEAVES);
	}

	public enum LeafType {
		TOP, MIDDLE, BOTTOM
	}
}
