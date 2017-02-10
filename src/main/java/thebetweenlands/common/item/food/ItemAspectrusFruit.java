package thebetweenlands.common.item.food;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

public class ItemAspectrusFruit extends Item {
	public ItemAspectrusFruit() {
		this.setCreativeTab(null);
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
		tooltip.add(TextFormatting.RED + "Not yet implemented!");
	}
}
