package thebetweenlands.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.common.block.entity.SiltGlassJarBlockEntity;
import thebetweenlands.common.registries.AdvancementCriteriaRegistry;
import thebetweenlands.common.registries.DataComponentRegistry;
import thebetweenlands.common.registries.ItemRegistry;

import java.util.List;

public class SiltGlassJarBlock extends BaseEntityBlock {

	protected static final VoxelShape SHAPE = Block.box(3.0D, 0.0D, 3.0D, 13.0D, 16.0D, 13.0D);

	public SiltGlassJarBlock(Properties properties) {
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
	protected void attack(BlockState state, Level level, BlockPos pos, Player player) {
		if (!level.isClientSide() && level.getBlockEntity(pos) instanceof SiltGlassJarBlockEntity jar) {
			ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
			if (stack.is(ItemRegistry.WEEDWOOD_FISHING_ROD)) {
				if (stack.getDamageValue() == stack.getMaxDamage())
					return;
				for (int i = 0; i < jar.getContainerSize(); i++) {
					if (!jar.getItem(i).isEmpty()) {
						if (!stack.getOrDefault(DataComponentRegistry.FISHING_ROD_BAIT, false)) {
							stack.set(DataComponentRegistry.FISHING_ROD_BAIT, true);
							jar.setItem(i, ItemStack.EMPTY);
							jar.updateItemCount(level, pos, state);
							if (player instanceof ServerPlayer sp)
								AdvancementCriteriaRegistry.USED_ROD_ON_JAR.get().trigger(sp);
						}
						return;
					}
				}
			}
		}
	}

	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		if (level.getBlockEntity(pos) instanceof SiltGlassJarBlockEntity jar) {
			if (stack.is(ItemRegistry.TINY_SLUDGE_WORM) || stack.is(ItemRegistry.TINY_SLUDGE_WORM_HELPER)) {
				if (!level.isClientSide()) {
					for (int i = 0; i < jar.getContainerSize(); i++) {
						if (jar.getItem(i).isEmpty()) {
							ItemStack jarStack = stack.consumeAndReturn(1, player);
							if (!jarStack.isEmpty()) {
								jar.setItem(i, stack);
								jar.updateItemCount(level, pos, state);
								break;
							}
						}
					}
				}
				return ItemInteractionResult.sidedSuccess(level.isClientSide());
			}
		}
		return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		if (level.getBlockEntity(pos) instanceof SiltGlassJarBlockEntity jar) {
			if(player.isShiftKeyDown()) {
				for(int i = jar.getContainerSize() - 1; i >= 0; i--) {
					if(!jar.getItem(i).isEmpty()) {
						if (!level.isClientSide()) {
							ItemStack extracted = jar.getItem(i);
							ItemEntity item = new ItemEntity(level, pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, extracted);
							item.setDeltaMovement(Vec3.ZERO);
							level.addFreshEntity(item);
							jar.setItem(i, ItemStack.EMPTY);
							jar.updateItemCount(level, pos, state);
						}
						return InteractionResult.sidedSuccess(level.isClientSide());
					}
				}
			}
		}
		return super.useWithoutItem(state, level, pos, player, hitResult);
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
		tooltip.add(Component.translatable(this.getDescriptionId() + ".desc").withStyle(ChatFormatting.GRAY));
		if (stack.has(DataComponentRegistry.WORMS)) {
			tooltip.add(Component.translatable(this.getDescriptionId() + ".worms", stack.get(DataComponentRegistry.WORMS)).withStyle(ChatFormatting.GRAY));
		}
	}

	@Override
	public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
		if (level.getBlockEntity(pos) instanceof SiltGlassJarBlockEntity jar && jar.getItemCount() != 0) {
			ItemStack stack = new ItemStack(this);
			stack.applyComponents(jar.collectComponents());
			return stack;
		}
		return super.getCloneItemStack(state, target, level, pos, player);
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new SiltGlassJarBlockEntity(pos, state);
	}
}
