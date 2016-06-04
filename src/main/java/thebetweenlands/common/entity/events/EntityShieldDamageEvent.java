package thebetweenlands.common.entity.events;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thebetweenlands.common.item.tools.ItemBLShield;

public class EntityShieldDamageEvent {

	@SubscribeEvent
	public void onEntityShielded(LivingHurtEvent event) {
		if (event.getEntityLiving() instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) event.getEntityLiving();
			if (event.getAmount() >= 3.0F && player.getActiveItemStack() != null && player.getActiveItemStack().getItem() instanceof ItemBLShield) {
				int i = 1 + MathHelper.floor_float(event.getAmount());
				System.out.println("uhm ");
				((ItemBLShield) player.getActiveItemStack().getItem()).damageShield(i, player.getActiveItemStack(), player);
				if (player.getActiveItemStack().stackSize <= 0) {
					EnumHand enumhand = player.getActiveHand();
					net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(player, player.getActiveItemStack(), enumhand);

					if (enumhand == EnumHand.MAIN_HAND)
						player.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, null);
					else
						player.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, null);
					
					player.playSound(SoundEvents.ITEM_SHIELD_BREAK, 0.8F, 0.8F + player.worldObj.rand.nextFloat() * 0.4F);
				}
				event.setCanceled(true);
			}
		}
	}
}
