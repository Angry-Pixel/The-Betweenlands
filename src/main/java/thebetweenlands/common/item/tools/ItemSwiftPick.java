package thebetweenlands.common.item.tools;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import thebetweenlands.api.item.CorrosionHelper;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.item.BLMaterialRegistry;

public class ItemSwiftPick extends ItemBLPickaxe {
	public ItemSwiftPick() {
		super(BLMaterialRegistry.TOOL_VALONITE);
		this.setCreativeTab(BLCreativeTabs.SPECIALS);
	}

	@Override
	public float getDestroySpeed(ItemStack stack, IBlockState state) {
		float digSpeed = this.efficiency;
		if (this.isEffective(stack, state)) {
			digSpeed = 100.0F;
		}
		return CorrosionHelper.getDestroySpeed(digSpeed, stack, state);
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
	
	@Override
	public boolean isRepairableByAnimator(ItemStack stack) {
		return false;
	}
	
	@Override
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.EPIC;
	}
}
