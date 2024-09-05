package thebetweenlands.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import thebetweenlands.common.block.entity.MossBedBlockEntity;

public class MossBedBlock extends BedBlock {

	protected static final VoxelShape BASE = Block.box(0.0, 3.0, 0.0, 16.0, 8.0, 16.0);
	protected static final VoxelShape LEG_NORTH_WEST = Block.box(0.0, 0.0, 0.0, 3.0, 3.0, 3.0);
	protected static final VoxelShape LEG_SOUTH_WEST = Block.box(0.0, 0.0, 13.0, 3.0, 3.0, 16.0);
	protected static final VoxelShape LEG_NORTH_EAST = Block.box(13.0, 0.0, 0.0, 16.0, 3.0, 3.0);
	protected static final VoxelShape LEG_SOUTH_EAST = Block.box(13.0, 0.0, 13.0, 16.0, 3.0, 16.0);
	protected static final VoxelShape LEG_NORTH = Block.box(6.5, 0.0, 0.0, 9.5, 3.0, 2.0);
	protected static final VoxelShape LEG_SOUTH = Block.box(6.5, 0.0, 14.0, 9.5, 3.0, 16.0);
	protected static final VoxelShape LEG_WEST = Block.box(0.0, 0.0, 6.5, 2.0, 3.0, 9.5);
	protected static final VoxelShape LEG_EAST = Block.box(14.0, 0.0, 6.5, 16.0, 3.0, 9.5);



	protected static final VoxelShape NORTH_SHAPE = Shapes.or(BASE, LEG_NORTH_WEST, LEG_NORTH, LEG_NORTH_EAST);
	protected static final VoxelShape SOUTH_SHAPE = Shapes.or(BASE, LEG_SOUTH_WEST, LEG_SOUTH, LEG_SOUTH_EAST);
	protected static final VoxelShape WEST_SHAPE = Shapes.or(BASE, LEG_NORTH_WEST, LEG_WEST, LEG_SOUTH_WEST);
	protected static final VoxelShape EAST_SHAPE = Shapes.or(BASE, LEG_NORTH_EAST, LEG_EAST, LEG_SOUTH_EAST);

	public MossBedBlock(Properties properties) {
		super(DyeColor.GREEN, properties);
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		Direction direction = getConnectedDirection(state).getOpposite();
		return switch (direction) {
			case NORTH -> NORTH_SHAPE;
			case SOUTH -> SOUTH_SHAPE;
			case WEST -> WEST_SHAPE;
			default -> EAST_SHAPE;
		};
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new MossBedBlockEntity(pos, state);
	}
}
