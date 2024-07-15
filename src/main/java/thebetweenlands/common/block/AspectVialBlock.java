package thebetweenlands.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.api.aspect.Aspect;
import thebetweenlands.api.aspect.AspectContainerItem;
import thebetweenlands.common.block.entity.AspectVialBlockEntity;
import thebetweenlands.common.herblore.Amounts;

public class AspectVialBlock extends BaseEntityBlock {

	public static final VoxelShape SHAPE = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 7.0D, 12.0D);
	public static final BooleanProperty RANDOM_POSITION = BooleanProperty.create("random_position");
	private Holder<Item> vialItem;

	public AspectVialBlock(Holder<Item> vialItem, Properties properties) {
		super(properties);
		this.vialItem = vialItem;
		this.registerDefaultState(this.getStateDefinition().any().setValue(RANDOM_POSITION, false));
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
		if (level.getBlockEntity(pos) instanceof AspectVialBlockEntity vial) {

			AspectContainerItem container;
			if (stack.is(ItemRegistry.ASPECT_VIAL) && (container = AspectContainerItem.fromItem(stack)).getAspects().size() == 1) {
				Aspect itemAspect = container.getAspects().getFirst();
				if (!player.isCrouching()) {
					if (vial.getAspect() == null || vial.getAspect().type == itemAspect.type) {
						if (!level.isClientSide()) {
							if (vial.getAspect() == null)
								vial.setAspect(new Aspect(itemAspect.type, 0));
							int added = vial.addAmount(Math.min(itemAspect.amount, 100));
							if (added > 0) {
								int leftAmount = itemAspect.amount - added;
								container.set(itemAspect.type, itemAspect.amount - added);
								if (leftAmount <= 0) {
									player.setItemInHand(hand, new ItemStack(this.vialItem));
								}
							}
						}
						return ItemInteractionResult.sidedSuccess(level.isClientSide());
					}
				} else {
					if (vial.getAspect() != null && vial.getAspect().type == itemAspect.type) {
						if (!level.isClientSide()) {
							int toRemove = Math.min(100, Amounts.VIAL - itemAspect.amount);
							if (toRemove > 0) {
								int removedAmount = vial.removeAmount(toRemove);
								container.set(itemAspect.type, itemAspect.amount + removedAmount);
							}
						}
						return ItemInteractionResult.sidedSuccess(level.isClientSide());
					}
				}
			} else if (stack.is(ItemRegistry.DENTROTHYST_VIAL) && player.isCrouching() && vial.getAspect() != null) {
				if (!level.isClientSide()) {
					Aspect aspect = vial.getAspect();
					int removedAmount = vial.removeAmount(100);
					if (removedAmount > 0) {
						container = AspectContainerItem.fromItem(new ItemStack(this.vialItem));
						container.add(aspect.type, removedAmount);

						stack.shrink(1);
						if (stack.getCount() <= 0)
							player.setItemInHand(hand, stack);

						//Drop new aspect item
						ItemEntity itemEntity = player.drop(new ItemStack(this.vialItem), false);
						if (itemEntity != null) itemEntity.setNoPickUpDelay();
					}
				}
				return ItemInteractionResult.sidedSuccess(level.isClientSide());
			}
		}
		return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		if (player.isCrouching()) {
			if (!level.isClientSide()) {
				level.setBlockAndUpdate(pos, state.setValue(RANDOM_POSITION, !state.getValue(RANDOM_POSITION)));
			}
			return InteractionResult.sidedSuccess(level.isClientSide());
		}
		return super.useWithoutItem(state, level, pos, player, hitResult);
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new AspectVialBlockEntity(pos, state);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(RANDOM_POSITION);
	}
}
