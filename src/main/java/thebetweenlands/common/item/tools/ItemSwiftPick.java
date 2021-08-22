package thebetweenlands.common.item.tools;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Enchantments;
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
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
		if(enchantment == Enchantments.UNBREAKING || enchantment == Enchantments.MENDING)
			return false;
        return true;
    }

	@Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book)  {
		if(EnchantmentHelper.getEnchantments(book).containsKey(Enchantments.UNBREAKING) || EnchantmentHelper.getEnchantments(book).containsKey(Enchantments.MENDING))
			return false;
        return true;
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
