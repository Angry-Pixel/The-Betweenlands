package thebetweenlands.common.item.misc;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemMapBase;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.config.BetweenlandsConfig;

public class ItemEmptyAmateMap extends ItemMapBase {

    public ItemEmptyAmateMap() {
        setCreativeTab(BLCreativeTabs.SPECIALS);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack itemstack1 = playerIn.getHeldItem(handIn);
        if (worldIn.provider.getDimension() == BetweenlandsConfig.WORLD_AND_DIMENSION.dimensionId) {
            ItemStack itemstack = ItemAmateMap.setupNewMap(worldIn, playerIn.posX, playerIn.posZ, (byte) 4, true, false);
            itemstack1.shrink(1);

            if (itemstack1.isEmpty()) {
                return new ActionResult<>(EnumActionResult.SUCCESS, itemstack);
            } else {
                if (!playerIn.inventory.addItemStackToInventory(itemstack.copy())) {
                    playerIn.dropItem(itemstack, false);
                }

                playerIn.addStat(StatList.getObjectUseStats(this));
                return new ActionResult<>(EnumActionResult.SUCCESS, itemstack1);
            }
        } else {
            if (!worldIn.isRemote) {
                playerIn.sendStatusMessage(new TextComponentTranslation("chat.amate_map.invalid").setStyle(new Style().setColor(TextFormatting.RED)), false);
            }
            return new ActionResult<>(EnumActionResult.FAIL, itemstack1);
        }
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.UNCOMMON;
    }
}
