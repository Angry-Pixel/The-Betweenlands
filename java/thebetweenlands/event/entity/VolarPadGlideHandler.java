package thebetweenlands.event.entity;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEvent;
import thebetweenlands.items.ItemVolarPad;

/**
 * Created by Bart on 25-9-2015.
 */
public class VolarPadGlideHandler {

    @SubscribeEvent
    public void VolarPadHandler(LivingEvent.LivingUpdateEvent event) {
        if (event.entityLiving != null) {
            EntityLivingBase entity = event.entityLiving;
            boolean checkForPlayer = true;
            if (entity.getHeldItem() != null && entity.getHeldItem().getItem() instanceof ItemVolarPad) {
                if (entity.motionY < -0.0784000015258789 && entity.fallDistance >= 2.9 && !entity.isSneaking()) {
                    entity.motionY /= 1 + 0.66F;
                    entity.fallDistance = Math.max(2.9F, entity.fallDistance - 2 / 3F);
                    checkForPlayer = false;
                    entity.getHeldItem().damageItem(1, entity);
                }
            }

            if (checkForPlayer && entity instanceof EntityPlayerMP) {
                ItemStack heldItem = ((EntityPlayerMP) entity).getHeldItem();
                if (heldItem != null && heldItem.getItem() instanceof ItemVolarPad) {
                    if (entity.motionY < -0.0784000015258789 && entity.fallDistance >= 2.9 && !entity.isSneaking()) {
                        entity.motionY /= 1 + 0.66F;
                        entity.fallDistance = Math.max(2.9F, entity.fallDistance - 2 / 3F);
                        if(!((EntityPlayerMP) entity).capabilities.isCreativeMode) {
                            entity.getHeldItem().setItemDamage(entity.getHeldItem().getItemDamage() + 1);
                            entity.getHeldItem().damageItem(1, entity);
                        }
                    }
                }
            }
        }
    }
}
