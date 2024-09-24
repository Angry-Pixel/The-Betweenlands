package thebetweenlands.common.item.tool;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.block.state.BlockState;

public class SwiftPickItem extends PickaxeItem {
	public SwiftPickItem(Tier tier, Properties properties) {
		super(tier, properties);
	}

	//TODO check logic
	@Override
	public float getDestroySpeed(ItemStack stack, BlockState state) {
		Tool tool = stack.get(DataComponents.TOOL);
		return tool != null && tool.isCorrectForDrops(state) ? 100.0F : 1.0F;
	}

	@Override
	public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
		return false;
	}
}
