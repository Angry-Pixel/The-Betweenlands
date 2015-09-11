package thebetweenlands.event.entity;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import thebetweenlands.client.particle.BLParticle;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.items.loot.ItemRingOfPower;

/**
 * Created by Bart on 8-7-2015.
 */
public class PowerRingHandler {

    @SubscribeEvent
    public void powerRingHandler(LivingHurtEvent event) {
        if(event.source.getEntity() instanceof EntityLivingBase){
            EntityLivingBase attacker = (EntityLivingBase)event.source.getEntity();
            boolean checkForPlayer = true;
            if(attacker.getHeldItem() != null && attacker.getHeldItem().getItem() instanceof ItemRingOfPower){
                event.ammount *= 1.5f;
                checkForPlayer = false;
            }

            if(checkForPlayer && attacker instanceof EntityPlayerMP) {
                ItemStack[] inventory = ((EntityPlayerMP) attacker).inventory.mainInventory;
                ItemStack[] items = new ItemStack[9];
                System.arraycopy(inventory, 0, items, 0, items.length);
                for (ItemStack item:items)
                    if(item != null && item.getItem() != null && item.getItem() instanceof ItemRingOfPower) {
                        event.ammount *= 1.5f;
                        BLParticle.BLACK_FLAME.spawn(((EntityPlayerMP) attacker).worldObj, event.entity.posX + event.entity.width, event.entity.posY + event.entity.height, event.entity.posZ);
                        BLParticle.BLACK_FLAME.spawn(((EntityPlayerMP) attacker).worldObj, event.entity.posX, event.entity.posY + event.entity.height, event.entity.posZ + event.entity.width);
                        BLParticle.BLACK_FLAME.spawn(((EntityPlayerMP) attacker).worldObj, event.entity.posX - event.entity.width, event.entity.posY + event.entity.height, event.entity.posZ);
                        BLParticle.BLACK_FLAME.spawn(((EntityPlayerMP) attacker).worldObj, event.entity.posX, event.entity.posY + event.entity.height, event.entity.posZ - event.entity.width);
                    }
            }
        }
    }
}
