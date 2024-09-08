package thebetweenlands.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidActionResult;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import thebetweenlands.common.block.entity.PurifierBlockEntity;
import thebetweenlands.common.registries.BlockEntityRegistry;
import thebetweenlands.common.registries.FluidRegistry;

import javax.annotation.Nullable;
import java.util.Optional;

public class PurifierBlock extends HorizontalBaseEntityBlock {

	public static final BooleanProperty LIT = BlockStateProperties.LIT;
	public static final VoxelShape SHAPE = Shapes.or(
		Block.box(0.0D, 0.0D, 0.0D, 3.0D, 4.0D, 3.0D),
		Block.box(0.0D, 0.0D, 13.0D, 3.0D, 4.0D, 16.0D),
		Block.box(13.0D, 0.0D, 0.0D, 16.0D, 4.0D, 3.0D),
		Block.box(13.0D, 0.0D, 13.0D, 16.0D, 4.0D, 16.0D),
		Block.box(0.0D, 4.0D, 0.0D, 16.0D, 14.0D, 16.0D));

	public PurifierBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(LIT, false));
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		Optional<IFluidHandler> fluidHandler = FluidUtil.getFluidHandler(level, pos, hitResult.getDirection());

		if (fluidHandler.isPresent() && FluidUtil.getFluidHandler(stack).isPresent()) {
			if (FluidUtil.interactWithFluidHandler(player, hand, level, pos, hitResult.getDirection())) {
				return ItemInteractionResult.sidedSuccess(level.isClientSide());
			}
		}

		return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult result) {
		if (level.isClientSide()) {
			return InteractionResult.SUCCESS;
		} else {
			if (level.getBlockEntity(pos) instanceof PurifierBlockEntity purifier) {
				player.openMenu(purifier);
			}
			return InteractionResult.CONSUME;
		}
	}

	@Override
	protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
		Containers.dropContentsOnDestroy(state, newState, level, pos);
		super.onRemove(state, level, pos, newState, movedByPiston);
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new PurifierBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return createTickerHelper(type, BlockEntityRegistry.PURIFIER.get(), PurifierBlockEntity::tick);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder.add(LIT));
	}
}
