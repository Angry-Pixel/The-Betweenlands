package thebetweenlands.common.block.container;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.BlockGetter;
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
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import javax.annotation.Nullable;
import thebetweenlands.api.recipes.AnimatorRecipe;
import thebetweenlands.client.particle.ParticleFactory;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.misc.HorizontalBaseEntityBlock;
import thebetweenlands.common.block.entity.AnimatorBlockEntity;
import thebetweenlands.common.block.waterlog.SwampWaterLoggable;
import thebetweenlands.common.registries.BlockEntityRegistry;
import thebetweenlands.common.registries.ParticleRegistry;
import thebetweenlands.common.registries.RecipeRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AnimatorBlock extends HorizontalBaseEntityBlock implements SwampWaterLoggable {

	private static final VoxelShape SHAPE = Block.box(0.01D, 0.0D, 0.01D, 15.99D, 15.0D, 15.99D);

	public AnimatorBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(WATER_TYPE, WaterType.NONE));
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		if (level.isClientSide()) {
			return InteractionResult.SUCCESS;
		} else {
			if (level.getBlockEntity(pos) instanceof AnimatorBlockEntity animator) {
				if (!animator.itemAnimated) {
					player.openMenu(animator, buf -> buf.writeBlockPos(pos));
				} else {
					SingleRecipeInput recipeInput = new SingleRecipeInput(animator.itemToAnimate);
					Optional<RecipeHolder<AnimatorRecipe>> recipe = level.getRecipeManager().getRecipeFor(RecipeRegistry.ANIMATOR_RECIPE.get(), recipeInput, level);
					if (recipe.isEmpty() || recipe.get().value().onRetrieved(player, pos, recipeInput)) {
						player.openMenu(animator, buf -> buf.writeBlockPos(pos));
					}
					animator.fuelConsumed = 0;
				}
				animator.itemToAnimate = ItemStack.EMPTY;
				animator.itemAnimated = false;
			}
			return InteractionResult.CONSUME;
		}
	}

	@Override
	protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
		Containers.dropContentsOnDestroy(state, newState, level, pos);
		super.onRemove(state, level, pos, newState, movedByPiston);
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
		if (level.getBlockEntity(pos) instanceof AnimatorBlockEntity animator && animator.isRunning()) {
			Direction facing = state.getValue(FACING);

			double zOff = facing.getStepX() * (facing.getAxis() == Direction.Axis.X ? 0.5F : -0.14F);
			double xOff = facing.getStepZ() * (facing.getAxis() == Direction.Axis.X ? 0.14F : -0.5F);

			// Runes
			List<Vec3> points = new ArrayList<>();
			points.add(new Vec3(pos.getX() + 0.5D + (random.nextFloat() - 0.5F) * 0.3D + xOff, pos.getY() + 0.9, pos.getZ() + 0.5 + (random.nextFloat() - 0.5F) * 0.3D + zOff));
			points.add(new Vec3(pos.getX() + 0.5D + (random.nextFloat() - 0.5F) * 0.3D + xOff, pos.getY() + 1.36, pos.getZ() + 0.5 + (random.nextFloat() - 0.5F) * 0.3D + zOff));
			points.add(new Vec3(pos.getX() + 0.5D, pos.getY() + 1.45D, pos.getZ() + 0.5D));
			TheBetweenlands.createParticle(ParticleRegistry.ANIMATOR.get(), level, points.getFirst().x(), points.getFirst().y(), points.getFirst().z(), ParticleFactory.ParticleArgs.get().withData(points));
			TheBetweenlands.createParticle(ParticleTypes.WHITE_SMOKE, level, pos.getX() + 0.5 + random.nextFloat() * 0.3D - 0.15D, pos.getY() + 0.3, pos.getZ() + 0.5 + random.nextFloat() * 0.3D - 0.15D);
		}
	}

	@Override
	protected boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}

	@Override
	protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
		if (level.getBlockEntity(pos) instanceof AnimatorBlockEntity animator) {
			return Math.round(((float) animator.fuelConsumed / (float) animator.requiredFuelCount) * 16.0F);
		}
		return 0;
	}


	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return super.getStateForPlacement(context).setValue(WATER_TYPE, WaterType.getFromFluid(context.getLevel().getFluidState(context.getClickedPos()).getType()));
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

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new AnimatorBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return createTickerHelper(type, BlockEntityRegistry.ANIMATOR.get(), AnimatorBlockEntity::tick);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder.add(WATER_TYPE));
	}
}
