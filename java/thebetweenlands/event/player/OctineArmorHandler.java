package thebetweenlands.event.player;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import thebetweenlands.items.OctineArmor;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class OctineArmorHandler {
	@SubscribeEvent
	public void onEntityOnFire(LivingHurtEvent event) {
		if (event.entityLiving instanceof EntityPlayer) {
			if (event.source == DamageSource.inFire || event.source == DamageSource.onFire || event.source == DamageSource.lava) {
				float damageMultiplier = 1;
				ItemStack[] armor = ((EntityPlayer) event.entityLiving).inventory.armorInventory;
				float reductionAmount = 1F / armor.length;
				for (int i = 0; i < armor.length; i++) {
					if (armor[i] != null && armor[i].getItem() instanceof OctineArmor) {
						damageMultiplier -= reductionAmount;
					}
				}
				if (damageMultiplier < 0.001F) {
					event.setCanceled(true);
					event.entityLiving.extinguish();
				} else {
					event.ammount *= damageMultiplier;
				}
			}
		}
	}
}