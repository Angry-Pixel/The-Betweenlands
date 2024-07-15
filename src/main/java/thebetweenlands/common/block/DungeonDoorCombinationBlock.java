package thebetweenlands.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import thebetweenlands.common.block.entity.DungeonDoorCombinationBlockEntity;
import thebetweenlands.common.registries.BlockEntityRegistry;

import javax.annotation.Nullable;

public class DungeonDoorCombinationBlock extends HorizontalBaseEntityBlock {

	public DungeonDoorCombinationBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult result) {
		if (player.isCreative()) {
			if (!level.isClientSide()) {
				if (level.getBlockEntity(pos) instanceof DungeonDoorCombinationBlockEntity combo && result.getDirection() == state.getValue(FACING)) {
					double hitY = result.getLocation().y();
					if (hitY >= 0.0625F && hitY < 0.375F)
						combo.cycleBottomState();
					if (hitY >= 0.375F && hitY < 0.625F)
						combo.cycleMidState();
					if (hitY >= 0.625F && hitY <= 0.9375F)
						combo.cycleTopState();
					level.playSound(null, pos, SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.BLOCKS, 1.0F, 1.0F);
					level.sendBlockUpdated(pos, state, state, 3);
					return InteractionResult.SUCCESS;
				}
			} else {
				return InteractionResult.SUCCESS;
			}
		}
		return InteractionResult.PASS;
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new DungeonDoorCombinationBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return createTickerHelper(type, BlockEntityRegistry.DUNGEON_DOOR_COMBINATION.get(), DungeonDoorCombinationBlockEntity::tick);
	}
}
