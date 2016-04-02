package thebetweenlands.common.item.tools;

import net.minecraft.item.Item;

/**
 * Created by Bart on 03/04/2016.
 */
public class ItemNet extends Item {

    public ItemNet() {
        this.setUnlocalizedName("thebetweenlands.net");
        this.maxStackSize = 1;
        this.setMaxDamage(32);
    }
    /*
    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer playerIn, EntityLivingBase target, EnumHand hand) {
        if (target instanceof EntityFirefly || target instanceof EntityGecko) {
            ItemStack itemStack1;
            if (target instanceof EntityFirefly)
                itemStack1 = new ItemStack(ItemRegistry.fireFly);
            else
                itemStack1 = new ItemStack(ItemRegistry.gecko);
            if (playerIn.getHeldItem(hand) != null && playerIn.getHeldItem(hand).getItem() == this && !playerIn.worldObj.isRemote) {
                if (((EntityLiving) target).getCustomNameTag() != null)
                    itemStack1.setStackDisplayName(((EntityLiving) target).getCustomNameTag());
                playerIn.worldObj.spawnEntityInWorld(new EntityItem(playerIn.worldObj, playerIn.posX, playerIn.posY, playerIn.posZ, itemStack1));
                target.setDead();
                stack.damageItem(1, playerIn);
            }
            playerIn.swingArm(hand);
        }
        return true;
    }*/
}
