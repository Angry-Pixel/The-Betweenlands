package thebetweenlands.common.block.container;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import javax.annotation.Nullable;
import thebetweenlands.common.block.entity.GroundItemBlockEntity;
import thebetweenlands.common.registries.BlockRegistry;

public class GroundItemBlock extends BaseEntityBlock {

	protected static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 6.0D, 14.0D);

	public GroundItemBlock(Properties properties) {
		super(properties);
	}

	public static void create(Level level, BlockPos pos, ItemStack stack) {
		BlockState state = BlockRegistry.GROUND_ITEM.get().defaultBlockState();
		if (!level.isClientSide() && state.canSurvive(level, pos)) {
			level.setBlockAndUpdate(pos, state);
			if (level.getBlockEntity(pos) instanceof GroundItemBlockEntity item) {
				item.setTheItem(stack);
			}
		}
	}

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec() {
		return null;
	}

	@Override
	protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		return level.getBlockState(pos.below()).isFaceSturdy(level, pos, Direction.UP);
	}

	@Override
	protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
		return state.canSurvive(level, pos) ? state : Blocks.AIR.defaultBlockState();
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		if (!level.isClientSide() && level.getBlockEntity(pos) instanceof GroundItemBlockEntity item) {
			ItemHandlerHelper.giveItemToPlayer(player, item.getTheItem());
			item.removeTheItem();
			level.removeBlock(pos, false);
		}
		return super.useWithoutItem(state, level, pos, player, hitResult);
	}

	@Override
	protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
		if (!newState.is(this)) {
			if (!level.isClientSide() && level.getBlockEntity(pos) instanceof GroundItemBlockEntity item && !item.getTheItem().isEmpty()) {
				Block.popResource(level, pos, item.getTheItem());
			}
		}
		super.onRemove(state, level, pos, newState, movedByPiston);
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new GroundItemBlockEntity(pos, state);
	}

	@Override
	public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
		if (level.getBlockEntity(pos) instanceof GroundItemBlockEntity item) {
			return item.getTheItem();
		}
		return ItemStack.EMPTY;
	}
}
