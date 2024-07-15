package thebetweenlands.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import thebetweenlands.common.items.OctineIngotItem;

public class OctineBlock extends Block {
	public OctineBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
		if (OctineIngotItem.isTinder(ItemStack.EMPTY, neighborState)) {
			boolean isTouching = true;
			int x = pos.getX() - neighborPos.getX();
			int y = pos.getY() - neighborPos.getY();
			int z = pos.getZ() - neighborPos.getZ();
			Direction facing = Direction.fromDelta(x, y, z);

			if (neighborState.getBlock() instanceof MossBlock) {
				if (facing.equals(neighborState.getValue(DirectionalBlock.FACING)))
					isTouching = false;
			} else if (neighborState.getBlock() instanceof ThornsBlock) {
				BooleanProperty side = switch (facing.getOpposite()) {
					case UP -> ThornsBlock.UP;
					case NORTH -> ThornsBlock.NORTH;
					case SOUTH -> ThornsBlock.SOUTH;
					case WEST -> ThornsBlock.WEST;
					case EAST -> ThornsBlock.EAST;
					default -> null;
				};
				if (side == null || neighborState.getValue(side))
					isTouching = false;
			}
			if (isTouching)
				level.setBlock(neighborPos, Blocks.FIRE.defaultBlockState(), 3);
		}
		return state;
	}
}
