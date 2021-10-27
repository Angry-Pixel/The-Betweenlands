package thebetweenlands.common.item.misc;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemNameTag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import thebetweenlands.api.item.IRenamableItem;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.proxy.CommonProxy;

public class ItemBLNameTag extends ItemNameTag implements IRenamableItem {

	public ItemBLNameTag() {
		setCreativeTab(BLCreativeTabs.ITEMS);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		if(player.isSneaking()) {
			if (!world.isRemote) {
				player.openGui(TheBetweenlands.instance, CommonProxy.GUI_ITEM_RENAMING, world, hand == EnumHand.MAIN_HAND ? 0 : 1, 0, 0);
			}
		}
		return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
	}
}