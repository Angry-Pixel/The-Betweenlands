package thebetweenlands.common.item.tools;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.item.BLMaterialRegistry;
import thebetweenlands.common.item.corrosion.CorrosionHelper;

public class ItemSwiftPick extends ItemBLPickaxe {
	public ItemSwiftPick() {
		super(BLMaterialRegistry.TOOL_VALONITE);
		this.setCreativeTab(BLCreativeTabs.ITEMS);
	}

	@Override
	public float getStrVsBlock(ItemStack stack, IBlockState state) {
		float digSpeed = this.efficiencyOnProperMaterial;
		if (this.isEffective(stack, state)) {
			digSpeed = 100.0F;
		}
		return CorrosionHelper.getStrVsBlock(digSpeed, stack, state); 
	}

	private boolean isEffective(ItemStack stack, IBlockState state) {
		if(state.getMaterial() == Material.IRON || state.getMaterial() == Material.ANVIL || state.getMaterial() == Material.ROCK) {
			return true;
		}
		for(String type : stack.getItem().getToolClasses(stack)) {
			if(state.getBlock().isToolEffective(type, state)) {
				return true;
			}
		}
		return false;
	}
}
