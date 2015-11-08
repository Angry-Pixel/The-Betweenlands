package thebetweenlands.event.player;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import thebetweenlands.items.armor.ItemLurkerSkinArmor;
import thebetweenlands.items.armor.ItemSyrmoriteArmor;

public class ArmorHandler {
	@SubscribeEvent
	public void onEntityOnFire(LivingHurtEvent event) {
		if (event.entityLiving instanceof EntityPlayer) {
			if (event.source == DamageSource.inFire || event.source == DamageSource.onFire || event.source == DamageSource.lava) {
				float damageMultiplier = 1;
				ItemStack[] armor = ((EntityPlayer) event.entityLiving).inventory.armorInventory;
				float reductionAmount = 1F / armor.length;
				for (int i = 0; i < armor.length; i++) {
					if (armor[i] != null && armor[i].getItem() instanceof ItemSyrmoriteArmor) {
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

	@SubscribeEvent
	public void onBreakSpeed(PlayerEvent.BreakSpeed event) {
		if(event.entityPlayer.isInWater()) {
			boolean fullyInWater = event.entityPlayer.worldObj.getBlock((int)event.entityPlayer.posX, (int)(event.entityPlayer.boundingBox.maxY + 0.1D), (int)event.entityPlayer.posZ).getMaterial().isLiquid();
			if(fullyInWater) {
				ItemStack[] armor = event.entityPlayer.inventory.armorInventory;
				int pieces = 0;
				for (int i = 0; i < armor.length; i++) {
					if (armor[i] != null && armor[i].getItem() instanceof ItemLurkerSkinArmor) {
						pieces++;
					}
				}
				event.newSpeed *= (5.0F * (event.entityPlayer.onGround ? 1.0F : 5.0F) / 4.0F * pieces);
			}
		}
	}
}