package thebetweenlands.common.item.farming;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

public class ItemAspectrusSeeds extends Item {
	public ItemAspectrusSeeds() {
		this.setCreativeTab(null);
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
		tooltip.add(TextFormatting.RED + "Not yet implemented!");
	}
}
