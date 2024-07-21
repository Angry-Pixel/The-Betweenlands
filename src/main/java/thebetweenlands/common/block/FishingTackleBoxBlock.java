package thebetweenlands.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.entity.FishingTackleBoxBlockEntity;
import thebetweenlands.common.registries.BlockEntityRegistry;
import thebetweenlands.common.registries.SoundRegistry;

import java.util.List;

public class FishingTackleBoxBlock extends HorizontalBaseEntityBlock {

	public static final BooleanProperty OPEN = BooleanProperty.create("open");

	public FishingTackleBoxBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.getStateDefinition().any().setValue(OPEN, false));
	}

	private boolean isBoxSatOn(Level level, BlockPos pos) {
		return !level.getEntitiesOfClass(FishingTackleBoxSeat.class, new AABB(pos), EntitySelector.ENTITY_STILL_ALIVE).isEmpty();
	}

	@Override
	protected void attack(BlockState state, Level level, BlockPos pos, Player player) {
		if (!level.isClientSide()) {
			if (level.getBlockEntity(pos) instanceof FishingTackleBoxBlockEntity box) {
				if (!this.isBoxSatOn(level, pos)) {
					level.playSound(null, pos, state.getValue(OPEN) ? SoundRegistry.FISHING_TACKLE_BOX_CLOSE.get() : SoundRegistry.FISHING_TACKLE_BOX_OPEN.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
					level.setBlockAndUpdate(pos, state.cycle(OPEN));
					level.sendBlockUpdated(pos, state, state, 2);
					box.setChanged();
				}
			}
		}
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		if (!level.isClientSide()) {
			if (level.getBlockEntity(pos) instanceof FishingTackleBoxBlockEntity box) {
				if (!state.getValue(OPEN) && !this.isBoxSatOn(level, pos)) {
					if (level.isEmptyBlock(pos.above()) && level.isEmptyBlock(pos.above(2)) && hitResult.getDirection() == Direction.UP)
						box.seatPlayer(player, level, pos);
				}
				if (state.getValue(OPEN))
					player.openMenu(box);
			}
		}
		return InteractionResult.sidedSuccess(level.isClientSide());
	}

	@Override
	public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
		BlockEntity blockentity = level.getBlockEntity(pos);
		if (blockentity instanceof ShulkerBoxBlockEntity shulkerboxblockentity) {
			if (!level.isClientSide() && player.isCreative() && !shulkerboxblockentity.isEmpty()) {
				ItemStack itemstack = new ItemStack(this);
				itemstack.applyComponents(blockentity.collectComponents());
				ItemEntity itementity = new ItemEntity(level, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, itemstack);
				itementity.setDefaultPickUpDelay();
				level.addFreshEntity(itementity);
			} else {
				shulkerboxblockentity.unpackLootTable(player);
			}
		}

		return super.playerWillDestroy(level, pos, state, player);
	}

	@Override
	protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
		BlockEntity blockentity = params.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
		if (blockentity instanceof FishingTackleBoxBlockEntity box) {
			params = params.withDynamicDrop(TheBetweenlands.prefix("contents"), consumer -> {
				for (int i = 0; i < box.getContainerSize(); i++) {
					consumer.accept(box.getItem(i));
				}
			});
		}

		return super.getDrops(state, params);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder.add(OPEN));
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new FishingTackleBoxBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return createTickerHelper(type, BlockEntityRegistry.FISHING_TACKLE_BOX.get(), FishingTackleBoxBlockEntity::tick);
	}
}
