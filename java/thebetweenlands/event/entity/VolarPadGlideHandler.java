package thebetweenlands.event.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingEvent;
import thebetweenlands.items.misc.ItemVolarkite;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

/**
 * Created by Bart on 25-9-2015.
 */
public class VolarPadGlideHandler {

	@SubscribeEvent
	public void VolarPadHandler(LivingEvent.LivingUpdateEvent event) {
		if (event.entityLiving != null) {
			EntityLivingBase entity = event.entityLiving;
			if (entity.getHeldItem() != null && entity.getHeldItem().getItem() instanceof ItemVolarkite) {
				if (entity instanceof EntityPlayer && !((EntityPlayer) entity).capabilities.isCreativeMode) {
					entity.getHeldItem().setItemDamage(entity.getHeldItem().getItemDamage() + 1);
					if (entity.getHeldItem().getItemDamage() > entity.getHeldItem().getMaxDamage())
						((EntityPlayer) entity).destroyCurrentEquippedItem();
				}
			}
		}
	}
}
