package thebetweenlands.common.item.tools;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.util.TranslationHelper;

import javax.annotation.Nullable;
import java.util.List;

public class ItemSyrmoriteBucketSolidRubber extends Item {
	public ItemSyrmoriteBucketSolidRubber() {
		this.setCreativeTab(BLCreativeTabs.GEARS);
		this.setMaxStackSize(1);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		tooltip.add(TranslationHelper.translateToLocal("tooltip.bl.rubber_bucket"));
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		player.addStat(StatList.getObjectUseStats(this));
		ItemStack bucket = new ItemStack(ItemRegistry.BL_BUCKET, 1, 1);
		bucket.getItem().onCreated(bucket, world, player);
		player.setHeldItem(hand, bucket);
		ItemStack rubber = EnumItemMisc.RUBBER_BALL.create(3);
		if (player.addItemStackToInventory(rubber))
			player.dropItem(rubber, false);
		return ActionResult.newResult(EnumActionResult.SUCCESS, player.getHeldItem(hand));
	}
}
