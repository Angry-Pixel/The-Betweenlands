package thebetweenlands.common.item.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public class DriedSwampReedItem extends Item {
	public DriedSwampReedItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Level level = context.getLevel();
		BlockPos blockpos = context.getClickedPos();
		BlockPos blockpos1 = blockpos.relative(context.getClickedFace());
		if (BoneMealItem.applyBonemeal(context.getItemInHand(), level, blockpos, context.getPlayer())) {
			if (!level.isClientSide) {
				context.getPlayer().gameEvent(GameEvent.ITEM_INTERACT_FINISH);
				level.levelEvent(1505, blockpos, 15);
			}

			return InteractionResult.sidedSuccess(level.isClientSide);
		} else {
			BlockState blockstate = level.getBlockState(blockpos);
			boolean flag = blockstate.isFaceSturdy(level, blockpos, context.getClickedFace());
			if (flag && BoneMealItem.growWaterPlant(context.getItemInHand(), level, blockpos1, context.getClickedFace())) {
				if (!level.isClientSide) {
					context.getPlayer().gameEvent(GameEvent.ITEM_INTERACT_FINISH);
					level.levelEvent(1505, blockpos1, 15);
				}

				return InteractionResult.sidedSuccess(level.isClientSide);
			} else {
				return InteractionResult.PASS;
			}
		}
	}
}
