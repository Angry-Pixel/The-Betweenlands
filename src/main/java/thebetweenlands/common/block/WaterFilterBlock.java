package thebetweenlands.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.common.block.entity.WaterFilterBlockEntity;
import thebetweenlands.common.registries.ItemRegistry;

import java.util.Optional;

public class WaterFilterBlock extends BaseEntityBlock {

	public WaterFilterBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec() {
		return null;
	}

	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		if (level.getBlockEntity(pos) instanceof WaterFilterBlockEntity filter) {
			Optional<IFluidHandler> fluidHandler = FluidUtil.getFluidHandler(level, pos, hitResult.getDirection());

			if (fluidHandler.isPresent() && FluidUtil.getFluidHandler(stack).isPresent()) {
				FluidUtil.interactWithFluidHandler(player, hand, level, pos, hitResult.getDirection());
				return ItemInteractionResult.sidedSuccess(level.isClientSide());
			} else if (stack.is(ItemRegistry.MOSS_FILTER) || stack.is(ItemRegistry.SILK_FILTER)) {
				if (filter.getItem(0).isEmpty()) {
					if (!level.isClientSide()) {
						filter.setItem(0, stack.copy());
						stack.consume(1, player);
						level.sendBlockUpdated(pos, state, state, 2);
					}
					return ItemInteractionResult.sidedSuccess(level.isClientSide());
				}
			}
		}

		return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		if (level.getBlockEntity(pos) instanceof WaterFilterBlockEntity filter) {
			boolean anyExtracted = false;
			if (player.isCrouching()) {
				for (int i = 0; i < filter.getContainerSize(); i++) {
					if (!filter.getItem(i).isEmpty()) {
						anyExtracted = true;
						if (!level.isClientSide()) {
							this.spitOutItems(level, pos, state, filter, i);
						}
					}
				}
			}
			return anyExtracted ? InteractionResult.sidedSuccess(level.isClientSide()) : super.useWithoutItem(state, level, pos, player, hitResult);
		}
		return super.useWithoutItem(state, level, pos, player, hitResult);
	}

	private void spitOutItems(Level level, BlockPos pos, BlockState state, WaterFilterBlockEntity filter, int slot) {
		ItemStack extracted = filter.getItem(slot);
		ItemEntity item = new ItemEntity(level, pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, extracted);
		item.setDeltaMovement(Vec3.ZERO);
		level.addFreshEntity(item);
		filter.setItem(slot, ItemStack.EMPTY);
		level.sendBlockUpdated(pos, state, state, 2);
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new WaterFilterBlockEntity(pos, state);
	}
}
