package thebetweenlands.common.item.misc;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.proxy.CommonProxy;

public class ItemSilkBundle extends Item {
	
    public ItemSilkBundle() {
        this.setMaxStackSize(1);
        this.setCreativeTab(BLCreativeTabs.GEARS);
        this.setMaxDamage(1);
        this.setNoRepair();
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 1;
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return false;
    }
    
    @Override
    public boolean isRepairable() {
    	return false;
    }
    
    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
    	return false;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (!world.isRemote && player.isSneaking())
            player.openGui(TheBetweenlands.instance, CommonProxy.GUI_SILK_BUNDLE, world, 0, 0, 0);

        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
    }
}
