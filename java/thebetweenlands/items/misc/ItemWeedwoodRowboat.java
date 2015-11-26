package thebetweenlands.items.misc;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import thebetweenlands.entities.rowboat.EntityWeedwoodRowboat;
import thebetweenlands.manual.IManualEntryItem;

public class ItemWeedwoodRowboat extends Item implements IManualEntryItem{
    public ItemWeedwoodRowboat() {
        maxStackSize = 1;
        setUnlocalizedName("thebetweenlands.weedwoodRowboat");
        setTextureName("thebetweenlands:weedwoodRowboat");
    }

    @Override
    public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer player) {
    	if (!world.isRemote) {
            world.spawnEntityInWorld(new EntityWeedwoodRowboat(world, player.posX, player.posY + 2, player.posZ));
    	}
        if (!player.capabilities.isCreativeMode) {
            player.destroyCurrentEquippedItem();
        }
        return is;
    }

    @Override
    public String manualName(int meta) {
        return "weedwoodRowboat";
    }

    @Override
    public Item getItem() {
        return this;
    }

    @Override
    public int[] recipeType(int meta) {
        return new int[]{2};
    }

    @Override
    public int metas() {
        return 0;
    }
}
