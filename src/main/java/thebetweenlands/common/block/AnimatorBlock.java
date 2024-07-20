package thebetweenlands.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.api.recipes.AnimatorRecipe;
import thebetweenlands.common.block.entity.AlembicBlockEntity;
import thebetweenlands.common.block.entity.AnimatorBlockEntity;
import thebetweenlands.common.registries.BlockEntityRegistry;
import thebetweenlands.common.registries.RecipeRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AnimatorBlock extends HorizontalBaseEntityBlock {

	public AnimatorBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		if (level.isClientSide()) {
			return InteractionResult.CONSUME;
		}
		if (level.getBlockEntity(pos) instanceof AnimatorBlockEntity animator) {
			if (!animator.itemAnimated) {
				player.openMenu(animator);
			} else {
				SingleRecipeInput recipeInput = new SingleRecipeInput(animator.itemToAnimate);
				Optional<RecipeHolder<AnimatorRecipe>> recipe = level.getRecipeManager().getRecipeFor(RecipeRegistry.ANIMATOR_RECIPE.get(), recipeInput, level);
				if (recipe.isPresent() || recipe.get().value().onRetrieved(player, pos, recipeInput)) {
					player.openMenu(animator);
				}
				animator.fuelConsumed = 0;
			}
			animator.itemToAnimate = ItemStack.EMPTY;
			animator.itemAnimated = false;
		}

		return super.useWithoutItem(state, level, pos, player, hitResult);
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

			double xOff = facing.getStepX() * (facing.getAxis() == Direction.Axis.X ? 0.5F : -0.14F);
			double zOff = facing.getStepZ() * (facing.getAxis() == Direction.Axis.X ? 0.14F : -0.5F);

			// Runes
			List<Vec3> points = new ArrayList<>();
			points.add(new Vec3(pos.getX() + 0.5D + (random.nextFloat() - 0.5F) * 0.3D + xOff, pos.getY() + 0.9, pos.getZ() + 0.5 + (random.nextFloat() - 0.5F) * 0.3D + zOff));
			points.add(new Vec3(pos.getX() + 0.5D + (random.nextFloat() - 0.5F) * 0.3D + xOff, pos.getY() + 1.36, pos.getZ() + 0.5 + (random.nextFloat() - 0.5F) * 0.3D + zOff));
			points.add(new Vec3(pos.getX() + 0.5D, pos.getY() + 1.45D, pos.getZ() + 0.5D));
//			BLParticles.ANIMATOR.spawn(level, pos.getX(), pos.getY() + 0.9, pos.getZ() + 0.65, ParticleArgs.get().withData(points));
//			BLParticles.SMOKE.spawn(level, pos.getX() + 0.5 + random.nextFloat() * 0.3D - 0.15D, pos.getY() + 0.3, pos.getZ() + 0.5 + random.nextFloat() * 0.3D - 0.15D);
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
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new AnimatorBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return createTickerHelper(type, BlockEntityRegistry.ANIMATOR.get(), AnimatorBlockEntity::tick);
	}
}
