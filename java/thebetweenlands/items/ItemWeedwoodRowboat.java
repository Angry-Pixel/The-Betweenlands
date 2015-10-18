package thebetweenlands.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import thebetweenlands.entities.rowboat.EntityWeedwoodRowboat;

public class ItemWeedwoodRowboat extends Item {
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
}
