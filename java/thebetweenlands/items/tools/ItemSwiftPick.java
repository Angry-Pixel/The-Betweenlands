package thebetweenlands.items.tools;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeHooks;
import thebetweenlands.utils.CorrodibleItemHelper;

public class ItemSwiftPick extends ItemPickaxeBL {
	public ItemSwiftPick(ToolMaterial material) {
		super(material);
	}

	@Override
	public float getDigSpeed(ItemStack stack, Block block, int meta) {
		float digSpeed = super.getDigSpeed(stack, block, meta);
		if (ForgeHooks.isToolEffective(stack, block, meta)) {
			digSpeed = 100.0F;
		}
		return CorrodibleItemHelper.getDigSpeed(digSpeed, stack, block, meta);
	}
}
