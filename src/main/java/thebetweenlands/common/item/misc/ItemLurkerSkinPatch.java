package thebetweenlands.common.item.misc;

import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.tab.BLCreativeTabs;

public class ItemLurkerSkinPatch extends Item {
	public ItemLurkerSkinPatch() {
		this.setCreativeTab(BLCreativeTabs.ITEMS);
		this.setMaxStackSize(16);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		tooltip.add(I18n.format("tooltip.bl.lurker_skin_patch"));
	}
}
