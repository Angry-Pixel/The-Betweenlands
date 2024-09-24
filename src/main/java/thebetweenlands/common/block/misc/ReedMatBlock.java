package thebetweenlands.common.block.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import thebetweenlands.common.block.container.FishingTackleBoxBlock;
import thebetweenlands.common.entity.Seat;

public class ReedMatBlock extends Block {

	public static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);

	public ReedMatBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		if (level.isEmptyBlock(pos.above()) && level.isEmptyBlock(pos.above(2)) && !FishingTackleBoxBlock.isSatOn(level, pos) && hitResult.getDirection() == Direction.UP && !player.isPassenger()) {
			if (!level.isClientSide()) {
				this.seatPlayer(level, player, pos);
			}
			return InteractionResult.sidedSuccess(level.isClientSide());
		}
		return super.useWithoutItem(state, level, pos, player, hitResult);
	}

	public void seatPlayer(Level level, Player player, BlockPos pos) {
		Seat seat = new Seat(level, false);
		seat.setPos(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
		seat.setSeatOffset(-0.45F);
		level.addFreshEntity(seat);
		player.startRiding(seat, true);
	}
}
