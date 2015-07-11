package thebetweenlands.items.loot;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

/**
 * Created by Bart on 8-7-2015.
 */
public class ItemVoodooDoll extends Item {

    public ItemVoodooDoll() {
        this.maxStackSize = 1;
        this.setUnlocalizedName("thebetweenlands.voodooDoll");
        setTextureName("thebetweenlands:voodooDoll");
    }


    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if(!world.isRemote){
            List<EntityLivingBase> living = world.getEntitiesWithinAABB(EntityLivingBase.class, AxisAlignedBB.getBoundingBox(player.posX, player.posY, player.posZ, player.posX, player.posY, player.posZ).expand(2.5, 2.5, 2.5));
            for(EntityLivingBase entity:living){
                if(entity != player) {
                    entity.attackEntityFrom(DamageSource.magic, 20f);
                    player.inventory.consumeInventoryItem(stack.getItem());
                }
            }
            return stack;
        }
        return stack;
    }
}
