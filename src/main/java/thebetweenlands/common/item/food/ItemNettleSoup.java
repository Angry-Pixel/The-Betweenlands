package thebetweenlands.common.item.food;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import thebetweenlands.common.item.misc.ItemGeneric;

public class ItemNettleSoup extends ItemBLFood {
    public ItemNettleSoup() {
        super(10, 8.4F, false, "nettleSoup");
    }

    @Override
    public ItemStack getContainerItem(ItemStack stack) {
        return ItemGeneric.createStack(ItemGeneric.EnumItemGeneric.WEEDWOOD_BOWL);
    }

    @Override
    protected void onFoodEaten(ItemStack stack, World worldIn, EntityPlayer player) {
        stack.stackSize--;
        player.getFoodStats().addStats(10, 8.4F);
        //worldIn.playSound(player, player.getPosition(), SoundEvents.slu, 0.5F, worldIn.rand.nextFloat() * 0.1F + 0.9F);

        if (stack.stackSize != 0)
            player.inventory.addItemStackToInventory(getContainerItem(stack));
    }
}
