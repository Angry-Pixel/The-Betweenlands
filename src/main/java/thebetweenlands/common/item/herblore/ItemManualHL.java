package thebetweenlands.common.item.herblore;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import thebetweenlands.api.aspect.DiscoveryContainer;
import thebetweenlands.api.item.IDiscoveryProvider;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.proxy.CommonProxy;

public class ItemManualHL extends Item implements IDiscoveryProvider<ItemStack>{
    public ItemManualHL() {
        setMaxStackSize(1);
        this.setCreativeTab(BLCreativeTabs.ITEMS);
    }
    
    @Override
    public DiscoveryContainer<ItemStack> getContainer(ItemStack stack) {
        if(stack != null) {
            if(stack.getTagCompound() == null)
                stack.setTagCompound(new NBTTagCompound());
            return new DiscoveryContainer<ItemStack>(this, stack).updateFromNBT(stack.getTagCompound(), false);
        }
        return null;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick( World world, EntityPlayer player, EnumHand hand) {
        player.openGui(TheBetweenlands.instance, CommonProxy.GUI_HL, world, hand == EnumHand.MAIN_HAND ? 0 : 1, 0, 0);
        return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
    }

    @Override
    public void saveContainer(ItemStack stack, DiscoveryContainer<ItemStack> container) {
        if(stack != null) {
            if(stack.getTagCompound() == null)
                stack.setTagCompound(new NBTTagCompound());
            stack.setTagCompound(container.writeToNBT(stack.getTagCompound()));
        }
    }
}
