package thebetweenlands.common.block.container;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import javax.annotation.Nullable;
import thebetweenlands.common.block.entity.GrubHubBlockEntity;
import thebetweenlands.common.registries.BlockEntityRegistry;

import java.util.Optional;

public class GrubHubBlock extends BaseEntityBlock {

	public static final VoxelShape SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);

	public GrubHubBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec() {
		return null;
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
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		if (level.getBlockEntity(pos) instanceof GrubHubBlockEntity hub) {
			if (!hub.getItem(0).isEmpty()) {
				if (!level.isClientSide()) {
					ItemStack extracted = hub.getItem(0).copy();
					if (!player.isShiftKeyDown()) {
						extracted.setCount(1);
					}
					ItemEntity item = new ItemEntity(level, pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, extracted);
					item.setDeltaMovement(Vec3.ZERO);
					level.addFreshEntity(item);
					if (!player.isShiftKeyDown()) {
						hub.getItem(0).shrink(1);
					} else {
						hub.setItem(0, ItemStack.EMPTY);
					}
					level.sendBlockUpdated(pos, state, state, 2);
				}
				return InteractionResult.sidedSuccess(level.isClientSide());
			}
		}
		return super.useWithoutItem(state, level, pos, player, hitResult);
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new GrubHubBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return createTickerHelper(type, BlockEntityRegistry.GRUB_HUB.get(), GrubHubBlockEntity::tick);
	}
}
