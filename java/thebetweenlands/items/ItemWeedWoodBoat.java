package thebetweenlands.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import thebetweenlands.entities.EntityWeedWoodBoat;

/**
 * Created by Bart on 3-10-2015.
 */
public class ItemWeedWoodBoat extends Item {
    public ItemWeedWoodBoat() {
        maxStackSize = 1;
        setUnlocalizedName("thebetweenlands.weedwoodboat");
    }

    @Override
    public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer player) {
        world.spawnEntityInWorld(new EntityWeedWoodBoat(world, player.posX, player.posY + 2, player.posZ));
        if (!player.capabilities.isCreativeMode)
            player.destroyCurrentEquippedItem();
        return super.onItemRightClick(is, world, player);
    }
}
