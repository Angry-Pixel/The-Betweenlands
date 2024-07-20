package thebetweenlands.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.common.block.entity.SimulacrumBlockEntity;
import thebetweenlands.common.registries.BlockEntityRegistry;

import java.util.List;

public class SimulacrumBlock extends HorizontalBaseEntityBlock {

	public static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D);

	public SimulacrumBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		if(level.getBlockEntity(pos) instanceof SimulacrumBlockEntity simulacrum) {
			simulacrum.setEffect(SimulacrumBlockEntity.Effect.byId(NBTHelper.getStackNBTSafe(stack).getInteger("simulacrumEffectId")));
			simulacrum.setActive(level, pos, state, true);
			if(stack.has(DataComponents.CUSTOM_NAME)) {
				simulacrum.setCustomName(stack.getDisplayName());
			}
		}
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		if(player.isCreative() && player.isCrouching()) {

			if(!level.isClientSide()) {
				if(level.getBlockEntity(pos) instanceof SimulacrumBlockEntity simulacrum) {
					SimulacrumBlockEntity.Effect nextEffect = SimulacrumBlockEntity.Effect.values()[(simulacrum.getEffect().ordinal() + 1) % SimulacrumBlockEntity.Effect.values().length];

					simulacrum.setEffect(nextEffect);

					player.displayClientMessage(Component.translatable("chat.simulacrum.changed_effect", Component.translatable("tooltip.bl.simulacrum.effect." + nextEffect.name)), true);
				}
			}

			return InteractionResult.sidedSuccess(level.isClientSide());
		}
		return super.useWithoutItem(state, level, pos, player, hitResult);
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new SimulacrumBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return createTickerHelper(type, BlockEntityRegistry.SIMULACRUM.get(), SimulacrumBlockEntity::tick);
	}

	@Override
	public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
		ItemStack stack = new ItemStack(this);

		if(level.getBlockEntity(pos) instanceof SimulacrumBlockEntity simulacrum) {
			CompoundTag nbt = NBTHelper.getStackNBTSafe(stack);
			nbt.putInt("simulacrumEffectId", simulacrum.getEffect().id);
		}

		return stack;
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
		if (flag.isCreative()) {
			tooltip.add(Component.translatable("tooltip.bl.simulacrum.effect", Component.translatable("tooltip.bl.simulacrum.effect." + SimulacrumBlockEntity.Effect.byId(NBTHelper.getStackNBTSafe(stack).getInteger("simulacrumEffectId")).name)));
		}
	}
}
