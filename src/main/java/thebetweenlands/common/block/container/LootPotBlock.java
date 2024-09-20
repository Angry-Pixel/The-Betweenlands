package thebetweenlands.common.block.container;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import thebetweenlands.common.block.misc.HorizontalBaseEntityBlock;
import thebetweenlands.common.block.entity.LootPotBlockEntity;

import javax.annotation.Nullable;

public class LootPotBlock extends HorizontalBaseEntityBlock {

	public static final VoxelShape POT_1 = Shapes.or(
		Block.box(4.0D, 0.0D, 4.0D, 12.0D, 2.0D, 12.0D),
		Block.box(2.0D, 2.0D, 2.0D, 14.0D, 12.0D, 14.0D),
		Block.box(3.0D, 12.0D, 3.0D, 13.0D, 14.0D, 13.0D),
		Block.box(6.0D, 14.0D, 6.0D, 10.0D, 16.0D, 10.0D));

	public static final VoxelShape POT_2 = Shapes.or(
		Block.box(4.0D, 0.0D, 4.0D, 12.0D, 2.0D, 12.0D),
		Block.box(5.0D, 2.0D, 5.0D, 11.0D, 4.0D, 11.0D),
		Block.box(3.0D, 4.0D, 3.0D, 13.0D, 6.0D, 13.0D),
		Block.box(2.0D, 6.0D, 2.0D, 14.0D, 12.0D, 14.0D),
		Block.box(4.0D, 12.0D, 4.0D, 12.0D, 14.0D, 12.0D),
		Block.box(3.0D, 14.0D, 3.0D, 13.0D, 16.0D, 13.0D));

	public static final VoxelShape POT_3 = Shapes.or(
		Block.box(3.0D, 0.0D, 3.0D, 13.0D, 2.0D, 13.0D),
		Block.box(1.0D, 2.0D, 1.0D, 15.0D, 12.0D, 15.0D),
		Block.box(4.0D, 12.0D, 4.0D, 12.0D, 14.0D, 12.0D),
		Block.box(3.0D, 14.0D, 3.0D, 13.0D, 16.0D, 13.0D));

	//due to wack lighting calculations this block cant be a full block
	public static final VoxelShape ALMOST_FULL = Block.box(0.001D, 0.0D, 0.001D, 15.999D, 16.0D, 15.999D);

	private final VoxelShape shape;

	public LootPotBlock(VoxelShape shape, Properties properties) {
		super(properties);
		this.shape = shape;
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
		return this.shape;
	}

	@Override
	protected VoxelShape getBlockSupportShape(BlockState state, BlockGetter level, BlockPos pos) {
		return this.shape == ALMOST_FULL ? Shapes.block() : super.getBlockSupportShape(state, level, pos);
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		BlockEntity tile = level.getBlockEntity(pos);
		if (tile instanceof LootPotBlockEntity pot) {
			pot.setModelRotationOffset(level.getRandom().nextInt(41) - 20);
			tile.setChanged();
		}
	}

	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		if (!level.isClientSide()) {
			if (level.getBlockEntity(pos) instanceof LootPotBlockEntity pot) {
				InvWrapper wrapper = new InvWrapper(pot);
				if (!stack.isEmpty()) {
					ItemStack prevStack = stack.copy();
					for (int i = 0; i < wrapper.getSlots() && !stack.isEmpty(); i++) {
						stack = wrapper.insertItem(i, stack, false);
					}
					if (stack.isEmpty() || stack.getCount() != prevStack.getCount()) {
						if (!player.isCreative()) {
							player.setItemInHand(hand, stack);
						}
						return ItemInteractionResult.SUCCESS;
					}
				}
			}
		} else {
			return ItemInteractionResult.SUCCESS;
		}
		return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		if (!level.isClientSide() && level.getBlockEntity(pos) instanceof LootPotBlockEntity pot) {
			InvWrapper wrapper = new InvWrapper(pot);
			if (player.isShiftKeyDown()) {
				for (int i = 0; i < wrapper.getSlots(); i++) {
					ItemStack extracted = wrapper.extractItem(i, 1, false);
					if (!extracted.isEmpty()) {
						ItemEntity item = new ItemEntity(level, pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, extracted);
						item.setDeltaMovement(Vec3.ZERO);
						level.addFreshEntity(item);
						return InteractionResult.SUCCESS;
					}
				}
			}
		}
		return super.useWithoutItem(state, level, pos, player, hitResult);
	}

	@Override
	public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
		//TODO
		if (!level.isClientSide()) {
//			LivingEntity entity;
//			if (level.getRandom().nextInt(3) == 0) {
//				if (!level.getFluidState(pos.above()).is(Tags.Fluids.WATER)) {
//					entity = new Termite(level);
//					entity.getAttribute(Termite.SMALL).setBaseValue(1);
//				} else {
//					if (level.getRandom().nextBoolean())
//						entity = new BubblerCrab(level);
//					else
//						entity = new SiltCrab(level);
//				}
//				if (entity != null) {
//					entity.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 0.0F, 0.0F);
//					level.addFreshEntity(entity);
//				}
//			}
		}
		super.playerDestroy(level, player, pos, state, blockEntity, tool);
	}

	@Override
	protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moving) {
		Containers.dropContentsOnDestroy(state, newState, level, pos);
		super.onRemove(state, level, pos, newState, moving);
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new LootPotBlockEntity(pos, state);
	}
}
