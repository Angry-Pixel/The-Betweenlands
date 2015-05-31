package thebetweenlands.event.player;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import thebetweenlands.items.BLItemRegistry;

public class OctineArmorHandler {
	@SubscribeEvent
	public void onEntityOnFire(LivingHurtEvent event) {
		if (event.entityLiving instanceof EntityPlayer) {
			if(event.source == DamageSource.inFire || event.source == DamageSource.onFire || event.source == DamageSource.lava) {
				float damage = 1;
				
				ItemStack boots = ((EntityPlayer) event.entityLiving).inventory.armorInventory[0];
				ItemStack legs = ((EntityPlayer) event.entityLiving).inventory.armorInventory[1];
				ItemStack chest = ((EntityPlayer) event.entityLiving).inventory.armorInventory[2];
				ItemStack helm = ((EntityPlayer) event.entityLiving).inventory.armorInventory[3];
				
				if (boots != null && boots.getItem() == BLItemRegistry.octineBoots)
					damage -= 0.25D;
				if (legs != null && legs.getItem() == BLItemRegistry.octineLeggings)
					damage -= 0.25D;
				if (chest != null && chest.getItem() == BLItemRegistry.octineChestplate)
					damage -= 0.25D;
				if (helm != null && helm.getItem() == BLItemRegistry.octineHelmet)
					damage -= 0.25D;
				
				if (event.ammount * damage <= 0) {
					event.setCanceled(true);
					event.entityLiving.extinguish();
				}
				else
					event.entityLiving.attackEntityFrom(event.source, event.ammount * damage);
				System.out.println("Damage: " + event.ammount * damage);
			}
		}
	}
}