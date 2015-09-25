package thebetweenlands.items;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Created by Bart on 25-9-2015.
 */
public class ItemVolarPad extends Item {
    public ItemVolarPad(){
        maxStackSize = 1;
        setUnlocalizedName("thebetweenlands.volarkite");
    }

}
