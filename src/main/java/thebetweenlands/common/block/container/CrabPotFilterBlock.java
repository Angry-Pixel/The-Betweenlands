package thebetweenlands.common.block.container;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import javax.annotation.Nullable;
import thebetweenlands.common.block.misc.HorizontalBaseEntityBlock;
import thebetweenlands.common.block.entity.CrabPotFilterBlockEntity;
import thebetweenlands.common.block.waterlog.SwampWaterLoggable;
import thebetweenlands.common.registries.BlockEntityRegistry;
import thebetweenlands.common.registries.BlockRegistry;

import java.util.List;

public class CrabPotFilterBlock extends HorizontalBaseEntityBlock implements SwampWaterLoggable {

	public CrabPotFilterBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(WATER_TYPE, WaterType.NONE));
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return super.getStateForPlacement(context).setValue(WATER_TYPE, WaterType.getFromFluid(context.getLevel().getFluidState(context.getClickedPos()).getType()));
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		super.setPlacedBy(level, pos, state, placer, stack);
		if (level.getBlockState(pos.above()).is(BlockRegistry.CRAB_POT)) {
			level.setBlockAndUpdate(pos.above(), level.getBlockState(pos.above()).setValue(CrabPotBlock.FACING, state.getValue(FACING)));
		}
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		if (state.getValue(WATER_TYPE) == WaterType.NONE) {
			player.displayClientMessage(Component.translatable("block.thebetweenlands.crab_pot_filter.not_underwater").withStyle(ChatFormatting.RED), true);
			return InteractionResult.CONSUME;
		}
		if (level.isClientSide()) {
			return InteractionResult.SUCCESS;
		} else {
			if (level.getBlockEntity(pos) instanceof CrabPotFilterBlockEntity filter) {
				player.openMenu(filter);
			}
			return InteractionResult.CONSUME;
		}
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
		if (level.getBlockEntity(pos) instanceof CrabPotFilterBlockEntity filter) {
			if (random.nextInt(3) == 0)
				if (filter.isBaited() && filter.isProgressing()) {
					for (int i = 0; i < 5 + random.nextInt(5); i++) {
//						BatchedParticleRenderer.INSTANCE.addParticle(
//							DefaultParticleBatches.TRANSLUCENT_NEAREST_NEIGHBOR,
//							BLParticles.SMOOTH_SMOKE.create(level, pos.getX() + 0.5F, pos.getY() + 0.99F, pos.getZ() + 0.5F,
//								ParticleArgs.get().withMotion((random.nextFloat() - 0.5f) * 0.01f, -random.nextFloat() * -0.05F - 0.05F, (random.nextFloat() - 0.5f) * 0.01f)
//									.withScale(0.5f + random.nextFloat() * 8.0F)
//									.withColor(0.59F, 0.29F, 0F, 0.3f).withData(80, true, 0.01F, true)));
					}
				}
		}
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
		tooltip.add(Component.translatable(this.getDescriptionId() + ".desc").withStyle(ChatFormatting.GRAY));
	}

	@Override
	protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
		if (state.getValue(WATER_TYPE) != WaterType.NONE) {
			level.scheduleTick(pos, state.getValue(WATER_TYPE).getFluid(), state.getValue(WATER_TYPE).getFluid().getTickDelay(level));
		}

		return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
	}

	@Override
	protected FluidState getFluidState(BlockState state) {
		return state.getValue(WATER_TYPE).getFluid().defaultFluidState();
	}

	@Override
	protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
		Containers.dropContentsOnDestroy(state, newState, level, pos);
		super.onRemove(state, level, pos, newState, movedByPiston);
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new CrabPotFilterBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return createTickerHelper(type, BlockEntityRegistry.CRAB_POT_FILTER.get(), CrabPotFilterBlockEntity::tick);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder.add(WATER_TYPE));
	}
}
