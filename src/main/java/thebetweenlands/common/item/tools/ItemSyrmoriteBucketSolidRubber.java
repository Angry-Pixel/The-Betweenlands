package thebetweenlands.common.item.tools;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemHandlerHelper;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.registries.ItemRegistry;

public class ItemSyrmoriteBucketSolidRubber extends Item {
	public ItemSyrmoriteBucketSolidRubber() {
		//this.setCreativeTab(null);
		this.setMaxStackSize(1);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {

		player.addStat(StatList.getObjectUseStats(this));
		player.setHeldItem(hand, ItemStack.EMPTY);
		ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(ItemRegistry.SYRMORITE_BUCKET), player.inventory.currentItem);
		ItemHandlerHelper.giveItemToPlayer(player, EnumItemMisc.RUBBER_BALL.create(1), player.inventory.currentItem + 1);
		return ActionResult.newResult(EnumActionResult.SUCCESS, player.getHeldItem(hand));
	}
}
