package thebetweenlands.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class SludgeDungeonPlantBlock extends PlantBlock {
	public SludgeDungeonPlantBlock(Properties properties) {
		super(properties);
	}

	@Override
	public boolean isFarmable(Level level, BlockPos pos, BlockState state) {
		BlockState soil = level.getBlockState(pos.below());
		if (soil.getBlock() instanceof DugSoilBlock) {
			return soil.getValue(DugSoilBlock.FOGGED);
		}
		return false;
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
		tooltip.add(Component.translatable("block.thebetweenlands.mist"));
	}
}
