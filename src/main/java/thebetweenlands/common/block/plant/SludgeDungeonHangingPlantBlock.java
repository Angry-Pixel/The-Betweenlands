package thebetweenlands.common.block.plant;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.api.block.DungeonFogBlock;

import java.util.List;

public class SludgeDungeonHangingPlantBlock extends HangingPlantBlock {

	public SludgeDungeonHangingPlantBlock(Holder<Block> body, Properties properties) {
		super(body, properties);
	}

	@Override
	public boolean canGrow(Level level, BlockPos pos, BlockState state) {
		if (super.canGrow(level, pos, state)) {
			for (BlockPos checkPos : BlockPos.betweenClosed(pos.offset(-6, -4, -6), pos.offset(6, 0, 6))) {
				if (level.isLoaded(checkPos)) {
					BlockState offsetState = level.getBlockState(checkPos);
					Block offsetBlock = offsetState.getBlock();
					if (offsetBlock instanceof DungeonFogBlock fog && fog.isCreatingDungeonFog(level, checkPos, offsetState)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
		tooltip.add(Component.translatable("block.thebetweenlands.mist"));
	}
}
